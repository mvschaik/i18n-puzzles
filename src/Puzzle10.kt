import at.favre.lib.crypto.bcrypt.BCrypt
import java.io.File
import java.text.Normalizer


fun variants(password: String): List<String> {
  val normalized = Normalizer.normalize(password, Normalizer.Form.NFC)
  val charGroups = normalized.map { c ->
    val denormalized = Normalizer.normalize("$c", Normalizer.Form.NFD)
    if ("$c" == denormalized) listOf(denormalized) else listOf(denormalized, "$c")
  }
  return charGroups.reduce { acc, chars -> acc.flatMap { partial -> chars.map { partial + it } } }
}

fun main(args: Array<String>) {
  val (authDbStr, attemptsStr) = File(args[0]).readText().split("\n\n")
  val authDb = authDbStr.lines().associate { line ->
    val (username, hash) = line.split(" ")
    username to hash.toByteArray()
  }
  val attempts = attemptsStr.lines().map { it.split(" ") }

  val passwords = mutableMapOf<String, String?>()
  println(attempts.count { (name, password) ->
    val normalizedPassword = Normalizer.normalize(password, Normalizer.Form.NFC)
    val validPassword = passwords.getOrPut(name) {
      if (variants(password).any { BCrypt.verifyer().verify(it.toCharArray(), authDb[name]).verified }) {
        normalizedPassword
      } else {
        null
      }
    }
    validPassword == normalizedPassword
  })
}
