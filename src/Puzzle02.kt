import java.io.File
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


fun main(args: Array<String>) {
  val zeroZone = ZoneId.of("UTC")
  println(
    File(args[0]).readLines().map { line ->
      OffsetDateTime.parse(line).atZoneSameInstant(zeroZone)
    }.groupingBy { it }.eachCount().filterValues { it == 4 }.keys.first()
      .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxxxx"))
  )
}
