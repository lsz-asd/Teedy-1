pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
        jdk 'JDK-17'
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
                sh 'mvn clean verify -DskipTests=false'
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
                sh 'mvn package -DskipTests=true'
            }
        }

        stage('Site Documentation') {
            steps {
                echo 'Generating site documentation...'
                sh 'mvn site -DskipTests=true'
            }
        }
    }

    post {
        always {
            echo 'Archiving test results and artifacts...'

            // Archive test results (JUnit reports)
            junit allowEmptyResults: true,
                  testResults: '**/target/surefire-reports/*.xml'

            // Archive artifacts: JARs and WAR
            archiveArtifacts allowEmptyArchive: true,
                             artifacts: '**/target/*.jar, **/target/*.war',
                             fingerprint: true

            // Archive site documentation
            archiveArtifacts allowEmptyArchive: true,
                             artifacts: '**/target/site/**'
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}
