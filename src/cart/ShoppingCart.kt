package cart

import product.Product

/**
 * Kelas untuk merepresentasikan keranjang belanja milik seorang pengguna.
 *
 * Keranjang menyimpan pasangan produk beserta jumlah belanjanya (Product -> Quantity).
 * Saat produk dimasukkan ke keranjang, stok produk langsung dikurangi dari inventaris,
 * dan stok dikembalikan jika produk dibatalkan/dihapus dari keranjang.
 *
 * @property owner Nama atau username pemilik keranjang belanja.
 */
class ShoppingCart(private val owner: String = "User") {

    /**
     * Koleksi internal pasangan produk dan kuantitasnya (private untuk menjamin enkapsulasi).
     */
    private val items = mutableMapOf<Product, Int>()

    /**
     * Total seluruh akumulasi kuantitas barang di keranjang belanja.
     * Bersifat read-only dari luar; perubahan nilai hanya dapat dilakukan oleh metode internal kelas (private setter).
     */
    var totalItems: Int = 0
        private set

    /**
     * Menambahkan produk ke dalam keranjang dan mengurangi stok produk terkait.
     *
     * @param product Objek produk yang akan ditambahkan.
     * @param quantity Jumlah kuantitas yang ingin dibeli (harus lebih besar dari 0).
     * @return `true` jika berhasil ditambahkan, `false` jika kuantitas tidak valid atau stok habis.
     */
    fun addItem(product: Product, quantity: Int): Boolean {
        if (quantity <= 0) {
            println("❌ Jumlah harus lebih dari 0")
            return false
        }

        if (!product.reduceStock(quantity)) {
            println("❌ Stok tidak mencukupi (tersedia: ${product.stock})")
            return false
        }

        items[product] = items.getOrDefault(product, 0) + quantity
        totalItems += quantity
        println("✅ ${product.name} x$quantity ditambahkan ke keranjang")
        return true
    }

    /**
     * Menghapus produk dari keranjang dan mengembalikan kuantitas stoknya ke inventaris.
     *
     * @param product Objek produk yang akan dihapus dari keranjang.
     * @return `true` jika produk ditemukan dan berhasil dihapus, `false` jika tidak ditemukan.
     */
    fun removeItem(product: Product): Boolean {
        val quantity = items[product] ?: return false
        items.remove(product)
        totalItems -= quantity
        // Kembalikan stok (reduceStock dengan nilai negatif = menambah stok kembali)
        product.reduceStock(-quantity)
        println("✅ ${product.name} dihapus dari keranjang")
        return true
    }

    /**
     * Menghitung total harga seluruh barang di keranjang setelah potongan diskon masing-masing produk.
     *
     * @return Total nilai belanja dalam satuan Rupiah.
     */
    fun getTotalPrice(): Double {
        return items.entries.sumOf { (product, quantity) ->
            product.getDiscountedPrice() * quantity
        }
    }

    /**
     * Menghitung total seluruh potongan diskon yang diperoleh atas isi keranjang.
     *
     * @return Total potongan diskon dalam satuan Rupiah.
     */
    fun getTotalDiscount(): Double {
        return items.entries.sumOf { (product, quantity) ->
            product.calculateDiscount() * quantity
        }
    }

    /**
     * Mengambil salinan tidak dapat dimodifikasi (read-only) dari isi keranjang belanja.
     *
     * @return Map berisi produk beserta kuantitas belanjanya.
     */
    fun getItems(): Map<Product, Int> = items.toMap()

    /**
     * Memeriksa apakah keranjang belanja saat ini dalam keadaan kosong.
     *
     * @return `true` jika tidak ada item di dalam keranjang, sebaliknya `false`.
     */
    fun isEmpty(): Boolean = items.isEmpty()

    /**
     * Mengosongkan seluruh isi keranjang belanja setelah proses checkout berhasil.
     * Stok barang tidak dikembalikan ke inventaris karena transaksi telah selesai.
     */
    fun clear() {
        items.clear()
        totalItems = 0
    }

    /**
     * Menampilkan isi keranjang belanja beserta rincian diskon dan total tagihan ke konsol terminal.
     */
    fun displayCart() {
        println("=".repeat(50))
        println("🛒 KERANJANG BELANJA - $owner")
        println("=".repeat(50))

        if (items.isEmpty()) {
            println("   Keranjang kosong")
        } else {
            items.forEach { (product, quantity) ->
                println("${product.id} | ${product.name} | Rp ${formatRupiah(product.getDiscountedPrice())} | Qty: $quantity")
                println("   (Diskon: Rp ${formatRupiah(product.calculateDiscount() * quantity)})")
            }
            println("-".repeat(50))
            println("Total Diskon : Rp ${formatRupiah(getTotalDiscount())}")
            println("Total Belanja: Rp ${formatRupiah(getTotalPrice())}")
        }
        println("=".repeat(50))
    }

    /**
     * Memformat angka menjadi representasi format Rupiah dengan pemisah titik ribuan.
     *
     * @param nominal Angka nominal yang akan diformat.
     * @return String berformat angka Rupiah.
     */
    private fun formatRupiah(nominal: Double): String {
        val str = nominal.toLong().toString()
        val builder = StringBuilder()
        var count = 0
        for (i in str.length - 1 downTo 0) {
            builder.insert(0, str[i])
            count++
            if (count % 3 == 0 && i > 0) {
                builder.insert(0, ".")
            }
        }
        return builder.toString()
    }
}
