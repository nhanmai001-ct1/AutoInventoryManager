@echo off
setlocal
set DIR=%~dp0
set GRADLE_WRAPPER_DIR=%DIR%gradle\wrapper
java -jar "%GRADLE_WRAPPER_DIR%\gradle-wrapper.jar" %*