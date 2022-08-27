#!groovy
pipeline {
    agent {
        kubernetes {
            yamlFile 'agent-pod.yaml'
        }
    }
    environment {
        REPOSITORY = "orelbriga/hello-world-app"  // Images location
        registryCredential = 'dockerhub'   // The credentials ID on jenkins
        dockerImage = ''
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
        stage('Build docker image and push to registry') {
            steps {
                container('docker') {
                    script {
                        echo "building docker image:"
                        dockerImage = docker.build REPOSITORY
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
                            sh ''' wget "https://storage.googleapis.com/kubernetes-release/release/v1.24.1/bin/linux/amd64/kubectl"
                               chmod +x ./kubectl
                               sleep 10s  '''

                            sh ''' APP_POD_NAME=$(./kubectl get po | grep hello-world-app-$BUILD_NUMBER-* | awk \'{print $1; exit}\')
                                ./kubectl logs $APP_POD_NAME | tee $APP_POD_NAME.log  '''
                            archiveArtifacts artifacts: 'hello-world-app-*.log'

                            def POD_STATE = sh(
                                    script: './kubectl get po | grep hello-world-app-$BUILD_NUMBER-* | awk \'{print $3; exit}\'', returnStdout: true
                            ).trim()
                            if (POD_STATE != "Running") {
                                error("Application pod ${APP_POD_NAME} is not healthy, check app log")
                            }
                            else {
                                echo "Pod ${APP_POD_NAME} state is ${POD_STATE}"
                            }
                        }
                    }
                }
            }
        }
    }
}