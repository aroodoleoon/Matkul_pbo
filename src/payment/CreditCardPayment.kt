package payment

/**
 * Implementasi metode pembayaran menggunakan Kartu Kredit.
 *
 * Mengimplementasikan antarmuka [PaymentMethod] dengan verifikasi panjang digit kartu kredit (16 digit),
 * kode verifikasi keamanan CVV (3 digit), serta pembebanan biaya admin sebesar 2%.
 *
 * @property cardNumber Nomor kartu kredit yang terdiri dari 16 digit angka.
 * @property cvv Nomor Card Verification Value (CVV) yang terdiri dari 3 digit angka.
 */
class CreditCardPayment(
    val cardNumber: String,
    val cvv: String
) : PaymentMethod {

    /** Nama metode pembayaran. */
    override val name: String = "Kartu Kredit"

    /**
     * Memproses transaksi kartu kredit dan memvalidasi format kartu serta CVV.
     *
     * @param amount Nominal tagihan yang harus dibayar dalam Rupiah.
     * @return [PaymentResult.Success] jika nomor kartu dan CVV sah, atau [PaymentResult.Failed] jika format tidak sesuai.
     */
    override fun processPayment(amount: Double): PaymentResult {
        if (cardNumber.length != 16) {
            return PaymentResult.Failed("Nomor kartu tidak valid (harus 16 digit)", 401)
        }
        if (cvv.length != 3) {
            return PaymentResult.Failed("CVV tidak valid (harus 3 digit)", 402)
        }
        return PaymentResult.Success("TX-CC-${System.currentTimeMillis()}")
    }

    /**
     * Menghitung besaran biaya admin transaksi kartu kredit sebesar 2% dari total belanja.
     *
     * @param amount Nominal transaksi belanja dalam Rupiah.
     * @return Biaya admin dalam Rupiah (2% dari [amount]).
     */
    override fun getFee(amount: Double): Double {
        return amount * 0.02
    }
}
