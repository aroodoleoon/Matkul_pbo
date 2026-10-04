package payment

import java.util.UUID

/**
 * Implementasi metode pembayaran digital menggunakan QRIS (Quick Response Code Indonesian Standard).
 *
 * Mengimplementasikan antarmuka [PaymentMethod] dengan mekanisme pembuatan kode QR dinamis,
 * batas waktu kedaluwarsa, dan bebas biaya admin (Rp 0).
 *
 * @property merchantName Nama identitas merchant yang terdaftar di QRIS.
 * @property expiryMinutes Batas waktu berlakunya kode QR dalam menit (default: 15 menit).
 */
class QRISPayment(
    private val merchantName: String = "ECommerce Store",
    private val expiryMinutes: Int = 15
) : PaymentMethod {

    /** Nama metode pembayaran. */
    override val name: String = "QRIS"

    /** Alias untuk properti [name]. */
    val methodName: String get() = name

    /** Kode QR yang aktif digenerate. */
    private var qrCode: String? = null

    /** Waktu dalam milidetik saat kode QR digenerate. */
    private var generatedAt: Long = 0L

    /** Status penanda apakah QRIS telah lunas dibayar. */
    var isPaid: Boolean = false
        private set

    /**
     * Membuat kode QR unik berbasis UUID untuk nominal tagihan belanja tertentu.
     *
     * @param amount Nominal tagihan yang harus dibayar dalam Rupiah.
     * @return String kode representasi QR unik.
     */
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

    /**
     * Memeriksa apakah kode QR yang dibuat telah melebihi batas waktu kedaluwarsa.
     *
     * @return `true` jika waktu telah melebihi [expiryMinutes].
     */
    private fun isExpired(): Boolean =
        System.currentTimeMillis() - generatedAt > expiryMinutes * 60_000L

    /**
     * Memproses transaksi pembayaran QRIS sesuai kontrak [PaymentMethod].
     *
     * @param amount Nominal yang dibayarkan.
     * @return [PaymentResult.Success] jika pembayaran valid, atau [PaymentResult.Failed] jika nominal tidak valid atau QR kedaluwarsa.
     */
    override fun processPayment(amount: Double): PaymentResult {
        if (amount <= 0) {
            println("Pembayaran gagal: nominal tidak valid.")
            return PaymentResult.Failed("Nominal tidak valid", 403)
        }

        val code = generateQR(amount)

        if (isExpired()) {
            println("Pembayaran gagal: kode QR kedaluwarsa.")
            return PaymentResult.Failed("Kode QR kedaluwarsa", 403)
        }

        // Simulasi pemindaian & konfirmasi dari aplikasi e-wallet/mobile banking
        println("Memindai $code ... pembayaran dikonfirmasi.")
        isPaid = true
        println("Pembayaran QRIS berhasil.")
        return PaymentResult.Success(code)
    }

    /**
     * Menjalankan pemrosesan pembayaran dan mengembalikan nilai boolean sederhana.
     *
     * @param amount Nominal transaksi.
     * @return `true` jika status pembayaran adalah Success.
     */
    fun processPaymentBoolean(amount: Double): Boolean {
        return processPayment(amount) is PaymentResult.Success
    }

    /**
     * Mengembalikan biaya admin untuk transaksi QRIS (gratis / Rp 0).
     *
     * @param amount Nominal transaksi.
     * @return Nilai `0.0`.
     */
    override fun getFee(amount: Double): Double = 0.0

    /**
     * Representasi teks dari objek [QRISPayment].
     */
    override fun toString(): String = "QRISPayment(merchant=$merchantName, paid=$isPaid)"
}
