package user

import cart.ShoppingCart
import order.Order

/**
 * Representasi entitas pengguna/pelanggan dalam sistem e-commerce.
 *
 * Menerapkan prinsip enkapsulasi informasi kata sandi ([password] bersifat private),
 * validasi integritas format email pada blok `init`, serta kepemilikan keranjang belanja
 * ([cart]) dan riwayat pesanan ([orderHistory]).
 *
 * @property id Identifikasi unik pengguna (contoh: "USR-001").
 * @property name Nama lengkap pengguna.
 * @property email Alamat email terdaftar milik pengguna (divalidasi dengan format standar).
 * @property address Alamat domisili atau pengiriman pesanan pengguna.
 * @property password Kata sandi akun pengguna (dienkapsulasi secara private untuk keamanan).
 */
class User(
    val id: String,
    var name: String,
    var email: String,
    var address: String,
    private val password: String
) {
    /** Keranjang belanja pribadi yang terkait dengan akun pengguna ini. */
    val cart: ShoppingCart = ShoppingCart(name)

    /** Daftar riwayat pesanan yang pernah dibuat oleh pengguna ini. */
    private val orderHistory: MutableList<Order> = mutableListOf()

    init {
        require(id.isNotBlank()) { "ID user tidak boleh kosong" }
        require(isValidEmail(email)) { "Format email tidak valid" }
        require(password.isNotBlank()) { "Password tidak boleh kosong" }
    }

    /**
     * Memverifikasi apakah kata sandi yang diinputkan sesuai dengan kata sandi akun pengguna.
     *
     * @param inputPassword Kata sandi yang dicocokkan.
     * @return `true` jika cocok, sebaliknya `false`.
     */
    fun authenticate(inputPassword: String): Boolean = inputPassword == password

    /**
     * Memvalidasi format alamat email.
     * Email valid harus tidak mengandung spasi, memiliki tepat satu karakter '@',
     * serta bagian domain memiliki pemisah titik (dot) yang tidak berada di ujung kata.
     *
     * @param value Alamat email yang akan divalidasi.
     * @return `true` jika format email valid, sebaliknya `false`.
     */
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

    /**
     * Memperbarui informasi profil pengguna.
     *
     * @param newName Nama baru pengguna (opsional).
     * @param newEmail Alamat email baru pengguna (opsional, divalidasi formatnya).
     * @param newAddress Alamat domisili baru pengguna (opsional).
     */
    fun updateProfile(newName: String = name, newEmail: String = email, newAddress: String = address) {
        require(isValidEmail(newEmail)) { "Format email tidak valid" }
        name = newName
        email = newEmail
        address = newAddress
    }

    /**
     * Menambahkan pesanan baru ke dalam daftar riwayat pesanan pengguna.
     *
     * @param order Objek pesanan yang telah diselesaikan.
     */
    fun addOrder(order: Order) {
        orderHistory.add(order)
    }

    /**
     * Mengambil salinan tidak dapat dimodifikasi (read-only) dari riwayat pesanan pengguna.
     *
     * @return List objek [Order].
     */
    fun getOrderHistory(): List<Order> = orderHistory.toList()

    /**
     * Menampilkan informasi profil akun dan jumlah pesanan pengguna ke konsol terminal.
     */
    fun displayInfo() {
        println("=== Profil User ===")
        println("ID     : $id")
        println("Nama   : $name")
        println("Email  : $email")
        println("Alamat : $address")
        println("Jumlah pesanan: ${orderHistory.size}")
    }

    /**
     * Mengembalikan representasi string ringkas dari objek [User] tanpa membocorkan kata sandi.
     *
     * @return String representasi akun user.
     */
    override fun toString(): String = "User(id=$id, name=$name, email=$email)"
}
