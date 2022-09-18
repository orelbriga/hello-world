def call() {
    sh(
            script: './kubectl get pod | grep hello-world-app-$BUILD_NUMBER-* | \
                                  awk \'{print $1}\'',
            returnStdout: true
    ).trim()
}

stage("Docker Build") {
    container('gradle') {
        sh "mkdir -p /gradle_local/.gradle"
        sh script: "cp -r /gradle_local/.gradle ~/.gradle"
        sh script: "./gradlew jib --build-cache", returnStdout: true
        sh script: "cp -r ~/.gradle /gradle_local/.gradle "
    }
}
