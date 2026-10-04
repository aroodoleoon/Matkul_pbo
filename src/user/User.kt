package user

import cart.ShoppingCart
import order.Order


class User(
    val id: String,
    var name: String,
    var email: String,
    var address: String,
    private val password: String,
    // private val UUID: String = java.util.UUID.randomUUID().toString()
) {
    val cart: ShoppingCart = ShoppingCart()
    private val orderHistory: MutableList<Order> = mutableListOf()

    init {
        require(id.isNotBlank()) { "ID user tidak boleh kosong" }
        require(isValidEmail(email)) { "Format email tidak valid" }
        require(password.isNotBlank()) { "Password tidak boleh kosong" }
    }

    fun authenticate(inputPassword: String): Boolean = inputPassword == password

    private fun isValidEmail(value: String): Boolean {
        if (value.contains(" ")) return false
 
        val parts = value.split("@")
        if (parts.size != 2) return false // harus tepat satu @
 
        val (namaUser, domain) = parts
        return namaUser.isNotEmpty() &&
               domain.contains(".") &&
               !domain.startsWith(".") &&
               !domain.endsWith(".")
    }

    fun updateProfile(newName: String = name, newEmail: String = email, newAddress: String = address) {
        require(isValidEmail(newEmail)) { "Format email tidak valid" }
        name = newName
        email = newEmail
        address = newAddress
    }

    fun addOrder(order: Order) {
        orderHistory.add(order)
    }

    fun getOrderHistory(): List<Order> = orderHistory.toList()

    fun displayInfo() {
        println("=== Profil User ===")
        println("ID     : $id")
        println("Nama   : $name")
        println("Email  : $email")
        println("Alamat : $address")
        println("Jumlah pesanan: ${orderHistory.size}")
    }
    
    // fun login(inputEmail: String, inputPassword: String): Boolean {
    //     if (inputEmail.isBlank() || inputPassword.isBlank()) {
    //         return false
    //     }
    //     return authenticate(inputPassword)
    // }
    
    // fun register(newName: String, newEmail: String, newAddress: String, newPassword: String): Boolean {
    //     if (newName.isBlank() || newEmail.isBlank() || newAddress.isBlank() || newPassword.isBlank()) {
    //         return false
    //     }
    //     if (!isValidEmail(newEmail)) {
    //         return false
    //     }
    //     name = newName
    //     email = newEmail
    //     address = newAddress
    //     return true
    // }

    override fun toString(): String = "User(id=$id, name=$name, email=$email)"
}
