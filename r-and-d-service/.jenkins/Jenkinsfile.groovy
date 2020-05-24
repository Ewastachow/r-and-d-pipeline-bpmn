#!/usr/bin/env groovy

pipeline {
    agent any
    tools {
        jdk 'jdk8'
        maven 'maven3'
    }
    stages {
        stage('hello world') {
            steps {
                echo "hello world"
            }
        }
        stage('install') {
            steps {
                sh 'mvn clean install'
            }
            post {
                always {
                    archiveArtifacts artifacts: 'r-and-d-service/target/process-test-coverage/*/*.html', onlyIfSuccessful: true
                }
            }
        }
    }
}
