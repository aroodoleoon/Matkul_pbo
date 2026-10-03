import order.Order
import order.OrderStatus
import product.FoodProduct
import product.Product


// ECommerceSystem - pusat pengelolaan toko: katalog produk, pelanggan, order, dan checkout.

class ECommerceSystem(val storeName: String = "Toko Kelompok") {

    private val products = mutableMapOf<String, Product>() // key = product id
    private val customers = mutableSetOf<String>()         // username pelanggan terdaftar
    private val orders = mutableListOf<Order>()
    private var orderCounter = 0

    // ---------------------- PRODUK ----------------------

    fun addProduct(product: Product): Boolean {
        if (products.containsKey(product.id)) {
            println("❌ Produk dengan ID ${product.id} sudah ada.")
            return false
        }
        products[product.id] = product
        return true
    }

    fun findProduct(id: String): Product? = products[id]

    fun getAllProducts(): List<Product> = products.values.toList()

    fun searchProducts(keyword: String): List<Product> =
        products.values.filter { it.name.contains(keyword, ignoreCase = true) }

    // Filter berdasarkan tipe, contoh: getProductsByType<FoodProduct>()
    inline fun <reified T : Product> getProductsByType(): List<T> =
        getAllProducts().filterIsInstance<T>()

    fun displayAllProducts() {
        println("===== Katalog $storeName =====")
        if (products.isEmpty()) {
            println("(kosong)")
            return
        }
        // Polymorphism: tiap produk menampilkan info sesuai tipenya
        products.values.forEach { it.displayInfo() }
    }

    // ---------------------- PELANGGAN ----------------------

    fun registerCustomer(username: String): Boolean {
        if (!customers.add(username)) {
            println("❌ Pelanggan $username sudah terdaftar.")
            return false
        }
        return true
    }

    // ---------------------- CHECKOUT ----------------------

    /**
     * Proses checkout. Return Order jika dibuat, null jika gagal.
     *  - Pembayaran Success -> order berstatus Paid
     *  - Pembayaran Pending -> order berstatus Pending (menunggu pembayaran)
     *  - Pembayaran Failed  -> tidak ada order, keranjang tetap utuh
     */
    fun checkout(customerName: String, cart: ShoppingCart, payment: PaymentMethod): Order? {
        if (customerName !in customers) {
            println("❌ Checkout gagal: pelanggan belum terdaftar.")
            return null
        }
        if (cart.isEmpty()) {
            println("❌ Checkout gagal: keranjang kosong.")
            return null
        }

        // 1. Validasi isi keranjang (stok tidak dicek lagi, sudah dikurangi saat addItem)
        for (product in cart.getItems().keys) {
            if (!products.containsKey(product.id)) {
                println("❌ Checkout gagal: ${product.name} tidak ada di katalog.")
                return null
            }
            if (product is FoodProduct && product.isExpired()) {
                println("❌ Checkout gagal: ${product.name} sudah kadaluarsa, hapus dari keranjang.")
                return null
            }
        }

        // 2. Hitung total + biaya admin, lalu bayar
        val subtotal = cart.getTotalPrice()
        val fee = payment.getFee(subtotal)
        val totalPayable = subtotal + fee
        println("Metode: ${payment.name} | Subtotal: Rp ${subtotal.toLong()} | Biaya admin: Rp ${fee.toLong()} | Bayar: Rp ${totalPayable.toLong()}")

        return when (val result = payment.processPayment(totalPayable)) {
            is PaymentResult.Success -> {
                println("✅ Pembayaran berhasil (Transaksi: ${result.transactionId})")
                buildOrder(customerName, cart, OrderStatus.Paid)
            }
            PaymentResult.Pending -> {
                println("⏳ Pembayaran menunggu konfirmasi, order dibuat berstatus Pending.")
                buildOrder(customerName, cart, OrderStatus.Pending)
            }
            is PaymentResult.Failed -> {
                println("❌ Pembayaran gagal: ${result.reason} (kode ${result.errorCode})")
                null
            }
        }
    }

    // Membuat order dari isi keranjang, lalu mengosongkan keranjang
    private fun buildOrder(customerName: String, cart: ShoppingCart, status: OrderStatus): Order {
        orderCounter++
        val orderId = "ORD-%03d".format(orderCounter)
        val order = Order(orderId, customerName, cart.getItems(), status)
        orders.add(order)
        cart.clear() // stok tidak dikembalikan, barang resmi dibeli
        println("🧾 Order $orderId berhasil dibuat.")
        return order
    }

    // ---------------------- ORDER ----------------------

    fun getAllOrders(): List<Order> = orders.toList()

    fun getOrdersByCustomer(customerName: String): List<Order> =
        orders.filter { it.customerName == customerName }

    fun findOrder(orderId: String): Order? = orders.find { it.id == orderId }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Boolean {
        val order = findOrder(orderId) ?: run {
            println("❌ Order $orderId tidak ditemukan.")
            return false
        }
        return order.updateStatus(newStatus)
    }

    /** Batalkan order (hanya jika masih Pending/Paid) dan kembalikan stok produk. */
    fun cancelOrder(orderId: String, reason: String): Boolean {
        val order = findOrder(orderId) ?: run {
            println("❌ Order $orderId tidak ditemukan.")
            return false
        }
        if (order.status != OrderStatus.Pending && order.status != OrderStatus.Paid) {
            println("❌ Order tidak bisa dibatalkan pada status: ${order.status.display()}")
            return false
        }
        if (!order.updateStatus(OrderStatus.Cancelled(reason))) return false
        // reduceStock dengan nilai negatif = menambah stok (cara yang sama dipakai ShoppingCart)
        order.items.forEach { (product, qty) -> product.reduceStock(-qty) }
        return true
    }

    // ---------------------- LAPORAN PENJUALAN ----------------------

    // Order dihitung sebagai penjualan jika sudah dibayar (Paid, Shipped, Delivered).
    // Pending (belum bayar) dan Cancelled tidak masuk pendapatan.
    private fun countsAsSale(order: Order): Boolean =
        order.status == OrderStatus.Paid ||
                order.status == OrderStatus.Shipped ||
                order.status == OrderStatus.Delivered

    /** Total pendapatan dari order yang sudah dibayar. */
    fun getTotalRevenue(): Double =
        orders.filter { countsAsSale(it) }.sumOf { it.totalPrice }

    /** Jumlah seluruh order (semua status). */
    fun getOrderCount(): Int = orders.size

    /** Menampilkan laporan penjualan lengkap. */
    fun displaySalesReport() {
        val sales = orders.filter { countsAsSale(it) }
        val pendingCount = orders.count { it.status == OrderStatus.Pending }
        val cancelledCount = orders.count { it.status is OrderStatus.Cancelled }

        println("=".repeat(55))
        println("LAPORAN PENJUALAN - $storeName")
        println("=".repeat(55))
        println("Total Order        : ${getOrderCount()}")
        println("  - Terjual        : ${sales.size}")
        println("  - Menunggu bayar : $pendingCount")
        println("  - Dibatalkan     : $cancelledCount")
        println("-".repeat(55))
        println("Total Pendapatan   : Rp ${formatRupiah(getTotalRevenue())}")
        println("Total Diskon       : Rp ${formatRupiah(sales.sumOf { it.totalDiscount })}")
        if (sales.isNotEmpty()) {
            println("Rata-rata per Order: Rp ${formatRupiah(getTotalRevenue() / sales.size)}")
        }

        // Rincian per kategori produk
        val entries = sales.flatMap { it.items.entries }
        val unitsPerCategory = entries.groupBy({ it.key.getCategory() }, { it.value })
        val revenuePerCategory = entries.groupBy({ it.key.getCategory() }, { it.key.getDiscountedPrice() * it.value })
        println("-".repeat(55))
        println("Per Kategori:")
        if (unitsPerCategory.isEmpty()) {
            println("   (belum ada penjualan)")
        } else {
            unitsPerCategory.forEach { (category, units) ->
                val revenue = revenuePerCategory[category]?.sum() ?: 0.0
                println("   $category: ${units.sum()} item, Rp ${formatRupiah(revenue)}")
            }
        }
        println("=".repeat(55))
    }

    // Format angka ke Rupiah dengan titik ribuan, contoh 15000000 -> 15.000.000
    private fun formatRupiah(nominal: Double): String {
        val str = nominal.toLong().toString()
        val builder = StringBuilder()
        var count = 0
        for (i in str.length - 1 downTo 0) {
            builder.insert(0, str[i])
            count++
            if (count % 3 == 0 && i > 0) builder.insert(0, ".")
        }
        return builder.toString()
    }
}