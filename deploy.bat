rmdir "C:\Program Files\apache-tomcat-8.0.28\webapps\ROOT\kvmclient\lib" /s /q
del /q /f "C:\Program Files\apache-tomcat-8.0.28\webapps\ROOT\kvmclient\*"
copy "%JENKINS_HOME%\jobs\%JOB_NAME%\builds\%BUILD_NUMBER%\com.kvm$kvmclient\archive\com.kvm\kvmclient\0.0.1-SNAPSHOT\kvmclient-0.0.1-SNAPSHOT.jar" "C:\Program Files\apache-tomcat-8.0.28\webapps\ROOT\kvmclient\kvmclient.jar"
(robocopy %WORKSPACE%\target\lib "C:\Program Files\apache-tomcat-8.0.28\webapps\ROOT\kvmclient\lib" /E) ^& IF %ERRORLEVEL% LEQ 1 exit 0