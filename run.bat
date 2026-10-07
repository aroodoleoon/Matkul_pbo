@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

echo =======================================================
echo   Menjalankan Aplikasi E-Commerce [Main.kt]
echo =======================================================

set "JAVA_CMD="

REM 1. Cek Java Portable lokal
if exist "jre\bin\java.exe" (
    set "JAVA_CMD=%~dp0jre\bin\java.exe"
    set "JAVA_HOME=%~dp0jre"
    set "PATH=%~dp0jre\bin;!PATH!"
    echo [INFO] Menggunakan Java Portable lokal [./jre]...
    goto CHECK_KOTLIN
)

REM Cek Java sistem
where java >nul 2>nul
if %errorlevel% equ 0 (
    set "JAVA_CMD=java"
    echo [INFO] Menggunakan Java dari sistem...
    goto CHECK_KOTLIN
)

REM Unduh Java jika belum ada
echo [PERINGATAN] Java tidak ditemukan di laptop ini!
echo [INFO] Mengunduh Java Portable OpenJDK 21 JRE secara otomatis...
echo Mohon tunggu sebentar [hanya sekali download]...

where curl >nul 2>nul
if %errorlevel% equ 0 (
    where tar >nul 2>nul
    if %errorlevel% equ 0 (
        curl -L -s -o jre.zip "https://api.adoptium.net/v3/binary/latest/21/ga/windows/x64/jre/hotspot/normal/eclipse"
        mkdir jre_temp 2>nul
        tar -xf jre.zip -C jre_temp
        for /d %%D in (jre_temp\*) do move "%%D" "jre" >nul 2>nul
        rmdir /s /q jre_temp 2>nul
        del /f /q jre.zip 2>nul
    )
)

if not exist "jre\bin\java.exe" (
    powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; $url = 'https://api.adoptium.net/v3/binary/latest/21/ga/windows/x64/jre/hotspot/normal/eclipse'; Invoke-WebRequest -Uri $url -OutFile 'jre.zip' -UseBasicParsing; Expand-Archive 'jre.zip' -DestinationPath 'jre_temp'; $inner = Get-ChildItem 'jre_temp' | Select-Object -First 1; Move-Item $inner.FullName 'jre'; Remove-Item 'jre.zip', 'jre_temp' -Recurse -Force"
)

if exist "jre\bin\java.exe" (
    set "JAVA_CMD=%~dp0jre\bin\java.exe"
    set "JAVA_HOME=%~dp0jre"
    set "PATH=%~dp0jre\bin;!PATH!"
    echo [SUKSES] Java Portable berhasil disiapkan!
) else (
    echo [ERROR] Gagal menyiapkan Java Portable.
    pause
    exit /b 1
)

:CHECK_KOTLIN
REM 2. Cek Kotlin Compiler
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
echo Mohon tunggu sebentar [hanya sekali download]...

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
REM 3. Kompilasi kode jika compiler tersedia
if defined KOTLINC_CMD (
    echo [INFO] Mengompilasi kode terbaru dari src/...
    call "!KOTLINC_CMD!" src -include-runtime -d EcommerceApp.jar
    if !errorlevel! neq 0 (
        echo.
        echo [ERROR] Kompilasi gagal! Periksa error di atas.
        pause
        exit /b 1
    )
    echo [SUKSES] Kompilasi berhasil!
)

:DO_RUN
REM 4. Jalankan program
if exist "EcommerceApp.jar" (
    echo.
    echo [RUN] Menjalankan program...
    echo =======================================================
    "%JAVA_CMD%" -jar EcommerceApp.jar
) else (
    echo [ERROR] File EcommerceApp.jar tidak ditemukan dan compiler tidak tersedia!
)

echo.
pause
