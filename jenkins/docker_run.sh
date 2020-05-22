#!/usr/bin/env bash

export USER_GID=$(id -g)
export USER_ID=$(id -u)

export JENKINS_HOME=/var/jenkins_home
export REF=/usr/share/jenkins/ref
export CASC_JENKINS_CONFIG=$REF/casc_configs/jenkins.yml

docker-compose "$@"
