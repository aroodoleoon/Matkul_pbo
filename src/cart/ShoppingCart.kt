package cart

import product.Product

/**
 * Kelas untuk merepresentasikan keranjang belanja milik seorang pengguna.
 *
 * Keranjang menyimpan produk beserta jumlahnya (Product -> Quantity).
 * Saat produk dimasukkan ke keranjang, stok produk langsung dikurangi,
 * dan dikembalikan jika produk dihapus dari keranjang.
 *
 * @property owner Username pemilik keranjang (private)
 */
class ShoppingCart(private val owner: String = "User") {

    // ============================================================
    // PROPERTI
    // ============================================================

    /** Isi keranjang: pasangan produk dan jumlahnya (private agar tidak diubah dari luar). */
    private val items = mutableMapOf<Product, Int>()

    /**
     * Total seluruh kuantitas barang di keranjang.
     * Hanya bisa dibaca dari luar; perubahan hanya lewat metode kelas ini (private setter).
     */
    var totalItems: Int = 0
        private set

    // ============================================================
    // METODE
    // ============================================================

    /**
     * Menambahkan produk ke keranjang dan mengurangi stok produk.
     *
     * @param product Produk yang ditambahkan
     * @param quantity Jumlah yang ditambahkan (harus lebih dari 0)
     * @return `true` jika berhasil, `false` jika jumlah tidak valid atau stok tidak cukup
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
     * Menghapus produk dari keranjang dan mengembalikan stoknya.
     *
     * @param product Produk yang dihapus
     * @return `true` jika produk ada di keranjang dan berhasil dihapus, `false` jika tidak ada
     */
    fun removeItem(product: Product): Boolean {
        val quantity = items[product] ?: return false
        items.remove(product)
        totalItems -= quantity
        // Kembalikan stok (reduceStock dengan nilai negatif = menambah stok)
        product.reduceStock(-quantity)
        println("✅ ${product.name} dihapus dari keranjang")
        return true
    }

    /**
     * Menghitung total harga seluruh isi keranjang setelah diskon.
     *
     * @return Total belanja dalam Rupiah
     */
    fun getTotalPrice(): Double {
        return items.entries.sumOf { (product, quantity) ->
            product.getDiscountedPrice() * quantity
        }
    }

    /**
     * Menghitung total diskon seluruh isi keranjang.
     *
     * @return Total diskon dalam Rupiah
     */
    fun getTotalDiscount(): Double {
        return items.entries.sumOf { (product, quantity) ->
            product.calculateDiscount() * quantity
        }
    }

    /**
     * Mengambil salinan (read-only) isi keranjang.
     *
     * Mengembalikan salinan agar Order yang dibuat dari keranjang
     * tidak ikut kosong ketika [clear] dipanggil.
     *
     * @return Map produk beserta jumlahnya
     */
    fun getItems(): Map<Product, Int> = items.toMap()

    /**
     * Mengecek apakah keranjang kosong.
     *
     * @return `true` jika tidak ada item
     */
    fun isEmpty(): Boolean = items.isEmpty()

    /**
     * Mengosongkan keranjang setelah checkout berhasil.
     *
     * Stok TIDAK dikembalikan karena barang sudah resmi dibeli.
     */
    fun clear() {
        items.clear()
        totalItems = 0
    }

    /**
     * Menampilkan isi keranjang beserta total diskon dan total belanja.
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
                // println("${product.name} x$quantity = Rp ${formatRupiah(product.getDiscountedPrice() * quantity)}")
                println("   (Diskon: Rp ${formatRupiah(product.calculateDiscount() * quantity)})")
            }
            println("-".repeat(50))
            println("Total Diskon : Rp ${formatRupiah(getTotalDiscount())}")
            println("Total Belanja: Rp ${formatRupiah(getTotalPrice())}")
        }
        println("=".repeat(50))
    }

    /**
     * Memformat angka menjadi format Rupiah dengan pemisah titik ribuan.
     *
     * @param nominal Angka yang diformat
     * @return String berformat, contoh `15.000.000`
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
