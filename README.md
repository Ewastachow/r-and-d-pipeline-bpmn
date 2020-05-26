# R&D BPMN in projects development

## Jenkins & Sonar infrastructure
Plik: [docker-compose.yml](r-and-d-infrastructure/docker-compose.yml)

Uruchomienie:
```shell script
cd r-and-d-infrastructure
docker-compose up
```

### Jenkins
#### Środowisko
Jenkins login: admin/admin

URL: [http://localhost:8001/](http://localhost:8001/)

`JENKINS_HOME` dla skonfigurowanego serwera ze wsparciem dla sonara oraz jobem dla tego projektu 
w `r-and-d-infrastructure/jenkins_home`, automatycznie załączany dla `r-and-d-infrastructure/docker-compose.yml`

```yaml
  jenkins:
    image: jenkins/jenkins:lts
    restart: unless-stopped
    ports:
      - 8001:8080
      - 50000:50000
    volumes:
      - ./jenkins_home:/var/jenkins_home
      - /var/run/docker.sock:/var/run/docker.sock
```
Pierwsze odpalenie joba może trwać długo (do godziny) ponieważ pobierane są wszystkie zależności

#### Multibranch pipeline
![multibranch_pipeline](doc/multibranch_pipeline.png)

![mutlibranch_pipeline_config](doc/multibranch_pipeline_config.png)

Pipeline znajduje się: `r-and-d-service/.jenkins/Jenkinsfile.groovy`

```groovy
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
    parameters {
        booleanParam(name: 'WITH_SONAR', defaultValue: false, description: 'Should run Sonar analysis?')
    }
    stages {
        //...
    }
    post {
        cleanup {
            cleanWs()
        }
    }
}

```

### SonarQube
#### Środowisko
Sonar login: admin/admin

URL: [http://localhost:9000/](http://localhost:9000/)
```yaml
  sonarqube:
    image: sonarqube:lts
    ports:
      - 9000:9000
    environment:
      - sonar.jdbc.username=sonar
      - sonar.jdbc.password=sonar
      - sonar.jdbc.url=jdbc:postgresql://db/sonar
  db:
    image: postgres
    environment:
      - POSTGRES_USER=sonar
      - POSTGRES_PASSWORD=sonar
```
#### Branch community plugin
[sonarqube-community-branch-plugin](https://github.com/mc1arke/sonarqube-community-branch-plugin)

Parametry wywołania
* Branch: `-Dsonar.branch.name=${BRANCH_NAME}`
* Pull request: `-Dsonar.pullrequest.key=${PR_ID} -Dsonar.pullrequest.branch=${CHANGE_BRANCH} -Dsonar.pullrequest.base=${CHANGE_TARGET}`

#### Przykładowy stage dla pipelinu
```groovy
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
```
#### UI
![sonarqube_ui](doc/sonarqube.png)

## BPMN code coverage
https://github.com/camunda/camunda-bpm-process-test-coverage

https://bpmn.io/toolkit/bpmn-js/

## Jenkow - Jenkins in BPMN Workflows
### Business Process Model and Notation (BPMN) Workflows in Jenkins
2012 San Fran JUC: Max Spring - Business Process Model and Notation (BPMN) Workflows in Jenkins
https://www.youtube.com/watch?v=lcZh2tvqC-A

TODO

## jBPM
https://plugins.jenkins.io/jbpm-workflow-plugin/

https://plugins.jenkins.io/jbpm-embedded-plugin/

https://wiki.jenkins.io/display/JENKINS/jBPM+Integration+with+Jenkins


TODO

## Artykuły
### Automatic Verification of BPMN Models
https://www.researchgate.net/publication/339068865_Automatic_Verification_of_BPMN_Models

TODO

### BPMN in the Wild: BPMN on GitHub.com
https://www.researchgate.net/publication/341000641_BPMN_in_the_Wild_BPMN_on_GitHubcom

TODO
