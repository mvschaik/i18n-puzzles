import java.io.File


fun main(args: Array<String>) {
  println(File(args[0]).readLines().count {
    // a length of at least 4 and at most 12
    it.length in 4..12 &&
        // at least one digit
        it.any(Char::isDigit) &&
        // at least one uppercase letter (with or without accents, examples: A or Ż)
        it.any(Char::isUpperCase) &&
        // at least one lowercase letter (with or without accents, examples: a or ŷ)
        it.any(Char::isLowerCase) &&
        // at least one character that is outside the standard 7-bit ASCII character set (examples: Ű, ù or ř)
        it.any { c -> c.code > 0x7f }
  })
}
