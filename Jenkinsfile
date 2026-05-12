pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'lishangzhi/teedy-1'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Image') {
            steps {
                script { docker.build("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}") }
            }
        }

        stage('Push') {
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', 'docker') {
                        docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").push()
                        docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").push('latest')
                    }
                }
            }
        }

        stage('Run Containers') {
            steps {
                sh 'docker stop teedy-8082 teedy-8083 teedy-8084 2>/dev/null; docker rm teedy-8082 teedy-8083 teedy-8084 2>/dev/null; true'
                script {
                    docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").run('--name teedy-8082 -d -p 8082:8080')
                    docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").run('--name teedy-8083 -d -p 8083:8080')
                    docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").run('--name teedy-8084 -d -p 8084:8080')
                }
            }
        }
    }
}
