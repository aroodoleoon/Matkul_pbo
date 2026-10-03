package payment

class CreditCardPayment(
    val cardNumber: String,
    val cvv: String
) : PaymentMethod {
    override val name: String = "Kartu Kredit"

    override fun processPayment(amount: Double): PaymentResult {
        if (cardNumber.length != 16) {
            return PaymentResult.Failed("Nomor kartu tidak valid (harus 16 digit)", 401)
        }
        if (cvv.length != 3) {
            return PaymentResult.Failed("CVV tidak valid (harus 3 digit)", 402)
        }
        return PaymentResult.Success("TX-CC-${System.currentTimeMillis()}")
    }

    override fun getFee(amount: Double): Double {
        return amount * 0.02 // 2% fee
    }
}
