import java.security.MessageDigest
import java.sql.DriverManager
import java.util.*

fun String.md5(): String {
    val digested = MessageDigest.getInstance("MD5").digest(toByteArray())
    return digested.joinToString("") { String.format("%02x", it) }
}

fun String.base64enc(): String {
    val bytes = this.toByteArray()
    return Base64.getEncoder().encodeToString(bytes)
}

fun String.sha1(): String {
    val digested = MessageDigest.getInstance("SHA-1").digest(toByteArray())
    return digested.joinToString("") { String.format("%02x", it) }
}

fun String.sha256(): String {
    val digested = MessageDigest.getInstance("SHA-256").digest(toByteArray())
    return digested.joinToString("") { String.format("%02x", it) }
}

fun main(args: Array<String>) {
    println("Starting running...\n")
    val connection = DriverManager.getConnection("jdbc:sqlite:./accounts.db")
    val statement = connection.createStatement()
    statement.queryTimeout = 30  // set timeout to 30 sec.
    val rs = statement.executeQuery("select * from users")
    while (rs.next()) {
        val password = rs.getString("password")
        connection.createStatement().executeUpdate(
                "update users set md5='${password.md5()}' where ROWID=${rs.row}")
        connection.createStatement().executeUpdate(
                "update users set sha1='${password.sha1()}' where ROWID=${rs.row}")
        connection.createStatement().executeUpdate(
                "update users set sha256='${password.sha256()}' where ROWID=${rs.row}")
        connection.createStatement().executeUpdate(
                "update users set base64='${password.base64enc()}' where ROWID=${rs.row}")
    }
    println("Added hashes and base64 encoded passwords to database")
    rs.close()
}
