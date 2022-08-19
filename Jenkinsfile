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
            image: gradle:jdk11-alpine
            command:
            - cat
            tty: true
        '''
        }
    }
    stages {
        stage('Run gradle') {
            steps {
                container('gradle') {
                    sh '''gradle build'''
                }
            }
        }
    }
}
