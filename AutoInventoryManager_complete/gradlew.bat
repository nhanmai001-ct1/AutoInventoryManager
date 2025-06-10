@echo off
set DIR=%~dp0
set WRAPPER=%DIR%gradle\wrapper\gradle-wrapper.jar
java -jar "%WRAPPER%" %*