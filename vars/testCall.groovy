def call() {
    sh(
            script: './kubectl get pod | grep hello-world-app-$BUILD_NUMBER-* | \
                                  awk \'{print $1}\'',
            returnStdout: true
    ).trim()
}


// sh script: "cp -r ~/.gradle /jenkinsPv/gradle/${env.JOB_NAME}/"


//public void agentTemplate(body) {
//    podTemplate(
//            containers: [
//                    containerTemplate(name: 'gradle', image: 'gradle:7.5.1-jdk11-jammy', resourceLimitMemory:'1024Mi', resourceRequestMemory:'512Mi', command: 'cat', ttyEnabled: true),
//                    containerTemplate(name: 'docker', image: 'docker', resourceLimitMemory:'1024Mi', resourceRequestMemory:'512Mi', command: 'cat', ttyEnabled: true)],
//            volumes: [persistentVolumeClaim(mountPath: '/root/.gradle', claimName: 'jenkins-pv-claim', readOnly: false)], [hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock')]) {
//        body.call()
//    }
//}
//
//stage('Docker Build and push (Dockerfile)') {
//    try {
//        container('gcloud') {
//            withCredentials([file(credentialsId: 'gcr-prod', variable: 'GC_KEY')]) {
//                sh script: "cat ${GC_KEY} | docker login -u _json_key --password-stdin https://eu.gcr.io/tos-ci", returnStdout: true
//                if (stand_alone) {
//                    sh script: "docker version && cd ${path_to_module} && docker build --build-arg ARTIFACTORY_USER=${props.artifactoryCloudUser} --build-arg ARTIFACTORY_SECRET=${props.artifactoryCloudSecret} -t ${dockerTargetRegistry}/${serviceName} -f Dockerfile ."
//                }
//                for (tag in DOCKER_IMAGE_TAG.split(',')) {
//                    sh script: "docker tag ${dockerTargetRegistry}/${serviceName} ${dockerTargetRegistry}/${serviceName}:${tag}", returnStdout: true
//                    sh script: "docker push ${dockerTargetRegistry}/${serviceName}:${tag}", returnStdout: true
//                }
//            }
//        }
//    } catch (e) {
//        currentBuild.result = 'FAILURE'
//        throw e