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
        '''
        }
    }
    stages {
        stage('Build Gradle') {
            steps {
                container('gradle') {
                    sh '''gradle -version
                          gradle build'''
                }
            }
        }
        stage('bootRun the application') {
            steps {
                container('gradle') {
                    sh 'gradle bootrun'
                }
            }
        }
        
    }
}
