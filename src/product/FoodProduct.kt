package product

/**
 * FoodProduct - produk makanan/minuman, turunan dari Product.
 * Aturan diskon (sesuai modul): produk organik mendapat diskon 20%.
 * expiryDate     : teks tanggal untuk ditampilkan, contoh "2026-10-10"
 * daysUntilExpiry: sisa hari, dipakai untuk logika kadaluarsa
 */
class FoodProduct(
    id: String,
    name: String,
    price: Double,
    stock: Int,
    val expiryDate: String,
    var daysUntilExpiry: Int,        // negatif = sudah lewat
    val weightInGram: Int,
    val isOrganic: Boolean = false,
    val isHalal: Boolean = true
) : Product(id, name, price, stock) {

    companion object {
        const val ORGANIC_DISCOUNT = 0.20 // diskon organik 20%
        const val NEAR_EXPIRY_DAYS = 3    // batas peringatan "hampir kadaluarsa"
    }

    fun isExpired(): Boolean = daysUntilExpiry < 0

    fun isNearExpiry(): Boolean = daysUntilExpiry in 0..NEAR_EXPIRY_DAYS

    // Untuk simulasi: memajukan waktu sebanyak n hari
    fun passDays(days: Int = 1) {
        daysUntilExpiry -= days
    }

    // Abstract dari Product: diskon hanya untuk produk organik
    override fun calculateDiscount(): Double =
        if (isOrganic) getPriceValue() * ORGANIC_DISCOUNT else 0.0

    // Abstract dari Product: nama kategori
    override fun getCategory(): String = "Makanan"

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