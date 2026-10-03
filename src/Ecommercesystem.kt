import order.Order
import order.OrderStatus
import product.FoodProduct
import product.Product

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
}