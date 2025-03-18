import java.io.File
import java.text.Collator
import java.util.*


class NameComparator<T>(private val comparator: Comparator<String>) : Comparator<Pair<String, T>> {
  override fun compare(o1: Pair<String, T>, o2: Pair<String, T>) =
    comparator.compare(normalize(o1.first), normalize(o2.first))

  private fun normalize(s: String) = s.substringBefore(',').replace("[^\\p{IsAlphabetic}]".toRegex(), "")
}

abstract class LocaleBasedComparator(locale: Locale) : Comparator<String> {
  private val collator = Collator.getInstance(locale)
  override fun compare(o1: String, o2: String) = collator.compare(normalize(o1), normalize(o2))
  abstract fun normalize(s: String): String
}

open class EnglishComparator : LocaleBasedComparator(Locale.ENGLISH) {
  override fun normalize(s: String) = s.lowercase().replace("ø", "o").replace("æ", "ae")
}

class SwedishComparator : LocaleBasedComparator(Locale.of("sv")) {
  override fun normalize(s: String) = s.lowercase().replace("ø", "ö").replace("æ", "ä")
}

class DutchComparator : EnglishComparator() {
  override fun normalize(s: String) = super.normalize(s.replace("^[^\\p{Lu}]*".toRegex(), ""))
}

fun main(args: Array<String>) {
  val phonebook = File(args[0]).readLines().map { line ->
    line.split(": ").let { Pair(it[0], it[1]) }
  }

  val en = phonebook.sortedWith(NameComparator(EnglishComparator()))
  val sv = phonebook.sortedWith(NameComparator(SwedishComparator()))
  val nl = phonebook.sortedWith(NameComparator(DutchComparator()))

  println(en[en.size / 2].second.toLong() * sv[sv.size / 2].second.toLong() * nl[nl.size / 2].second.toLong())
}
