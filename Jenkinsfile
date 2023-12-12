pipeline {
    agent any

    environment {
        dockerImageBaseName = 'train-schedule'
        dockerImageOwner = 'viavn'
        finalImageName = ''
    }

    stages {
        stage('Build') {
            steps {
                echo 'Running build automation'
                /* sh './gradlew myZip' */
                sh './gradlew build'

                /*echo 'Generating artifact'
                archiveArtifacts artifacts: 'dist/trainSchedule.zip' */
            }
        }
        stage('Build Docker Image') {
            when {
                branch 'master'
            }
            steps {
                echo 'Starting to build docker image'
                script {
                    finalImageName = '${dockerImageOwner}/${dockerImageBaseName}'
                    docker.build(finalImageName).inside {
                        // sh 'echo $(curl localhost:3000)'
                        sh('ls -lha')
                    }
                }
            }
        }
        stage('Docker Hub login') {
            when {
                branch 'master'
            }
            steps {
                echo 'Retrieve Docker registry credentials from Jenkins credentials store'
                withCredentials([usernamePassword(credentialsId: 'docker_hub_login', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                    sh('echo ${DOCKER_PASSWORD} | docker login -u ${DOCKER_USERNAME} --password-stdin')
                }
            }
        }
        stage('Push Docker Image') {
            when {
                branch 'master'
            }
            steps {
                echo 'Starting to push docker image to registry'
                sh """
                    docker push ${finalImageName}:${env.BUILD_NUMBER}
                    docker push ${finalImageName}:latest
                """
            }
        }
    }
    post {
        always {
            sh('docker logout')
        }
  }
}