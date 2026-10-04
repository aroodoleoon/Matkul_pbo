package payment

/**
 * Implementasi metode pembayaran melalui Transfer Rekening Bank (VA / Transfer Antar Bank).
 *
 * Mengimplementasikan antarmuka [PaymentMethod] dengan verifikasi panjang digit rekening bank
 * serta perhitungan biaya administrasi berbasis persentase (1%) dengan nilai minimum Rp 5.000.
 *
 * @property accountNumber Nomor rekening bank tujuan/sumber pembayaran (minimal 8 karakter).
 * @property bankName Nama lembaga perbankan (contoh: "BCA", "Mandiri", "BNI", "BRI").
 */
class BankTransferPayment(
    val accountNumber: String = "1234567890",
    val bankName: String = "BCA"
) : PaymentMethod {

    /** Nama tampilan metode pembayaran dengan keterangan nama bank. */
    override val name: String = "Transfer Bank ($bankName)"

    /**
     * Memproses transaksi transfer bank dan memvalidasi keabsahan nomor rekening.
     *
     * @param amount Nominal tagihan yang harus dibayar dalam Rupiah.
     * @return [PaymentResult.Success] jika nomor rekening valid, atau [PaymentResult.Failed] jika panjang rekening kurang dari 8 digit.
     */
    override fun processPayment(amount: Double): PaymentResult {
        return if (accountNumber.length >= 8) {
            PaymentResult.Success("BT-${System.currentTimeMillis()}")
        } else {
            PaymentResult.Failed("Nomor rekening tidak valid", 404)
        }
    }

    /**
     * Menghitung besaran biaya administrasi transfer bank (1% dari transaksi belanja, minimal Rp 5.000).
     *
     * @param amount Nominal transaksi belanja dalam Rupiah.
     * @return Biaya admin dalam satuan Rupiah.
     */
    override fun getFee(amount: Double): Double {
        val fee = amount * 0.01
        return maxOf(fee, 5000.0)
    }
}
