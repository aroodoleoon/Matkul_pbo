/**
 * Kontrak (abstraksi) untuk semua metode pembayaran.
 *
 * Setiap metode pembayaran (Kartu Kredit, QRIS, Transfer Bank) wajib
 * mengimplementasikan interface ini sehingga dapat diperlakukan secara
 * polimorfik oleh `User.checkout()`.
 */
interface PaymentMethod {

    /** Nama metode pembayaran yang ditampilkan ke pengguna. */
    val name: String

    /**
     * Memproses pembayaran sejumlah tertentu.
     *
     * @param amount Nominal yang dibayar dalam Rupiah
     * @return [PaymentResult] hasil pembayaran (Success, Failed, atau Pending)
     */
    fun processPayment(amount: Double): PaymentResult

    /**
     * Menghitung biaya admin untuk metode pembayaran ini.
     *
     * @param amount Nominal transaksi dalam Rupiah
     * @return Biaya admin dalam Rupiah
     */
    fun getFee(amount: Double): Double
}

/**
 * Sealed class untuk hasil pembayaran.
 *
 * Karena sealed, `when` pada [PaymentResult] bersifat eksaustif
 * (compiler memastikan semua kemungkinan hasil ditangani).
 */
sealed class PaymentResult {

    /**
     * Pembayaran berhasil.
     *
     * @property transactionId ID unik transaksi
     */
    data class Success(val transactionId: String) : PaymentResult()

    /**
     * Pembayaran gagal.
     *
     * @property reason Alasan kegagalan
     * @property errorCode Kode error (401 = kartu, 402 = CVV, 403 = QR, 404 = rekening)
     */
    data class Failed(val reason: String, val errorCode: Int) : PaymentResult()

    /** Pembayaran masih menunggu konfirmasi. */
    object Pending : PaymentResult()
}
