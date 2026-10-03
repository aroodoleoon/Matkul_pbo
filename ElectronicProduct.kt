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
     * Menghitung diskon produk elektronik.
     *
     * Aturan diskon:
     * - Diskon dasar 5%
     * - Tambahan 10% jika produk premium
     * - Tambahan 5% jika garansi lebih dari 24 bulan
     *
     * @return total diskon dalam Rupiah
     *
     * Alasan dari mengganti pemanggilan data 'price' menjadi fun 'getPriceValue()' karena sifat data 'price' private
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
     * Mengembalikan kategori produk.
     *
     * @return "Elektronik"
     */
    override fun getCategory(): String {
        return "Elektronik"
    }


    /**
     * Menampilkan informasi produk elektronik.
     *
     * Method superclass dipanggil terlebih dahulu menggunakan
     * super.displayInfo(), kemudian informasi khusus elektronik
     * ditampilkan.
     */
    override fun displayInfo() {

        super.displayInfo()

        println("Merek      : $brand")
        println("Garansi    : $warrantyMonths bulan")
        println(
            "Premium    : ${
                if (isPremium) "✅ Ya" else "❌ Tidak"
            }"
        )

        println("=".repeat(50))
    }
}