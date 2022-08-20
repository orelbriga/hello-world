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
              path: /var/run/docker.sock '''
        }
    }
    stages {
        stage('Test and Build the app') {
            steps {
                container('gradle') {
                    sh '''gradle clean build'''
                }
            }
        }
        stage ('Build & Push docker image') {
            steps {
                    sh 'docker build -t orelbriga/hello-world-app:latest .'
                }
            }
        }
    }
