@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

echo =======================================================
echo   Kompilasi Aplikasi E-Commerce [src/ -^> JAR]
echo =======================================================

REM 1. Siapkan Java jika ada di jre lokal
if exist "jre\bin\java.exe" (
    set "JAVA_HOME=%~dp0jre"
    set "PATH=%~dp0jre\bin;!PATH!"
)

REM 2. Cek Kotlin compiler lokal atau sistem
set "KOTLINC_CMD="
if exist "kotlinc\bin\kotlinc.bat" (
    set "KOTLINC_CMD=%~dp0kotlinc\bin\kotlinc.bat"
    echo [INFO] Menggunakan Kotlin Compiler Portable [./kotlinc]...
    goto DO_COMPILE
)

where kotlinc >nul 2>nul
if %errorlevel% equ 0 (
    set "KOTLINC_CMD=kotlinc"
    echo [INFO] Menggunakan Kotlin Compiler dari sistem...
    goto DO_COMPILE
)

echo [INFO] Mengunduh Kotlin Compiler Portable v2.1.0...
where curl >nul 2>nul
if %errorlevel% equ 0 (
    where tar >nul 2>nul
    if %errorlevel% equ 0 (
        curl -L -s -o kotlinc.zip "https://github.com/JetBrains/kotlin/releases/download/v2.1.0/kotlin-compiler-2.1.0.zip"
        tar -xf kotlinc.zip
        del /f /q kotlinc.zip 2>nul
    )
)

if not exist "kotlinc\bin\kotlinc.bat" (
    powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://github.com/JetBrains/kotlin/releases/download/v2.1.0/kotlin-compiler-2.1.0.zip' -OutFile 'kotlinc.zip' -UseBasicParsing; Expand-Archive 'kotlinc.zip' -DestinationPath '.'; Remove-Item 'kotlinc.zip' -Force"
)

if exist "kotlinc\bin\kotlinc.bat" (
    set "KOTLINC_CMD=%~dp0kotlinc\bin\kotlinc.bat"
    echo [SUKSES] Kotlin Compiler Portable berhasil disiapkan!
)

:DO_COMPILE
if not defined KOTLINC_CMD (
    echo [ERROR] Gagal menemukan atau mengunduh Kotlin compiler.
    pause
    exit /b 1
)

echo [INFO] Mengompilasi kode dari folder src/...
call "!KOTLINC_CMD!" src -include-runtime -d EcommerceApp.jar
if !errorlevel! equ 0 (
    echo [SUKSES] EcommerceApp.jar berhasil dibuat!
) else (
    echo [ERROR] Kompilasi gagal! Periksa kode Anda.
    pause
    exit /b 1
)

echo.
pause
