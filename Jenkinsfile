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
        REPOSITORY = "orelbriga/hello-world-app"
        registryCredential = 'dockerhub'   // The credentials ID on jenkins
        dockerImage = ''
    }

    stages {
        stage('Test and build the app') {
            steps {
                container('gradle') {
                    sh '''gradle clean build'''
                    archiveArtifacts artifacts: 'build/libs/hello-world-0.0.1-SNAPSHOT.jar', onlyIfSuccessful: true
                }
            }
        }
        stage('Build docker image') {
            steps {
                container('docker') {
                    script {
                        dockerImage = docker.build REPOSITORY
                    }
                }
            }
        }
        stage('Push image to registry') {
            steps {
                container('docker') {
                    script {
                        docker.withRegistry('', registryCredential) {
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
                        kubernetesDeploy(configs: 'config.yaml', kubeconfigId: 'k8sconfig')
                    }
                }
            }
        }
        stage('Validate App is running') {
            steps {
                withKubeConfig([credentialsId: 'kubectl']) {
                    sh 'kubectl get pods'
                }
            }
        }
    }
}
