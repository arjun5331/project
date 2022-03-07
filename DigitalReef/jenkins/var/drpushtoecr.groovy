def call(drPipeline) {
    sh """
        echo ${drPipeline.dockerBuildNumber}
        aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin ${drPipeline.ecraccount}
        docker build -t ${drPipeline.imgName} .
        docker tag ${drPipeline.imgName}:latest ${drPipeline.ecraccount}/${drPipeline.imgName}:${drPipeline.dockerBuildNumber}
        docker push ${drPipeline.ecraccount}/${drPipeline.imgName}:${drPipeline.dockerBuildNumber}
    """
              
    return drPipeline
}

return this