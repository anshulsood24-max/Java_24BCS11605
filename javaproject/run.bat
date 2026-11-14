@echo off
title Student Productivity & Placement Tracker
echo [1/2] Compiling Java source files...
if not exist "bin" mkdir bin
javac -encoding UTF-8 -d bin src\model\*.java src\service\*.java src\ui\*.java src\Main.java

if %ERRORLEVEL% EQU 0 (
    echo [2/2] Launching Application...
    java -cp bin Main %*
) else (
    echo.
    echo Compilation failed. Ensure that JDK (Java Development Kit) is installed and 'javac' is in your PATH.
    pause
)
