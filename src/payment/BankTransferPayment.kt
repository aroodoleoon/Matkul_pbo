package payment

class BankTransferPayment(
    val accountNumber: String = "1234567890",
    val bankName: String = "BCA"
) : PaymentMethod {
    override val name: String = "Transfer Bank ($bankName)"

    override fun processPayment(amount: Double): PaymentResult {
        return if (accountNumber.length >= 8) {
            PaymentResult.Success("BT-${System.currentTimeMillis()}")
        } else {
            PaymentResult.Failed("Nomor rekening tidak valid", 404)
        }
    }

    override fun getFee(amount: Double): Double {
        val fee = amount * 0.01
        return maxOf(fee, 5000.0)
    }
}
