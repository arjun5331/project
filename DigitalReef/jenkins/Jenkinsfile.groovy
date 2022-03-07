def drpipeline = [
    # module: 'll-web-ui',
    # imgName: 'webui',
    # paramFile: "src/environments/environment.ts", 
    # current_env: '',
    ]
@Library("microservice-shared-library") _
drsetparams(drPipeline)
pipeline {
    agent { node { label 'ecs-dr-slaves' } }
    stages {
        stage ('buid') {
            steps {    
                drbuildreactjs(drPipeline)
            }
        }
    
        stage('imagepushtoecr'){
            steps {
                script{
                    docker.withServer('tcp://ec2-44-194-106-119.compute-1.amazonaws.com:1111') {
                        withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', accessKeyVariable: 'AWS_ACCESS_KEY_ID', credentialsId: 'AKIA3JDKVGSK6JGMVKW3', secretKeyVariable: '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']]) {
                            drpushtoecr(drPipeline)
                        }
                    }
                }
            }
        }
        stage('deploytofargate'){
            steps {
        
                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', accessKeyVariable: 'AWS_ACCESS_KEY_ID', credentialsId: 'AKIA3JDKVGSK6JGMVKW3', secretKeyVariable: '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']]) {
                    drdeploytofargate(drPipeline)
                }
            }
        }
    }
    post {
        always {
             emailext attachLog: true, body: """$JOB_NAME -  Build $BUILD_DISPLAY_NAME - ${currentBuild.currentResult} : 
                
                Check console output at ${env.BUILD_URL} to view the details.""", subject: "$JOB_NAME -  Build $BUILD_DISPLAY_NAME - ${currentBuild.currentResult}", to: 'nagarjunareddy.palem@motivitylabs.com'
             cleanWs()
        }
    }   
}