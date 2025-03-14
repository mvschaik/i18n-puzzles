import java.io.File
import java.text.Normalizer


fun main(args: Array<String>) {
  println(File(args[0]).readLines().count { passwd ->
    val normalized = Normalizer.normalize(passwd.lowercase(), Normalizer.Form.NFKD)
      .replace("[^a-z]".toRegex(), "")
    // a length of at least 4 and at most 12
    passwd.length in 4..12 &&
        // at least one digit
        passwd.any(Char::isDigit) &&
        // at least one accented or unaccented vowel1 (a, e, i, o, u) (examples: i, Á or ë).
        normalized.any { it in "aeiou" } &&
        // at least one accented or unaccented consonant, examples: s, ñ or ŷ
        normalized.any { it in "bcdfghjklmnpqrstvwxyz" } &&
        // no recurring letters in any form
        normalized.groupingBy { it }.eachCount().values.all { it == 1 }
  })
}
