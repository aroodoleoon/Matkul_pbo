package order

import product.Product
import java.time.LocalDateTime

class Order(
    val id: String,
    val customerName: String,
    val items: Map<Product, Int>,
    var status: OrderStatus = OrderStatus.Pending
) {

    val totalPrice: Double
        get() = items.entries.sumOf { (product, quantity) ->
            product.getDiscountedPrice() * quantity
        }

    val totalDiscount: Double
        get() = items.entries.sumOf { (product, quantity) ->
            product.calculateDiscount() * quantity
        }

    val createdAt: String = LocalDateTime.now().toString()

    fun updateStatus(newStatus: OrderStatus): Boolean {
        if (status.isFinal()) {
            println("Status sudah final, tidak bisa diubah.")
            return false
        }
        status = newStatus
        println("Status order $id diubah menjadi: ${status.display()}")
        return true
    }

    fun displayOrder() {
        println("=" .repeat(55))
        println("DETAIL ORDER")
        println("=" .repeat(55))
        println("ID Order    : $id")
        println("Pelanggan   : $customerName")
        println("Tanggal     : $createdAt")
        println("Status      : ${status.display()}")
        println("-" .repeat(55))
        println("Items:")
        items.forEach { (product, quantity) ->
            println("   ${product.name} x$quantity = Rp ${formatRupiah(product.getDiscountedPrice() * quantity)}")
        }
        println("-" .repeat(55))
        println("Total Diskon: Rp ${formatRupiah(totalDiscount)}")
        println("Total Harga : Rp ${formatRupiah(totalPrice)}")
        println("=" .repeat(55))
    }

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