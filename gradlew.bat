@ECHO OFF
SET DIR=%~dp0
SET APP_HOME=%DIR%
SET CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar

IF NOT EXIST "%CLASSPATH%" (
    IF EXIST "%ProgramFiles%\Gradle\gradle\bin\gradle.bat" (
        CALL "%ProgramFiles%\Gradle\gradle\bin\gradle.bat" %*
        EXIT /B %ERRORLEVEL%
    ) ELSE (
        gradle %*
        EXIT /B %ERRORLEVEL%
    )
)

IF EXIST "%JAVA_HOME%\bin\java.exe" (
    SET JAVACMD="%JAVA_HOME%\bin\java.exe"
) ELSE (
    SET JAVACMD=java
)

%JAVACMD% -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
