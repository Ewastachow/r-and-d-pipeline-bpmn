#!/usr/bin/env groovy

pipeline {
    agent any
    tools {
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
    parameters {
        booleanParam(name: 'WITH_SONAR', defaultValue: false, description: 'Should run Sonar analysis?')
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
                success {
                    archiveArtifacts artifacts: '**/process-test-coverage/*/*.html'
                }
            }
        }
        stage('Sonar') {
            when {
                expression { return params.WITH_SONAR }
            }
            stages {
                stage('Sonar PR') {
                    when {
                        branch pattern: "PR-\\d+", comparator: "REGEXP"
                    }
                    environment {
                        PR_ID = "${BRANCH_NAME}".replace("PR-", "")
                        SONAR_PR_PARAMS = "-Dsonar.pullrequest.key=${PR_ID} -Dsonar.pullrequest.branch=${CHANGE_BRANCH} -Dsonar.pullrequest.base=${CHANGE_TARGET}"
                    }
                    steps {
                        dir('r-and-d-service') {
                            withSonarQubeEnv('SonarQube') {
                                sh "mvn sonar:sonar ${SONAR_PR_PARAMS}"
                            }
                        }
                    }
                }
                stage('Sonar branch') {
                    when {
                        not {
                            branch pattern: "PR-\\d+", comparator: "REGEXP"
                        }
                    }
                    environment {
                        SONAR_BRANCH_PARAMS = "-Dsonar.branch.name=${BRANCH_NAME}"
                    }
                    steps {
                        dir('r-and-d-service') {
                            withSonarQubeEnv('SonarQube') {
                                sh "mvn sonar:sonar ${SONAR_BRANCH_PARAMS}"
                            }
                        }
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
