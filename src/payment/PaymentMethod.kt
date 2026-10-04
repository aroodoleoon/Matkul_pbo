package payment

/**
 * Kontrak abstraksi antarmuka untuk semua metode pembayaran dalam sistem e-commerce.
 *
 * Menerapkan prinsip polimorfisme sehingga berbagai penyedia pembayaran
 * (QRIS, Kartu Kredit, Transfer Bank) dapat diproses seragam oleh sistem checkout.
 */
interface PaymentMethod {

    /**
     * Nama tampilan metode pembayaran yang disajikan kepada pengguna.
     */
    val name: String

    /**
     * Memproses transaksi pembayaran untuk nominal tertentu.
     *
     * @param amount Nominal transaksi yang harus dibayar dalam satuan Rupiah.
     * @return [PaymentResult] yang merepresentasikan status transaksi (Success, Failed, atau Pending).
     */
    fun processPayment(amount: Double): PaymentResult

    /**
     * Menghitung besaran biaya transaksi/admin untuk metode pembayaran ini.
     *
     * @param amount Nominal dasar transaksi belanja dalam Rupiah.
     * @return Biaya admin dalam satuan Rupiah.
     */
    fun getFee(amount: Double): Double
}

/**
 * Kelas tersegel (sealed class) yang merepresentasikan variasi hasil dari pemrosesan pembayaran.
 *
 * Mengamankan evaluasi ekspresi `when` agar bersifat mutlak/eksaustif tanpa memerlukan cabang `else`.
 */
sealed class PaymentResult {

    /**
     * Merepresentasikan transaksi pembayaran yang berhasil diselesaikan.
     *
     * @property transactionId Nomor referensi unik tanda terima transaksi.
     */
    data class Success(val transactionId: String) : PaymentResult()

    /**
     * Merepresentasikan transaksi pembayaran yang mengalami kegagalan.
     *
     * @property reason Penjelasan penyebab terjadinya kegagalan pembayaran.
     * @property errorCode Kode status error numerik (401 = kartu, 402 = CVV, 403 = QR, 404 = rekening).
     */
    data class Failed(val reason: String, val errorCode: Int) : PaymentResult()

    /**
     * Merepresentasikan transaksi pembayaran yang masih berstatus menunggu konfirmasi.
     */
    object Pending : PaymentResult()
}
