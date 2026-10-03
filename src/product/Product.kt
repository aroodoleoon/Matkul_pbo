package product

abstract class Product(
    val id: String,
    val name: String,
    private var price: Double,
    var stock: Int
) {

    /**
     * Mendapatkan harga produk dalam format Rupiah.
     */
    val formattedPrice: String
        get() = "Rp ${formatRupiah(price)}"


    /**
     * Menghitung jumlah diskon yang diberikan
     * kepada produk.
     *
     * Setiap subclass memiliki aturan diskon
     * masing-masing.
     *
     * @return jumlah diskon dalam Rupiah
     */
    abstract fun calculateDiscount(): Double


    /**
     * Mendapatkan kategori produk.
     *
     * @return nama kategori produk
     */
    abstract fun getCategory(): String


    /**
     * Menghitung harga produk setelah dikurangi diskon.
     *
     * @return harga setelah diskon
     *
     * Pengganti data 'price' yang sifatnya private sehingga tidak bisa dipanggil dari luar class ataupun subclass
     */
    open fun getDiscountedPrice(): Double {
        return price - calculateDiscount()
    }


    /**
     * Menampilkan informasi lengkap mengenai produk.
     */
    open fun displayInfo() {
        println("=".repeat(50))
        println("📦 ${getCategory()} - $name")
        println("ID         : $id")
        println("Harga      : $formattedPrice")
        println(
            "Diskon     : Rp ${
                formatRupiah(calculateDiscount())
            }"
        )
        println(
            "Harga Akhir: Rp ${
                formatRupiah(getDiscountedPrice())
            }"
        )
        println("Stok       : $stock")
        println("=".repeat(50))
    }


    /**
     * Mengurangi stok produk sesuai jumlah pembelian.
     *
     * @param quantity jumlah stok yang ingin dikurangi
     * @return true jika stok mencukupi, false jika tidak
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
     *
     * Method ini bersifat protected agar hanya class
     * turunan yang dapat mengakses harga.
     *
     * Digunakan oleh subclass untuk menghitung diskon.
     *
     * @return harga asli produk
     */
    protected fun getPriceValue(): Double {
        return price
    }


    /**
     * Mengubah angka menjadi format Rupiah.
     *
     * @param nominal nominal yang akan diformat
     * @return angka dalam format Rupiah tanpa simbol Rp
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