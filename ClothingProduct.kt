package product

/**
 * ClothingProduct - produk pakaian, turunan dari Product.
 * Aturan diskon: persentase tetap dari harga asli.
 */
class ClothingProduct(
    id: String,
    name: String,
    price: Double,
    stock: Int,
    val size: String,                  // contoh: "S", "M", "L", "XL"
    val color: String,
    val material: String,              // contoh: "Katun", "Polyester"
    val discountPercent: Double = 0.0  // diskon 0 - 100 (%)
) : Product(id, name, price, stock) {

    init {
        require(discountPercent in 0.0..100.0) { "Diskon harus antara 0 sampai 100" }
    }

    // Abstract dari Product: jumlah diskon dalam Rupiah
    override fun calculateDiscount(): Double =
        getPriceValue() * discountPercent / 100

    // Abstract dari Product: nama kategori
    override fun getCategory(): String = "Pakaian"

    // Info umum dicetak parent, lalu ditambah info khusus pakaian
    override fun displayInfo() {
        super.displayInfo()
        println("Ukuran     : $size")
        println("Warna      : $color")
        println("Bahan      : $material")
        println("=".repeat(50))
    }
}