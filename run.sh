#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "======================================================="
echo "  🚀 Menjalankan Aplikasi E-Commerce (Main.kt)"
echo "======================================================="

JAVA_CMD=""

# 1. Cek atau siapkan Java
if [ -f "jre/bin/java" ]; then
    JAVA_CMD="./jre/bin/java"
    export JAVA_HOME="$SCRIPT_DIR/jre"
    export PATH="$SCRIPT_DIR/jre/bin:$PATH"
    echo "ℹ️  Menggunakan Java Portable lokal (./jre)..."
elif command -v java &> /dev/null; then
    JAVA_CMD="java"
    echo "ℹ️  Menggunakan Java dari sistem..."
else
    echo "⚠️  Java tidak ditemukan di sistem!"
    echo "📥 Mengunduh Java Portable (OpenJDK 21 JRE) otomatis..."
    OS="linux"
    if [[ "$OSTYPE" == "darwin"* ]]; then
        OS="mac"
    fi
    ARCH="x64"
    if [[ "$(uname -m)" == "arm64" || "$(uname -m)" == "aarch64" ]]; then
        ARCH="aarch64"
    fi
    curl -sL "https://api.adoptium.net/v3/binary/latest/21/ga/${OS}/${ARCH}/jre/hotspot/normal/eclipse" -o jre.tar.gz
    mkdir -p jre_temp
    tar -xzf jre.tar.gz -C jre_temp
    mv jre_temp/* jre
    rm -rf jre.tar.gz jre_temp
    JAVA_CMD="./jre/bin/java"
    export JAVA_HOME="$SCRIPT_DIR/jre"
    export PATH="$SCRIPT_DIR/jre/bin:$PATH"
    echo "✅ Java Portable berhasil disiapkan!"
fi

# 2. Cek atau siapkan Kotlin Compiler
KOTLINC_CMD=""
if [ -f "kotlinc/bin/kotlinc" ]; then
    chmod +x kotlinc/bin/* 2>/dev/null || true
    KOTLINC_CMD="./kotlinc/bin/kotlinc"
    echo "ℹ️  Menggunakan Kotlin Compiler Portable (./kotlinc)..."
elif command -v kotlinc &> /dev/null; then
    KOTLINC_CMD="kotlinc"
    echo "ℹ️  Menggunakan Kotlin Compiler dari sistem..."
else
    echo "📥 Mengunduh Kotlin Compiler Portable (v2.1.0)..."
    curl -sL "https://github.com/JetBrains/kotlin/releases/download/v2.1.0/kotlin-compiler-2.1.0.zip" -o kotlinc.zip
    unzip -q kotlinc.zip
    rm -f kotlinc.zip
    chmod +x kotlinc/bin/*
    KOTLINC_CMD="./kotlinc/bin/kotlinc"
    echo "✅ Kotlin Compiler Portable berhasil disiapkan!"
fi

# 3. Kompilasi kode jika compiler tersedia
if [ -n "$KOTLINC_CMD" ]; then
    echo "🔄 Mengompilasi kode terbaru dari src/..."
    "$KOTLINC_CMD" src -include-runtime -d EcommerceApp.jar
    echo "✅ Kompilasi berhasil!"
fi

# 4. Jalankan aplikasi
if [ -f "EcommerceApp.jar" ]; then
    echo "▶️  Menjalankan program..."
    echo "======================================================="
    "$JAVA_CMD" -jar EcommerceApp.jar
else
    echo "❌ File EcommerceApp.jar tidak ditemukan!"
    exit 1
fi
