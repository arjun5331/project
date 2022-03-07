def call(drPipeline) {
    sh 'npm install'
    sh 'ng build'
            
    return drPipeline
}

return this
