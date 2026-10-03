@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

echo =======================================================
echo   Menjalankan Aplikasi E-Commerce (Main.kt)
echo =======================================================

set "JAVA_CMD="

REM 1. Cek apakah ada folder jre lokal (Portable JRE offline)
if exist "jre\bin\java.exe" (
    set "JAVA_CMD=jre\bin\java.exe"
    echo [INFO] Menggunakan Java Portable lokal (./jre)...
    goto RUN
)

REM 2. Cek apakah ada java di sistem PATH
where java >nul 2>nul
if %errorlevel% equ 0 (
    set "JAVA_CMD=java"
    echo [INFO] Menggunakan Java dari sistem...
    goto COMPILE_CHECK
)

REM 3. Jika laptop kosongan sama sekali (tidak ada Java & tidak ada ./jre)
echo [PERINGATAN] Java tidak ditemukan di laptop ini!
echo [INFO] Mengunduh Java Portable (OpenJDK 21 JRE) secara otomatis...
echo Mohon tunggu sebentar (hanya sekali download)...

powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; $url = 'https://api.adoptium.net/v3/binary/latest/21/ga/windows/x64/jre/hotspot/normal/eclipse'; Invoke-WebRequest -Uri $url -OutFile 'jre.zip' -UseBasicParsing; Expand-Archive 'jre.zip' -DestinationPath 'jre_temp'; $inner = Get-ChildItem 'jre_temp' | Select-Object -First 1; Move-Item $inner.FullName 'jre'; Remove-Item 'jre.zip', 'jre_temp' -Recurse -Force"

if exist "jre\bin\java.exe" (
    set "JAVA_CMD=jre\bin\java.exe"
    echo [SUKSES] Java Portable berhasil disiapkan!
    goto RUN
) else (
    echo [ERROR] Gagal mengunduh Java Portable otomatis.
    echo Silakan hubungkan ke internet atau letakkan folder Java JRE di dalam folder 'jre'.
    pause
    exit /b 1
)

:COMPILE_CHECK
where kotlinc >nul 2>nul
if %errorlevel% equ 0 (
    echo [INFO] Mengompilasi kode terbaru dari src/...
    call kotlinc src -include-runtime -d EcommerceApp.jar
)

:RUN
if exist "EcommerceApp.jar" (
    echo.
    echo [RUN] Menjalankan program...
    echo =======================================================
    "%JAVA_CMD%" -jar EcommerceApp.jar
) else (
    echo [ERROR] File EcommerceApp.jar tidak ditemukan!
)

echo.
pause
