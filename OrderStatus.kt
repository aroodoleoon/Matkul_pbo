package order

sealed class OrderStatus {

    object Pending : OrderStatus() {
        override fun display(): String = "Menunggu Pembayaran"
    }

    object Paid : OrderStatus() {
        override fun display(): String = "Dibayar"
    }

    object Shipped : OrderStatus() {
        override fun display(): String = "Dikirim"
    }

    object Delivered : OrderStatus() {
        override fun display(): String = "Diterima"
    }

    data class Cancelled(val reason: String) : OrderStatus() {
        override fun display(): String = "Dibatalkan: $reason"
    }

    abstract fun display(): String

    fun isFinal(): Boolean {
        return this is Delivered || this is Cancelled
    }
}