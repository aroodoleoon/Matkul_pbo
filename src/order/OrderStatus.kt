package order

/**
 * Kelas tersegel (sealed class) yang merepresentasikan siklus status dari sebuah pesanan (Order).
 *
 * Menerapkan sealed class agar setiap status pesanan terdefinisi dalam hierarki yang tertutup,
 * memungkinkan pengecekan kondisi yang aman dan tuntas pada ekspresi `when`.
 */
sealed class OrderStatus {

    /**
     * Status pesanan saat baru dibuat dan sedang menunggu konfirmasi pembayaran.
     */
    object Pending : OrderStatus() {
        /** Mengembalikan deskripsi teks status. */
        override fun display(): String = "Menunggu Pembayaran"
    }

    /**
     * Status pesanan setelah pembayaran berhasil diverifikasi.
     */
    object Paid : OrderStatus() {
        /** Mengembalikan deskripsi teks status. */
        override fun display(): String = "Dibayar"
    }

    /**
     * Status pesanan ketika barang telah dikirim oleh pihak penjual/ekspedisi.
     */
    object Shipped : OrderStatus() {
        /** Mengembalikan deskripsi teks status. */
        override fun display(): String = "Dikirim"
    }

    /**
     * Status pesanan ketika barang telah diterima dengan baik oleh pelanggan.
     */
    object Delivered : OrderStatus() {
        /** Mengembalikan deskripsi teks status. */
        override fun display(): String = "Diterima"
    }

    /**
     * Status pesanan yang dibatalkan beserta alasannya.
     *
     * @property reason Keterangan alasan pembatalan pesanan.
     */
    data class Cancelled(val reason: String) : OrderStatus() {
        /** Mengembalikan deskripsi teks status beserta alasan pembatalan. */
        override fun display(): String = "Dibatalkan: $reason"
    }

    /**
     * Mengembalikan teks representasi nama status pesanan yang ramah pengguna.
     *
     * @return String nama status pesanan.
     */
    abstract fun display(): String

    /**
     * Memeriksa apakah status pesanan sudah mencapai tahap akhir (terminal state)
     * sehingga tidak dapat diubah lagi.
     *
     * @return `true` jika status adalah [Delivered] atau [Cancelled].
     */
    fun isFinal(): Boolean {
        return this is Delivered || this is Cancelled
    }
}