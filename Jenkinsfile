pipeline {
    agent any

    environment {
        DOCKER_IMAGE_BASE_NAME = 'train-schedule'
        DOCKER_IMAGE_OWNER = 'viavn'
        FINAL_IMAGE_NAME = ''
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
                    FINAL_IMAGE_NAME = '$DOCKER_IMAGE_OWNER/$DOCKER_IMAGE_BASE_NAME'
                    docker.build(FINAL_IMAGE_NAME).inside {
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
                    docker push ${FINAL_IMAGE_NAME}:${env.BUILD_NUMBER}
                    docker push ${FINAL_IMAGE_NAME}:latest
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