import product.ElectronicProduct
import product.FoodProduct
import product.ClothingProduct
import system.ECommerceSystem
import user.User
import payment.QRISPayment

fun main() {
    val system = ECommerceSystem("Toko Kelompok PBO")

    val p1 = ElectronicProduct("E01", "Laptop HP", 15000000.0, 10, "HP", 24, true)
    val p2 = ClothingProduct("C01", "Kemeja Flanel", 200000.0, 50, "L", "Merah", "Katun", 10.0)
    val p3 = FoodProduct("F01", "Roti Tawar", 20000.0, 30, 2, 500, true)

    system.addProduct(p1)
    system.addProduct(p2)
    system.addProduct(p3)

    system.displayAllProducts()

    val user1 = User("U01", "Budi", "budi@email.com", "Jl. Mawar")
    system.registerUser(user1)

    println("\n--- Menambah ke Keranjang ---")
    user1.cart.addItem(p1, 1)
    user1.cart.addItem(p2, 2)
    user1.cart.addItem(p3, 1)

    user1.cart.displayCart()

    println("\n--- Proses Checkout ---")
    val qris = QRISPayment()
    val order = system.checkout(user1, user1.cart, qris)

    if (order != null) {
        order.displayOrder()
    }
}
