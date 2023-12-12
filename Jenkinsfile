pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo 'Running build automation'
                sh './gradlew myZip --no-daemon'

                echo 'Generating artifact'
                archiveArtifacts artifacts: 'dist/trainSchedule.zip'
            }
        }
        stage('Build Docker Image') {
            when {
                branch 'master'
            }
            steps {
                echo 'Starting to build docker image'
                script {
                    def app = docker.build("viavn/train-schedule")
                    app.inside {
                        sh 'echo $(curl localhost:3000)'
                    }
                }
            }
        }
        stage('Push Docker Image') {
            when {
                branch 'master'
            }
            steps {
                echo 'Starting to push docker image to registry'
                /* https://index.docker.io/v1/ */
                script {
                    docker.push("${env.BUILD_NUMBER}")
                    docker.push("latest")
                }
            }
        }
    }
}