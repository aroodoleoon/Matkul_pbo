package product

/**
 * Representasi produk kategori elektronik dalam sistem e-commerce.
 *
 * Mewarisi kelas [Product] dengan atribut khusus seperti merek, masa garansi,
 * dan status keanggotaan produk premium.
 *
 * @param id Identifikasi unik produk.
 * @param name Nama produk elektronik.
 * @param price Harga asli produk elektronik.
 * @param stock Jumlah unit stok yang tersedia.
 * @property brand Merek produsen elektronik (misal: "ASUS", "Samsung", "HP").
 * @property warrantyMonths Durasi garansi resmi dalam satuan bulan.
 * @property isPremium Menunjukkan apakah produk termasuk kategori barang premium/eksklusif.
 */
class ElectronicProduct(
    id: String,
    name: String,
    price: Double,
    stock: Int,
    val brand: String,
    val warrantyMonths: Int,
    val isPremium: Boolean
) : Product(
    id,
    name,
    price,
    stock
) {

    /**
     * Menghitung total potongan diskon produk elektronik.
     *
     * Aturan kalkulasi diskon:
     * - Diskon dasar: 5% dari harga produk
     * - Tambahan diskon jika produk premium: 10%
     * - Tambahan diskon jika garansi lebih dari 24 bulan: 5%
     *
     * @return Total potongan diskon dalam satuan Rupiah.
     */
    override fun calculateDiscount(): Double {
        var discount = 0.0

        // Diskon dasar 5%
        discount += getPriceValue() * 0.05

        // Diskon tambahan untuk produk premium
        if (isPremium) {
            discount += getPriceValue() * 0.10
        }

        // Diskon tambahan jika garansi > 24 bulan
        if (warrantyMonths > 24) {
            discount += getPriceValue() * 0.05
        }

        return discount
    }

    /**
     * Mengembalikan nama kategori produk elektronik.
     *
     * @return String `"Elektronik"`.
     */
    override fun getCategory(): String {
        return "Elektronik"
    }

    /**
     * Menampilkan informasi spesifik produk elektronik ke konsol terminal.
     * Memanggil [super.displayInfo] terlebih dahulu, kemudian mencetak merek,
     * masa garansi, dan label status produk premium.
     */
    override fun displayInfo() {
        super.displayInfo()
        println("Merek      : $brand")
        println("Garansi    : $warrantyMonths bulan")
        println("Premium    : ${if (isPremium) "✅ Ya" else "❌ Tidak"}")
        println("=".repeat(50))
    }
}