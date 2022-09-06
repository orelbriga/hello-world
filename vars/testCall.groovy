def call() {
    sh(
            script: './kubectl get pod | grep hello-world-app-$BUILD_NUMBER-* | \
                                  awk \'{print $1}\'',
            returnStdout: true
    ).trim()
}


// sh script: "cp -r ~/.gradle /jenkinsPv/gradle/${env.JOB_NAME}/"