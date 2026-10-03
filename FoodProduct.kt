package product

/**
 * FoodProduct - produk makanan/minuman, turunan dari Product.
 * Aturan diskon: otomatis 20% jika hampir kadaluarsa (<= 3 hari).
 * Masa kadaluarsa disimpan sebagai sisa hari (Int).
 */
class FoodProduct(
    id: String,
    name: String,
    price: Double,
    stock: Int,
    var daysUntilExpiry: Int,        // sisa hari sebelum kadaluarsa (negatif = sudah lewat)
    val weightInGram: Int,
    val isHalal: Boolean = true
) : Product(id, name, price, stock) {

    companion object {
        const val NEAR_EXPIRY_DAYS = 3        // batas "hampir kadaluarsa"
        const val NEAR_EXPIRY_DISCOUNT = 0.20 // diskon 20%
    }

    fun isExpired(): Boolean = daysUntilExpiry < 0

    fun isNearExpiry(): Boolean = daysUntilExpiry in 0..NEAR_EXPIRY_DAYS

    // Untuk simulasi: memajukan waktu sebanyak n hari
    fun passDays(days: Int = 1) {
        daysUntilExpiry -= days
    }

    // Abstract dari Product: diskon hanya jika hampir kadaluarsa
    override fun calculateDiscount(): Double =
        if (isNearExpiry()) getPriceValue() * NEAR_EXPIRY_DISCOUNT else 0.0

    // Abstract dari Product: nama kategori
    override fun getCategory(): String = "Makanan"

    override fun displayInfo() {
        super.displayInfo()
        println("Berat      : ${weightInGram}g")
        println("Halal      : ${if (isHalal) "Ya" else "Tidak"}")
        println("Sisa Hari  : $daysUntilExpiry")
        when {
            isExpired() -> println("Status     : KADALUARSA (tidak dapat dibeli)")
            isNearExpiry() -> println("Status     : Hampir kadaluarsa, diskon otomatis")
        }
        println("=".repeat(50))
    }
}