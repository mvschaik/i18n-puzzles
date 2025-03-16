import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


val VALID_FORMATS = listOf("yy-MM-dd", "yy-dd-MM", "dd-MM-yy", "MM-dd-yy").map(DateTimeFormatter::ofPattern)
val NINE_ELEVEN: LocalDate = LocalDate.of(2001, 9, 11)

fun validDate(dateStr: String, format: DateTimeFormatter): Boolean {
  return try {
    LocalDate.parse(dateStr, format).format(format) == dateStr
  } catch (e: DateTimeParseException) {
    false
  }
}

fun main(args: Array<String>) {
  val namesWithDates = File(args[0]).readLines().map { line ->
    val (dateStr, namesStr) = line.split(": ")
    val names = namesStr.split(", ").map(String::trim)
    names.associateWith { dateStr }
  }.flatMap { map -> map.entries }.groupBy({ it.key }, { it.value })

  println(namesWithDates.filterValues { dates ->
    val format = VALID_FORMATS.first { format -> dates.all { validDate(it, format) } }
    dates.any { LocalDate.parse(it, format) == NINE_ELEVEN }
  }.keys.sorted().joinToString(" "))
}
