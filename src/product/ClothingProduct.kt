package product

/**
 * ClothingProduct - produk pakaian, turunan dari Product.
 * Aturan diskon:
 *  1. Diskon seasonal jika isSeasonal = true
 *  2. Diskon ukuran besar jika size XL ke atas (XL, XXL, XXXL)
 * Kedua diskon dijumlahkan (maksimal 100%).
 */
class ClothingProduct(
    id: String,
    name: String,
    price: Double,
    stock: Int,
    val size: String,                 // contoh: "S", "M", "L", "XL", "XXL"
    val color: String,
    val material: String,             // contoh: "Katun", "Polyester"
    val isSeasonal: Boolean = false   // true = produk musiman
) : Product(id, name, price, stock) {

    companion object {
        // TODO: cocokkan angka dengan modul
        const val SEASONAL_DISCOUNT_PERCENT = 30.0
        const val LARGE_SIZE_DISCOUNT_PERCENT = 10.0
        val LARGE_SIZES = setOf("XL", "XXL", "XXXL")
    }

    fun isLargeSize(): Boolean = size.uppercase() in LARGE_SIZES

    // Total persentase diskon dari semua aturan
    fun getDiscountPercent(): Double {
        var percent = 0.0
        if (isSeasonal) percent += SEASONAL_DISCOUNT_PERCENT
        if (isLargeSize()) percent += LARGE_SIZE_DISCOUNT_PERCENT
        return minOf(percent, 100.0)
    }

    // Abstract dari Product: jumlah diskon dalam Rupiah
    override fun calculateDiscount(): Double =
        getPriceValue() * getDiscountPercent() / 100

    // Abstract dari Product: nama kategori
    override fun getCategory(): String = "Pakaian"

    // Info umum dicetak parent, lalu ditambah info khusus pakaian
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