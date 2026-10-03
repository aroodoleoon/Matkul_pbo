import java.util.UUID

/**
 * Pembayaran menggunakan QRIS.
 * Mengimplementasikan PaymentMethod.
 */
class QRISPayment(
    private val merchantName: String = "ECommerce Store",
    private val expiryMinutes: Int = 15
) : PaymentMethod {

    override val methodName: String = "QRIS"

    private var qrCode: String? = null
    private var generatedAt: Long = 0L
    var isPaid: Boolean = false
        private set

    /** Membuat kode QR unik untuk tagihan. */
    fun generateQR(amount: Double): String {
        val code = "QRIS-" + UUID.randomUUID().toString().take(8).uppercase()
        qrCode = code
        generatedAt = System.currentTimeMillis()
        isPaid = false
        println("=== QRIS ===")
        println("Merchant : $merchantName")
        println("Nominal  : Rp${"%,.0f".format(amount)}")
        println("Kode QR  : $code")
        println("Berlaku  : $expiryMinutes menit")
        return code
    }

    private fun isExpired(): Boolean =
        System.currentTimeMillis() - generatedAt > expiryMinutes * 60_000L

    /** Memproses pembayaran. Mengembalikan true jika berhasil. */
    override fun processPayment(amount: Double): Boolean {
        if (amount <= 0) {
            println("Pembayaran gagal: nominal tidak valid.")
            return false
        }

        val code = generateQR(amount)

        if (isExpired()) {
            println("Pembayaran gagal: kode QR kedaluwarsa.")
            return false
        }

        // Simulasi pemindaian & konfirmasi dari aplikasi e-wallet/mobile banking
        println("Memindai $code ... pembayaran dikonfirmasi.")
        isPaid = true
        println("Pembayaran QRIS berhasil.")
        return true
    }

    override fun toString(): String = "QRISPayment(merchant=$merchantName, paid=$isPaid)"
}
