pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
    }

    environment {
        JAVA_HOME = '/opt/java/openjdk'
        DOCKER_IMAGE = 'lishangzhi/teedy-1'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Maven Build') {
            steps {
                sh 'export JAVA_HOME=/opt/java/openjdk && mvn clean package -DskipTests=true'
            }
        }

        stage('Build Image') {
            steps {
                script {
                    docker.build("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}")
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', 'docker') {
                        docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").push()
                        docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").push('latest')
                    }
                }
            }
        }

        stage('Run 3 Containers') {
            steps {
                script {
                    sh 'docker stop teedy-8082 teedy-8083 teedy-8084 2>/dev/null || true'
                    sh 'docker rm teedy-8082 teedy-8083 teedy-8084 2>/dev/null || true'
                    docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").run('--name teedy-8082 -d -p 8082:8080')
                    docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").run('--name teedy-8083 -d -p 8083:8080')
                    docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").run('--name teedy-8084 -d -p 8084:8080')
                }
            }
        }
    }

    post {
        success {
            echo 'Containers running: http://localhost:8082 http://localhost:8083 http://localhost:8084'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}
