import product.ElectronicProduct
import product.FoodProduct
import product.ClothingProduct
import system.ECommerceSystem
import user.User
import payment.QRISPayment
import payment.CreditCardPayment
import payment.BankTransferPayment

fun main() {
    val system = ECommerceSystem("Toko Online Kampus")

    // ============================================================
    // SEED DATA: KATALOG PRODUK
    // ============================================================
    system.addProduct(ElectronicProduct("E01", "Laptop Gaming ASUS", 15000000.0, 10, "ASUS", 36, true))
    system.addProduct(ElectronicProduct("E02", "Smartphone Samsung", 5000000.0, 15, "Samsung", 12, false))
    system.addProduct(ClothingProduct("C01", "Jaket Musim Dingin", 400000.0, 20, "XL", "Navy", "Wol", true))
    system.addProduct(ClothingProduct("C02", "Kaos Polos Cotton", 100000.0, 50, "M", "Hitam", "Katun Combed", false))
    system.addProduct(FoodProduct("F01", "Beras Organik 5kg", 90000.0, 30, "2026-12-31", 100, 5000, true, true))
    system.addProduct(FoodProduct("F02", "Roti Tawar Gandum", 20000.0, 25, "2026-10-06", 2, 500, false, true))

    // Akun demo bawaan (agar bisa langsung tes login)
    val demoUser = User("USR-001", "Budi Santoso", "budi@email.com", "Jl. Mawar No. 10", "password123")
    system.registerUser(demoUser)

    // State user yang sedang login (null = belum login)
    var currentUser: User? = null
    var isRunning = true

    println("\n=======================================================")
    println("      SELAMAT DATANG DI SISTEM E-COMMERCE")
    println("=======================================================")

    while (isRunning) {
        if (currentUser == null) {
            // ========================================================
            // MENU 1: BELUM LOGIN
            // ========================================================
            println("\n--- MENU PENGGUNA ---")
            println("1. Login")
            println("2. Register")
            println("3. Lihat Katalog Produk (Tamu)")
            println("4. Keluar")
            print("Pilih menu [1-4]: ")

            when (readLine()?.trim()) {
                "1" -> {
                    currentUser = system.loginForm()
                }
                "2" -> {
                    val newUser = system.registerForm()
                    if (newUser != null) {
                        println("  Silakan login dengan akun yang baru didaftarkan.")
                    }
                }
                "3" -> {
                    system.displayAllProducts()
                }
                "4" -> {
                    println("Terima kasih telah berkunjung ke Toko Online Kampus!")
                    isRunning = false
                }
                else -> {
                    println("Pilihan tidak valid, silakan coba lagi.")
                }
            }
        } else {
            // ========================================================
            // MENU 2: SUDAH LOGIN
            // ========================================================
            println("\n=======================================================")
            println("  Halo, ${currentUser.name}! [${currentUser.email}]")
            println("=======================================================")
            println("1. Lihat Katalog Produk")
            println("2. Tambah Produk ke Keranjang")
            println("3. Lihat Keranjang Belanja")
            println("4. Hapus Produk dari Keranjang")
            println("5. Checkout Keranjang")
            println("6. Profil & Riwayat Pesanan Saya")
            println("7. Laporan Penjualan Toko")
            println("8. Logout")
            print("Pilih menu [1-8]: ")

            when (readLine()?.trim()) {
                "1" -> {
                    system.displayAllProducts()
                }
                "2" -> {
                    system.displayAllProducts()
                    print("\nMasukkan ID Produk yang ingin dibeli: ")
                    val productId = readLine()?.trim().orEmpty()
                    val product = system.findProduct(productId)

                    if (product == null) {
                        println("Produk dengan ID '$productId' tidak ditemukan.")
                    } else {
                        print("Masukkan jumlah (Quantity): ")
                        val qty = readLine()?.trim()?.toIntOrNull() ?: 0
                        currentUser.cart.addItem(product, qty)
                    }
                }
                "3" -> {
                    currentUser.cart.displayCart()
                }
                "4" -> {
                    if (currentUser.cart.isEmpty()) {
                        println(" Keranjang belanja Anda masih kosong.")
                    } else {
                        currentUser.cart.displayCart()
                        print("\nMasukkan ID Produk yang ingin dihapus dari keranjang: ")
                        val productId = readLine()?.trim().orEmpty()
                        val itemToRemove = currentUser.cart.getItems().keys.find { it.id.equals(productId, ignoreCase = true) }

                        if (itemToRemove != null) {
                            currentUser.cart.removeItem(itemToRemove)
                        } else {
                            println("Produk dengan ID '$productId' tidak ada di keranjang Anda.")
                        }
                    }
                }
                "5" -> {
                    if (currentUser.cart.isEmpty()) {
                        println("Keranjang kosong! Tambahkan produk terlebih dahulu sebelum checkout.")
                    } else {
                        currentUser.cart.displayCart()
                        println("\n--- Pilih Metode Pembayaran ---")
                        println("1. QRIS (Instan & Bebas Biaya Admin)")
                        println("2. Kartu Kredit (Biaya Admin 2%)")
                        println("3. Transfer Bank (BCA / Mandiri / BNI)")
                        print("Pilih metode pembayaran [1-3]: ")

                        val payment = when (readLine()?.trim()) {
                            "1" -> QRISPayment()
                            "2" -> {
                                print("Masukkan Nomor Kartu (16 digit): ")
                                val card = readLine()?.trim().orEmpty()
                                print("Masukkan CVV (3 digit): ")
                                val cvv = readLine()?.trim().orEmpty()
                                CreditCardPayment(card, cvv)
                            }
                            "3" -> {
                                print("Masukkan Nama Bank (contoh: BCA): ")
                                val bank = readLine()?.trim().orEmpty()
                                print("Masukkan Nomor Rekening: ")
                                val rek = readLine()?.trim().orEmpty()
                                BankTransferPayment(rek, if (bank.isBlank()) "BCA" else bank)
                            }
                            else -> {
                                println(" Pilihan tidak valid, default menggunakan QRIS.")
                                QRISPayment()
                            }
                        }

                        val order = system.checkout(currentUser, currentUser.cart, payment)
                        if (order != null) {
                            order.displayOrder()
                        }
                    }
                }
                "6" -> {
                    currentUser.displayInfo()
                    val history = currentUser.getOrderHistory()
                    if (history.isEmpty()) {
                        println("   (Belum ada riwayat pesanan)")
                    } else {
                        println("\nDaftar Pesanan:")
                        history.forEachIndexed { idx, ord ->
                            println("${idx + 1}. [${ord.id}] Status: ${ord.status.display()} | Total: Rp ${ord.totalPrice.toLong()}")
                        }
                    }
                }
                "7" -> {
                    system.displaySalesReport()
                }
                "8" -> {
                    println(" Anda telah berhasil logout.")
                    currentUser = null
                }
                else -> {
                    println("Pilihan tidak valid, silakan coba lagi.")
                }
            }
        }
    }
}