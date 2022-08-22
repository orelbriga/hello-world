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
          volumes:
          - name: docker-sock
            hostPath:
              path: /var/run/docker.sock 
              '''

        }
    }
    environment {
        DOCKERHUB_CREDENTIALS=credentials('dockerhub')
    }

    stages {
        stage('Test and Build the app') {
            steps {
                container('gradle') {
                    sh '''gradle clean build'''
                    archiveArtifacts artifacts: 'build/libs/hello-world-0.0.1-SNAPSHOT.jar', onlyIfSuccessful: true
                }
            }
        }
        stage('Build Docker Image') {
            steps {
                container('docker') {
                    sh 'docker build -t orelbriga/hello-world-app:latest .'

                }
            }
        }

        stage('Login') {

            steps {
                container('docker') {
                    sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
                }
            }
        }

        stage('Push') {

            steps {
                container('docker') {
                    sh 'docker push orelbriga/hello-world-app:latest'
            }
        }
    }
}
}

