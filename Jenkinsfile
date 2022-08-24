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
            image: gradle:7.5.1-jdk11-alpine
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
        registry = "orelbriga/hello-world-app"
        registryCredential = 'dockerhub'
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
                        dockerImage = docker.build registry
                    }
                }
            }
        }
        stage('Push image to registry') {
            steps {
                container('docker') {
                    script {
                        docker.withRegistry( '', registryCredential ) {
                            dockerImage.push("latest")
                        }
                    }
                }
            }
        }
        stage('Deploy app to k8s') {
            steps {
                script{
                    kubernetesDeploy (configs: 'config.yaml', kubeconfigId: 'k8sconfig')
                }
            }
        }
    }
}

