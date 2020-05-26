cd %~dp0

SET JENKINS_PORT=8093
SET JENKINS_HOME=C:\opt\jenkins-1.651.3\home
java -DJENKINS_HOME=%JENKINS_HOME% -jar jenkins.war --httpPort=%JENKINS_PORT% > localJenkins.log 2>&1
