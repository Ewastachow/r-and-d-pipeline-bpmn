FROM jenkins/jenkins:lts

USER root

ARG USER_GID
ARG USER_ID

# RUN groupmod -g ${USER_GROUP_ID} jenkins
RUN usermod -u ${USER_ID} -g ${USER_GID} jenkins \
    # chown ${REF} since we changed the UID
    && chown -R jenkins ${REF}

# install docker commandline interface and its dependencies
RUN apt update && apt install -y lsb-release \
    software-properties-common \
    apt-transport-https \
    && rm -rf /var/lib/apt/lists/*

RUN curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add - \
    && add-apt-repository "deb [arch=amd64] \
    https://download.docker.com/linux/debian $(lsb_release -cs) stable" \
    && apt update && apt install -y docker-ce-cli \
    && rm -rf /var/lib/apt/lists/*

USER jenkins

# copy plugins.txt to the $REF/init.groovy.d directory
# that is already set up by the base jenkins image
COPY plugins.txt ${REF}/init.groovy.d/plugins.txt
# install all plugins listed up there
RUN install-plugins.sh < ${REF}/init.groovy.d/plugins.txt

WORKDIR $JENKINS_HOME
