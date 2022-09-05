#!groovy
pipeline {
    agent {
        kubernetes {
            yamlFile 'agent-pod.yaml'
        }
    }
    stages {
        stage('Gradle: Test & Build') {
            steps {
                container('gradle') {
                    echo "compiling code + running  tests + building jar: "
                    sh """chmod +x ./gradlew
                          ./gradlew clean build"""
                    echo "saving jar as an artifact:"
                    archiveArtifacts artifacts: 'build/libs/hello-world-0.0.1-SNAPSHOT.jar', onlyIfSuccessful: true
                }
            }
        }
        stage('Build docker image & push to registry') {
            steps {
                container('docker') {
                    script {
                        def REPOSITORY = "orelbriga/hello-world-app"
                        def registryCredentialID = 'dockerhub'
                        echo "building docker image:"
                        def dockerImage = docker.build REPOSITORY
                        echo "Login to private repo on dockerhub:"
                        docker.withRegistry('', registryCredentialID) {
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
                    echo "deploy the app to the k8s cluster using yaml files - with kube-config as an authenticator: "
                    kubernetesDeploy(configs: 'config.yaml', kubeconfigId: 'k8sconfig')
                }
            }
        }
        stage('Deployment Tests') {
            steps {
                container('docker') {
                    script {
                        withKubeConfig([credentialsId: 'secret-jenkins']) {
                            echo "installing kubectl on the container to check the application's pod state + logs:"
                            sh ''' wget "https://storage.googleapis.com/kubernetes-release/release/v1.24.1/bin/linux/amd64/kubectl"
                               chmod +x ./kubectl
                               sleep 10s '''

                            def APP_POD_NAME = sh(
                                    script: './kubectl get pod | grep hello-world-app-$BUILD_NUMBER-* | \
                                    awk \'{print $1; exit}\'',
                                    returnStdout: true
                            ).trim()

                            def POD_STATE = sh(
                                    script: './kubectl get po | grep hello-world-app-$BUILD_NUMBER-* | \
                                    awk \'{print $3; exit}\'',
                                    returnStdout: true
                            ).trim()

                            def CLUSTER_HOST_IP = sh(
                            script: './kubectl get pod -n kube-system $(./kubectl get po -n kube-system | grep dns \
                            | awk \'{print $1; exit}\') -o=jsonpath=\'{.status.hostIP}\' ' , returnStdout: true
                            ).trim()

                            def NODE_PORT = sh(
                            script: './kubectl get svc hello-world-svc-$BUILD_NUMBER -o=jsonpath=\'{.spec.ports[].nodePort}\' ',
                            returnStdout: true
                            ).trim()

                            echo "Sending GET request to the application: "
                            def RESPONSE = httpRequest "http://$CLUSTER_HOST_IP:$NODE_PORT"
                            println("Content: "+RESPONSE.content)
                            sh "sleep 5s"

                            sh "./kubectl logs $APP_POD_NAME | tee ${APP_POD_NAME}.log"
                            archiveArtifacts artifacts: 'hello-world-app-*.log'

                            if (POD_STATE != "Running" || RESPONSE.status >= 400) {
                                error("Application pod $APP_POD_NAME is not healthy, check app log")
                            }
                            else {
                                echo "Application pod $APP_POD_NAME is in $POD_STATE state!"
                            }
                        }
                    }
                }
            }
        }
        stage('Terminate app & Image cleanup') {
            steps {
                container('docker') {
                    withKubeConfig([credentialsId: 'secret-jenkins']) {

                        echo "Deployment tests passed successfully - Terminating the app: "
                        sh '''./kubectl delete deployment,services -l app=hello-world-app-${BUILD_NUMBER}
                              sleep 5s'''

                        echo "Delete unused app image: "
                        sh '''docker image rmi -f orelbriga/hello-world-app:$BUILD_NUMBER '''
                    }
                }
            }
        }
    }
}
