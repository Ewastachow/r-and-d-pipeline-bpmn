#!/usr/bin/env groovy

pipeline {
    agent any
    tools {
        maven 'openjdk11'
        maven 'maven3'
    }
    stages {
        stage('test') {
            steps {
                sh 'mvn clean test'
            }
            post {
                always {
                    archiveArtifacts artifacts: 'r-and-d-service/target/process-test-coverage/*/*.html', onlyIfSuccessful: true
                }
            }
        }
    }
}
