#!groovy
pipeline {
    agent {
        kubernetes {
            yaml '''
        apiVersion: v1
        kind: Pod
        spec:
          containers:
          - name: gradle
            image: gradle:7.5.1-jdk11-jammy
            command:
            - cat
            tty: true
          - name: docker
            image: docker:latest
            command:
            - cat
            tty: true
            volumeMounts:
             - mountPath: /var/run/docker.sock
               name: docker-sock
            securityContext:
              privileged: true
              runAsUser: 0             
          volumes:
          - name: docker-sock
            hostPath:
              path: /var/run/docker.sock 
              '''

        }
    }
    environment {
        REPOSITORY = "orelbriga/hello-world-app"  // Image location
        registryCredential = 'dockerhub'   // The credentials ID on jenkins
        dockerImage = ''
        POD_STATE = ''    // Validation after deploy stage
        APP_POD_NAME = ''  // Validation after deploy stage
    }

    stages {
        stage('Test and build the app') {
            steps {
                container('gradle') {
                    echo "compiling code + running  tests + creating jar: "
                    sh "gradle clean build"
                    echo "saving jar as an artifact:"
                    archiveArtifacts artifacts: 'build/libs/hello-world-0.0.1-SNAPSHOT.jar', onlyIfSuccessful: true
                }
            }
        }
        stage('Build docker image') {
            steps {
                container('docker') {
                    script {
                        echo "building docker image:"
                        dockerImage = docker.build REPOSITORY
                    }
                }
            }
        }
        stage('Push image to registry') {
            steps {
                container('docker') {
                    script {
                        echo "Login to private repo on dockerhub:"
                        docker.withRegistry('', registryCredential) {
                            echo "push the new image to repo with the build number as a tag: "
                            dockerImage.push("$BUILD_NUMBER")
                        }
                    }
                }
            }
        }
        stage('Deploy app to k8s') {
            steps {
                container('docker') {
                    script {
                        echo "deploy the app to the k8s cluster using yaml files - with kube-config as an approval: "
                        kubernetesDeploy(configs: 'config.yaml', kubeconfigId: 'k8sconfig')
                    }
                }
            }
        }
        stage('Validate App is running') {
            steps {
                container('docker') {
                    script {
                        withKubeConfig([credentialsId: 'secret-jenkins']) {
                            echo "installing kubectl on the container to check the application's pod state + logs:"
                            sh '''wget "https://storage.googleapis.com/kubernetes-release/release/v1.24.1/bin/linux/amd64/kubectl"
                              chmod +x ./kubectl
                              sleep 10s
                              POD_STATE=$(./kubectl get po | grep hello-world-app-$BUILD_NUMBER-* | awk \'{print $3; exit}\')
                              APP_POD_NAME=$(./kubectl get po | grep hello-world-app-$BUILD_NUMBER-* | awk \'{print $1; exit}\')
                              ./kubectl logs $APP_POD_NAME | tee $APP_POD_NAME.log '''
                            echo "archiving the app log as an artifact:"
                            archiveArtifacts artifacts: 'hello-world-app-*.log', onlyIfSuccessful: true
                        }
                    }
                }
            }
        }
    }
}
