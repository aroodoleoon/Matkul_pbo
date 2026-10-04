# 🛒 Sistem E-Commerce Console (Kotlin OOP)

Proyek aplikasi e-commerce berbasis antarmuka konsol (*Command Line Interface*) yang dibangun menggunakan bahasa pemrograman **Kotlin** untuk memenuhi Tugas Kelompok Mata Kuliah **Pemrograman Berorientasi Objek (PBO)**.

Aplikasi ini menerapkan seluruh prinsip dasar dan lanjutan PBO seperti **Enkapsulasi**, **Pewarisan (Inheritance)**, **Polimorfisme**, **Abstraksi**, **Sealed Classes**, hingga **Generics & Type Checking**.

---

## 👥 Anggota Kelompok & Kontribusi

| Nama Anggota | GitHub Username | Kontribusi & Tanggung Jawab Modul |
| :--- | :--- | :--- |
| **Muhamad Wildan Muzaqi** | [@aroodoleoon](https://github.com/aroodoleoon) | `Main.kt`, `CreditCardPayment.kt`, `BankTransferPayment.kt`, Portable Runner (`run.bat`, `run.sh`), Bundle JAR, KDoc & Dokumentasi |
| **Pardamean** | [@Pardamean760](https://github.com/Pardamean760) | `Product.kt` (Kelas Induk Abstrak), `ElectronicProduct.kt` |
| **Dhafin** | [@Dhafin](https://github.com) | `Order.kt`, `OrderStatus.kt` (Sealed Class) |
| **Arya Tirta** | [@AryaTirta](https://github.com) | `ShoppingCart.kt`, `PaymentMethod.kt` (Interface) |
| **Habibi** | [@zdnhabibi02](https://github.com/zdnhabibi02) | `User.kt` (Enkapsulasi & Autentikasi), `QRISPayment.kt` |
| **Dhia Athalla** | [@JeDhiaAthalla](https://github.com/JeDhiaAthalla) | `ClothingProduct.kt`, `FoodProduct.kt`, `ECommerceSystem.kt` (Laporan Omset & Logika Toko) |

---

## ⚡ Cara Menjalankan Program (Quick Start)

Aplikasi telah dibundel menjadi file **Executable JAR Standalone** (`EcommerceApp.jar`) dan dilengkapi script runner otomatis yang dapat berjalan di laptop mana pun (bahkan di laptop kosongan tanpa instalasi Java sebelumnya).

### 🪟 1. Pengguna Windows
Cukup **klik dua kali (*double-click*)** file:
```text
run.bat
```
*Atau jalankan via Command Prompt / PowerShell:*
```bat
.\run.bat
```
> **Catatan Windows:** Jika di laptop Anda belum terpasang Java, script `run.bat` akan secara otomatis mengunduh OpenJDK 21 Portable (JRE) langsung dari Adoptium ke folder lokal `jre/`.

---

### 🐧 / 🍎 2. Pengguna Linux / macOS / GitHub Codespace
Buka terminal pada direktori proyek, berikan izin eksekusi (jika belum), lalu jalankan:
```bash
chmod +x run.sh
./run.sh
```

---

### ☕ 3. Menjalankan File JAR Langsung (Bagi yang sudah memiliki Java)
Jika di laptop sudah terpasang Java (versi 17 ke atas):
```bash
java -jar EcommerceApp.jar
```

---

### 🛠️ 4. Mengompilasi Manual dari Source Code (Kompiler Kotlin)
Jika Anda mengubah kode di dalam folder `src/` dan ingin mengompilasinya kembali secara manual:
```bash
# Kompilasi seluruh file di folder src/ ke bundle JAR
kotlinc src -include-runtime -d EcommerceApp.jar

# Jalankan hasil kompilasi
java -jar EcommerceApp.jar
```

---

## 🔑 Akun Demo Pengujian

Untuk mempermudah pengujian alur belanja tanpa perlu melakukan registrasi baru setiap kali menjalankan program:
* **Email:** `budi@email.com`
* **Password:** `password123`

*(Anda juga dapat memilih menu **2. Register** pada aplikasi untuk membuat akun baru dengan data sendiri).*

---

## 🏛️ Arsitektur & Penerapan Konsep PBO

```
src/
├── product/              # Hierarchy Produk & Diskon
│   ├── Product.kt           -> Kelas abstrak dasar (Enkapsulasi harga, format Rupiah)
│   ├── ElectronicProduct.kt -> Subclass elektronik (Garansi, status premium)
│   ├── ClothingProduct.kt   -> Subclass pakaian (Diskon musiman & ukuran besar)
│   └── FoodProduct.kt       -> Subclass makanan (Diskon organik, tanggal kedaluwarsa)
├── cart/                 # Keranjang Belanja
│   └── ShoppingCart.kt      -> Manajemen item belanja, potong & restore stok otomatis
├── order/                # Manajemen Pesanan & Status
│   ├── Order.kt             -> Objek nota transaksi belanja (Invoice)
│   └── OrderStatus.kt       -> Sealed class status pesanan (Pending, Paid, Shipped, Delivered, Cancelled)
├── payment/              # Metode Pembayaran & Gateway
│   ├── PaymentMethod.kt     -> Interface metode pembayaran & Sealed class PaymentResult
│   ├── QRISPayment.kt       -> Implementasi QRIS dinamis (Rp 0 admin)
│   ├── CreditCardPayment.kt -> Implementasi Kartu Kredit (Validasi 16 digit & CVV, 2% admin)
│   └── BankTransferPayment.kt -> Implementasi Transfer Bank (Validasi no. rek, biaya flat)
├── user/                 # Entitas Pengguna
│   └── User.kt              -> Enkapsulasi password, validasi email, relasi keranjang & pesanan
├── system/               # Sistem Pengelola Toko
│   └── ECommerceSystem.kt   -> Katalog produk, autentikasi user, checkout, laporan penjualan
└── Main.kt               # Entry Point & Menu Interaktif CLI
```

### 1. Enkapsulasi (Encapsulation)
* Properti sensitif seperti `price` pada `Product`, `password` pada `User`, serta koleksi `items` pada `ShoppingCart` dienkapsulasi menggunakan visibilitas `private`.
* Validasi integritas data pada blok `init` (contoh: validasi format email dengan pemisah `@` dan dot domain, nama serta password tidak boleh kosong).

### 2. Pewarisan (Inheritance)
* `ElectronicProduct`, `ClothingProduct`, dan `FoodProduct` mewarisi kelas abstrak induk `Product`.
* Semua subclass memanggil konstruktor induk melalui `super(...)`.

### 3. Abstraksi (Abstraction)
* `Product` menyediakan method abstrak `calculateDiscount()` dan `getCategory()` yang wajib diimplementasikan secara spesifik oleh tiap jenis produk.
* `PaymentMethod` menyediakan interface kontrak pemrosesan pembayaran (`processPayment`) dan penghitungan biaya admin (`getFee`).

### 4. Polimorfisme (Polymorphism)
* Pemanggilan fungsi `displayInfo()` dan `calculateDiscount()` secara dinamis sesuai tipe objek produk sebenarnya saat runtime.
* Pemrosesan checkout menerima objek antarmuka `PaymentMethod`, sehingga sistem checkout dapat menerima QRIS, Kartu Kredit, maupun Transfer Bank secara seragam.

### 5. Sealed Classes (Type-Safe State)
* `OrderStatus` membatasi variasi status pesanan hanya pada `Pending`, `Paid`, `Shipped`, `Delivered`, dan `Cancelled(reason)`.
* `PaymentResult` memodelkan hasil transaksi menjadi `Success(transactionId)`, `Failed(reason, errorCode)`, atau `Pending`.
* Memastikan ekspresi percabangan `when` bersifat tuntas/eksaustif tanpa memerlukan cabang fallback `else`.

---

## 📖 Dokumentasi Kode (KDoc)

Seluruh kelas, konstruktor properti, dan metode dalam proyek ini telah didokumentasikan menggunakan standar **KDoc (Kotlin Documentation)**:
* Format komentar menggunakan blok `/** ... */`.
* Menyertakan tag `@property`, `@param`, `@return`, serta tautan referensi antar kelas `[NamaKelas]`.
* Anda dapat menyorot (*hover*) setiap nama fungsi atau kelas di VS Code / IntelliJ IDEA untuk melihat dokumentasi interaktifnya.

---

## 📜 Lisensi
Proyek ini dibuat untuk keperluan akademik dan perkuliahan Pemrograman Berorientasi Objek (PBO).
