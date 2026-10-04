package product

/**
 * Kelas abstrak dasar yang merepresentasikan produk dalam sistem e-commerce.
 *
 * Menerapkan prinsip enkapsulasi (harga [price] bersifat private),
 * abstraksi (metode [calculateDiscount] dan [getCategory] abstrak),
 * serta polimorfisme untuk perhitungan harga dan penampil informasi.
 *
 * @property id Identifikasi unik produk.
 * @property name Nama dari produk.
 * @property price Harga dasar produk (dienkapsulasi secara private).
 * @property stock Jumlah unit stok produk yang tersedia di inventaris.
 */
abstract class Product(
    val id: String,
    val name: String,
    private var price: Double,
    var stock: Int
) {

    /**
     * Mendapatkan harga produk dalam format mata uang Rupiah.
     */
    val formattedPrice: String
        get() = "Rp ${formatRupiah(price)}"

    /**
     * Menghitung jumlah potongan diskon yang diberikan kepada produk.
     * Setiap subclass memiliki logika perhitungan diskon masing-masing.
     *
     * @return Nilai potongan diskon dalam satuan Rupiah.
     */
    abstract fun calculateDiscount(): Double

    /**
     * Mendapatkan nama kategori produk.
     *
     * @return Nama kategori produk.
     */
    abstract fun getCategory(): String

    /**
     * Menghitung harga akhir produk setelah dikurangi diskon.
     *
     * @return Nilai harga setelah diskon dalam satuan Rupiah.
     */
    open fun getDiscountedPrice(): Double {
        return price - calculateDiscount()
    }

    /**
     * Menampilkan informasi lengkap mengenai produk ke konsol terminal.
     */
    open fun displayInfo() {
        println("=".repeat(50))
        println("📦 ${getCategory()} - $name")
        println("ID         : $id")
        println("Harga      : $formattedPrice")
        println("Diskon     : Rp ${formatRupiah(calculateDiscount())}")
        println("Harga Akhir: Rp ${formatRupiah(getDiscountedPrice())}")
        println("Stok       : $stock")
        println("=".repeat(50))
    }

    /**
     * Mengurangi stok produk sesuai jumlah kuantitas pembelian.
     *
     * @param quantity Jumlah unit stok yang ingin dikurangi.
     * @return `true` jika stok mencukupi dan berhasil dikurangi, `false` jika stok tidak mencukupi.
     */
    fun reduceStock(quantity: Int): Boolean {
        return if (stock >= quantity) {
            stock -= quantity
            true
        } else {
            false
        }
    }

    /**
     * Mengambil nilai harga asli produk.
     * Bersifat protected agar subclass dapat mengakses nilai harga untuk menghitung diskon.
     *
     * @return Nilai harga asli produk.
     */
    protected fun getPriceValue(): Double {
        return price
    }

    /**
     * Mengubah angka desimal menjadi format mata uang Rupiah dengan pemisah titik ribuan.
     *
     * @param nominal Nilai angka yang akan diformat.
     * @return String angka berformat Rupiah tanpa prefix 'Rp'.
     */
    protected fun formatRupiah(nominal: Double): String {
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