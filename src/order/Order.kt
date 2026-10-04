package order

import product.Product
import java.time.LocalDateTime

/**
 * Representasi pesanan (Order / Invoice) yang telah berhasil diproses dari keranjang belanja.
 *
 * Menyimpan data pemesan, daftar produk yang dibeli beserta kuantitasnya, tanggal transaksi,
 * dan status progres pemenuhan pesanan ([OrderStatus]).
 *
 * @property id Nomor unik identifikasi pesanan (contoh: "ORD-001").
 * @property customerName Nama pelanggan yang melakukan pemesanan.
 * @property items Daftar produk dan kuantitas belanja yang diabadikan saat checkout.
 * @property status Status pesanan saat ini (default: [OrderStatus.Pending]).
 */
class Order(
    val id: String,
    val customerName: String,
    val items: Map<Product, Int>,
    var status: OrderStatus = OrderStatus.Pending
) {

    /**
     * Total harga akhir belanjaan seluruh item pesanan setelah diskon masing-masing produk.
     */
    val totalPrice: Double
        get() = items.entries.sumOf { (product, quantity) ->
            product.getDiscountedPrice() * quantity
        }

    /**
     * Total potongan diskon yang diperoleh pada pesanan ini.
     */
    val totalDiscount: Double
        get() = items.entries.sumOf { (product, quantity) ->
            product.calculateDiscount() * quantity
        }

    /**
     * Waktu dan tanggal saat transaksi pesanan dibuat.
     */
    val createdAt: String = LocalDateTime.now().toString()

    /**
     * Memperbarui status pesanan ke tahap berikutnya.
     * Status tidak dapat diubah apabila telah mencapai status final ([OrderStatus.isFinal]).
     *
     * @param newStatus Status baru yang akan diterapkan.
     * @return `true` jika status berhasil diperbarui, `false` jika pesanan sudah dalam status final.
     */
    fun updateStatus(newStatus: OrderStatus): Boolean {
        if (status.isFinal()) {
            println("Status sudah final, tidak bisa diubah.")
            return false
        }
        status = newStatus
        println("Status order $id diubah menjadi: ${status.display()}")
        return true
    }

    /**
     * Menampilkan rincian detail nota pesanan ke konsol terminal.
     */
    fun displayOrder() {
        println("=".repeat(55))
        println("DETAIL ORDER")
        println("=".repeat(55))
        println("ID Order    : $id")
        println("Pelanggan   : $customerName")
        println("Tanggal     : $createdAt")
        println("Status      : ${status.display()}")
        println("-".repeat(55))
        println("Items:")
        items.forEach { (product, quantity) ->
            println("   ${product.name} x$quantity = Rp ${formatRupiah(product.getDiscountedPrice() * quantity)}")
        }
        println("-".repeat(55))
        println("Total Diskon: Rp ${formatRupiah(totalDiscount)}")
        println("Total Harga : Rp ${formatRupiah(totalPrice)}")
        println("=".repeat(55))
    }

    /**
     * Memformat nilai desimal ke format nominal Rupiah dengan pemisah ribuan.
     *
     * @param nominal Angka nominal yang diformat.
     * @return String angka berformat Rupiah.
     */
    private fun formatRupiah(nominal: Double): String {
        val str = nominal.toLong().toString()
        val builder = StringBuilder()
        var count = 0
        for (i in str.length - 1 downTo 0) {
            builder.insert(0, str[i])
            count++
            if (count % 3 == 0 && i > 0) {
                builder.insert(0, ".")
            }
        }
        return builder.toString()
    }
}