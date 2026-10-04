package product

/**
 * Representasi produk makanan atau minuman dalam sistem e-commerce.
 *
 * Mewarisi kelas [Product] dengan penanganan tanggal kedaluwarsa, berat kemasan,
 * label sertifikasi halal, dan diskon untuk makanan organik.
 *
 * @param id Identifikasi unik produk.
 * @param name Nama produk makanan/minuman.
 * @param price Harga asli produk.
 * @param stock Jumlah unit stok yang tersedia.
 * @property expiryDate Representasi teks tanggal kedaluwarsa (contoh: "2026-10-10").
 * @property daysUntilExpiry Jumlah sisa hari hingga tanggal kedaluwarsa (nilai negatif menandakan sudah kedaluwarsa).
 * @property weightInGram Berat bersih produk dalam satuan gram.
 * @property isOrganic Menandakan apakah produk merupakan makanan organik bersertifikasi.
 * @property isHalal Menandakan apakah produk berstatus halal.
 */
class FoodProduct(
    id: String,
    name: String,
    price: Double,
    stock: Int,
    val expiryDate: String,
    var daysUntilExpiry: Int,
    val weightInGram: Int,
    val isOrganic: Boolean = false,
    val isHalal: Boolean = true
) : Product(id, name, price, stock) {

    /**
     * Objek pendamping yang memuat konstanta terkait diskon dan ambang batas kedaluwarsa.
     */
    companion object {
        /** Persentase diskon untuk produk organik (20%). */
        const val ORGANIC_DISCOUNT = 0.20

        /** Batas toleransi sisa hari (3 hari) untuk peringatan produk hampir kedaluwarsa. */
        const val NEAR_EXPIRY_DAYS = 3
    }

    /**
     * Memeriksa apakah produk sudah melewati masa tanggal kedaluwarsa.
     *
     * @return `true` jika [daysUntilExpiry] bernilai negatif.
     */
    fun isExpired(): Boolean = daysUntilExpiry < 0

    /**
     * Memeriksa apakah produk berada dalam masa kritis mendekati tanggal kedaluwarsa.
     *
     * @return `true` jika [daysUntilExpiry] berada di antara 0 dan [NEAR_EXPIRY_DAYS].
     */
    fun isNearExpiry(): Boolean = daysUntilExpiry in 0..NEAR_EXPIRY_DAYS

    /**
     * Memajukan waktu simulasi sebanyak [days] hari, mengurangi nilai [daysUntilExpiry].
     *
     * @param days Jumlah hari yang telah berlalu (default: 1).
     */
    fun passDays(days: Int = 1) {
        daysUntilExpiry -= days
    }

    /**
     * Menghitung nilai diskon produk makanan.
     * Diskon sebesar 20% hanya diberikan jika produk berstatus organik ([isOrganic]).
     *
     * @return Nilai potongan harga diskon dalam satuan Rupiah.
     */
    override fun calculateDiscount(): Double =
        if (isOrganic) getPriceValue() * ORGANIC_DISCOUNT else 0.0

    /**
     * Mengembalikan nama kategori produk makanan.
     *
     * @return String `"Makanan"`.
     */
    override fun getCategory(): String = "Makanan"

    /**
     * Menampilkan informasi detail produk makanan ke konsol terminal.
     * Memanggil [super.displayInfo], lalu menampilkan berat, status organik,
     * status halal, tanggal kedaluwarsa, serta peringatan jika kedaluwarsa/hampir kedaluwarsa.
     */
    override fun displayInfo() {
        super.displayInfo()
        println("Berat      : ${weightInGram}g")
        println("Organik    : ${if (isOrganic) "Ya (diskon 20%)" else "Tidak"}")
        println("Halal      : ${if (isHalal) "Ya" else "Tidak"}")
        println("Kadaluarsa : $expiryDate (sisa $daysUntilExpiry hari)")
        when {
            isExpired() -> println("Status     : KADALUARSA (tidak dapat dibeli)")
            isNearExpiry() -> println("Status     : Hampir kadaluarsa!")
        }
        println("=".repeat(50))
    }
}