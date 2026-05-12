pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
    }

    environment {
        JAVA_HOME = '/opt/java/openjdk'
        DOCKER_HUB_CREDENTIALS = credentials('dockerhub_credentials')
        DOCKER_IMAGE = 'lishangzhi/teedy-1'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                echo 'Building project and running tests...'
                sh 'export JAVA_HOME=/opt/java/openjdk && mvn clean install -DskipTests=false'
            }
            post {
                success {
                    echo 'Build and tests passed!'
                }
            }
        }

        stage('Package') {
            steps {
                echo 'Packaging artifacts...'
                sh 'export JAVA_HOME=/opt/java/openjdk && mvn package -DskipTests=true'
            }
        }

        stage('Building image') {
            steps {
                script {
                    docker.build("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}")
                }
            }
        }

        stage('Upload image') {
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', 'dockerhub_credentials') {
                        docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").push()
                        docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").push('latest')
                    }
                }
            }
        }

        stage('Run containers') {
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
        always {
            echo 'Archiving test results and artifacts...'
            junit allowEmptyResults: true,
                  testResults: '**/target/surefire-reports/*.xml'
            archiveArtifacts allowEmptyArchive: true,
                             artifacts: '**/target/*.jar, **/target/*.war',
                             fingerprint: true
            archiveArtifacts allowEmptyArchive: true,
                             artifacts: '**/target/site/**'
        }
        success {
            echo 'Pipeline completed successfully!'
            echo 'Containers running on ports: 8082, 8083, 8084'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}
