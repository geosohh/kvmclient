rmdir "%TOMCAT%\webapps\ROOT\kvmclient\lib" /s /q
del /q /f "%TOMCAT%\webapps\ROOT\kvmclient\*"
copy "%JENKINS_HOME%\jobs\%JOB_NAME%\builds\%BUILD_NUMBER%\com.kvm$kvmclient\archive\com.kvm\kvmclient\0.0.1-SNAPSHOT\kvmclient-0.0.1-SNAPSHOT.jar" "%TOMCAT%\webapps\ROOT\kvmclient\kvmclient.jar"
(robocopy %WORKSPACE%\target\lib "%TOMCAT%\webapps\ROOT\kvmclient\lib" /E) ^& IF %ERRORLEVEL% LEQ 1 exit 0
