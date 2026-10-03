package payment

class BankTransferPayment(
    val accountNumber: String,
    val bankName: String
) : PaymentMethod {
    override val name: String = "Transfer Bank ($bankName)"

    override fun processPayment(amount: Double): PaymentResult {
        return if (accountNumber.isNotEmpty()) {
            PaymentResult.Pending
        } else {
            PaymentResult.Failed("Nomor rekening tidak valid", 404)
        }
    }

    override fun getFee(amount: Double): Double {
        return 2500.0 // Flat fee 2500
    }
}
