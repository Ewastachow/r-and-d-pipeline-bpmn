#!/usr/bin/env groovy

pipeline{

    agent{
        label "deploy"
    }

    stages{
        stage('hello world'){
            steps{
                sh "echo hello world"
            }
        }
    }

}