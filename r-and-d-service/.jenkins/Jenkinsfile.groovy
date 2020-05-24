#!/usr/bin/env groovy

pipeline {
    agent any
    tools {
        maven 'openjdk11'
        maven 'maven3'
    }
    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(
                logRotator(
                        numToKeepStr: '5',
                        daysToKeepStr: '20'
                )
        )
    }
    stages {
        stage('Compile') {
            steps {
                sh 'mvn clean compile'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/surefire-reports/*.xml'
                }
                success {
                    archiveArtifacts artifacts: '**/process-test-coverage/*/*.html'
                }
            }
        }
        stage('Sonar') {
            steps {
                dir('r-and-d-service') {
                    withSonarQubeEnv('SonarQube') {
                        sh 'mvn sonar:sonar'
                    }
                }
            }
        }
        stage('Publish') {
            steps {
                sh 'mvn package -Dskip.tests'
            }
            post {
                success {
                    archiveArtifacts artifacts: '**/pom.xml'
                    archiveArtifacts artifacts: 'r-and-d-service/target/r-and-d-service-*.jar'
                }
            }
        }
    }
    post {
        cleanup {
            cleanWs()
        }
    }
}
