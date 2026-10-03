#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "======================================================="
echo "  🚀 Menjalankan Aplikasi E-Commerce (Main.kt)"
echo "======================================================="

JAVA_CMD=""

# 1. Cek folder jre lokal
if [ -f "jre/bin/java" ]; then
    JAVA_CMD="./jre/bin/java"
    echo "ℹ️  Menggunakan Java Portable lokal (./jre)..."
elif command -v java &> /dev/null; then
    JAVA_CMD="java"
    echo "ℹ️  Menggunakan Java dari sistem..."
    if command -v kotlinc &> /dev/null; then
        echo "🔄 Mengompilasi kode terbaru dari src/..."
        kotlinc src -include-runtime -d EcommerceApp.jar
    fi
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
    echo "✅ Java Portable berhasil disiapkan!"
fi

if [ -f "EcommerceApp.jar" ]; then
    echo "▶️  Menjalankan program..."
    echo "======================================================="
    "$JAVA_CMD" -jar EcommerceApp.jar
else
    echo "❌ File EcommerceApp.jar tidak ditemukan!"
    exit 1
fi
