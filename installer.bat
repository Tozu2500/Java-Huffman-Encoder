@echo off
setlocal enabledelayedexpansion
title Huffman Encoder - Installer

cd /d "%~dp0"

echo.
echo ========================================
echo    Huffman Encoder - Installation
echo ========================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java 21 or later is not installed or not in your system PATH.
    echo Please install Java from: https://www.oracle.com/java/technologies/downloads/
    echo.
    pause
    exit /b 1
)

REM Check if compilation is needed
if not exist "bin" (
    echo.
    echo The project needs to be compiled before installation.
    echo This will compile all Java source files to the 'bin' directory.
    echo.
    
    set /p compile_consent="Do you want to compile now? (Y/N): "
    if /i not "!compile_consent!"=="Y" (
        echo Compilation skipped. Installation cannot continue.
        pause
        exit /b 1
    )
    
    echo.
    echo Compiling project...
    echo.
    
    mkdir bin 2>nul
    
    REM Find all Java files and compile them
    setlocal enabledelayedexpansion
    set "files="
    for /r HuffmanEncoder\src %%f in (*.java) do (
        set "files=!files! "%%f""
    )
    
    if "!files!"=="" (
        echo ERROR: No Java files found in HuffmanEncoder\src directory.
        pause
        exit /b 1
    )
    
    javac -d bin -cp "HuffmanEncoder\src;HuffmanEncoder\lib\*" !files!
    
    REM Check if compilation actually produced .class files
    if not exist "bin\huffman\Main.class" (
        echo.
        echo ERROR: Compilation failed. No .class files were generated.
        echo.
        pause
        exit /b 1
    )
    
    if errorlevel 1 (
        echo.
        echo ERROR: Compilation failed. Please check your Java installation.
        echo.
        pause
        exit /b 1
    )
    echo Compilation successful!
    echo.
)

echo This will create a desktop shortcut for
echo the Huffman Encoder application.
echo.
echo Location: %USERPROFILE%\Desktop\Huffman Encoder.lnk
echo.

set /p consent="Do you want to continue? (Y/N): "
if /i not "%consent%"=="Y" (
    echo Installation cancelled.
    pause
    exit /b
)

echo.
echo Installing...
echo.

REM Get the full path to this batch file
for /f "delims=" %%A in ('cd') do set "ProjectPath=%%A"
set "LaunchPath=%ProjectPath%\launch.bat"

REM Escape backslashes for VBScript
set "LaunchPathEscaped=%LaunchPath:\=\\%"
set "ProjectPathEscaped=%ProjectPath:\=\\%"

REM Create VBScript to generate shortcut
set "VBScript=%temp%\CreateShortcut.vbs"
(
    echo Set oWS = WScript.CreateObject("WScript.Shell"^)
    echo sLinkFile = "%USERPROFILE%\Desktop\Huffman Encoder.lnk"
    echo Set oLink = oWS.CreateShortcut(sLinkFile^)
    echo oLink.TargetPath = "%LaunchPath%"
    echo oLink.WorkingDirectory = "%ProjectPath%"
    echo oLink.Description = "Huffman Encoder - Text Compression Tool"
    echo oLink.IconLocation = "%LaunchPath%"
    echo oLink.Save
) > "%VBScript%"

REM Execute VBScript
cscript.exe /nologo "%VBScript%"

if exist "%USERPROFILE%\Desktop\Huffman Encoder.lnk" (
    echo.
    echo ========================================
    echo Installation successful!
    echo ========================================
    echo.
    echo A shortcut has been created on your desktop.
    echo You can now launch Huffman Encoder from the desktop.
    echo.
) else (
    echo.
    echo Installation failed. Could not create shortcut.
    echo.
)

REM Cleanup
del "%VBScript%" 2>nul

pause
