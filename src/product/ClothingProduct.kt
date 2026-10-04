package product

/**
 * Representasi produk kategori pakaian (clothing) dalam sistem e-commerce.
 *
 * Mewarisi kelas [Product] dengan aturan diskon khusus:
 * 1. Diskon musiman ([isSeasonal]) sebesar 30%.
 * 2. Diskon ukuran besar ([isLargeSize]) untuk ukuran XL ke atas sebesar 10%.
 *
 * Kedua diskon dapat diakumulasikan hingga batas maksimal 100%.
 *
 * @param id Identifikasi unik produk.
 * @param name Nama produk pakaian.
 * @param price Harga asli produk pakaian.
 * @param stock Jumlah unit stok yang tersedia.
 * @property size Ukuran pakaian (contoh: "S", "M", "L", "XL", "XXL").
 * @property color Warna produk pakaian (contoh: "Hitam", "Navy", "Merah").
 * @property material Jenis bahan kain (contoh: "Katun", "Polyester", "Wol").
 * @property isSeasonal Menandakan apakah produk merupakan barang promo musiman.
 */
class ClothingProduct(
    id: String,
    name: String,
    price: Double,
    stock: Int,
    val size: String,
    val color: String,
    val material: String,
    val isSeasonal: Boolean = false
) : Product(id, name, price, stock) {

    /**
     * Objek pendamping yang memuat konstanta diskon dan ukuran besar pakaian.
     */
    companion object {
        /** Persentase diskon untuk produk pakaian musiman (30%). */
        const val SEASONAL_DISCOUNT_PERCENT = 30.0

        /** Persentase diskon tambahan untuk produk berukuran besar (10%). */
        const val LARGE_SIZE_DISCOUNT_PERCENT = 10.0

        /** Himpunan ukuran pakaian yang dikategorikan sebagai ukuran besar. */
        val LARGE_SIZES = setOf("XL", "XXL", "XXXL")
    }

    /**
     * Memeriksa apakah ukuran pakaian termasuk dalam kategori ukuran besar ([LARGE_SIZES]).
     *
     * @return `true` jika ukuran pakaian adalah XL, XXL, atau XXXL.
     */
    fun isLargeSize(): Boolean = size.uppercase() in LARGE_SIZES

    /**
     * Menghitung total akumulasi persentase diskon berdasarkan aturan musiman dan ukuran pakaian.
     *
     * @return Total persentase diskon dalam persen (rentang 0.0 sampai 100.0).
     */
    fun getDiscountPercent(): Double {
        var percent = 0.0
        if (isSeasonal) percent += SEASONAL_DISCOUNT_PERCENT
        if (isLargeSize()) percent += LARGE_SIZE_DISCOUNT_PERCENT
        return minOf(percent, 100.0)
    }

    /**
     * Menghitung potongan diskon pakaian dalam satuan Rupiah.
     *
     * @return Nilai potongan harga diskon dalam Rupiah.
     */
    override fun calculateDiscount(): Double =
        getPriceValue() * getDiscountPercent() / 100

    /**
     * Mengembalikan nama kategori produk pakaian.
     *
     * @return String `"Pakaian"`.
     */
    override fun getCategory(): String = "Pakaian"

    /**
     * Menampilkan informasi detail pakaian ke konsol terminal.
     * Memanggil [super.displayInfo], lalu menampilkan ukuran, warna, bahan,
     * status musiman, dan total persentase diskon.
     */
    override fun displayInfo() {
        super.displayInfo()
        println("Ukuran     : $size")
        println("Warna      : $color")
        println("Bahan      : $material")
        println("Seasonal   : ${if (isSeasonal) "Ya" else "Tidak"}")
        println("Diskon (%) : ${getDiscountPercent()}%")
        println("=".repeat(50))
    }
}