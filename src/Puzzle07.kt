import java.io.File
import java.time.OffsetDateTime
import java.time.ZoneId


fun main(args: Array<String>) {
  val haliZone = ZoneId.of("America/Halifax")
  val santiZone = ZoneId.of("America/Santiago")
  val whitespace = """\s+""".toRegex()
  println(File(args[0]).readLines().mapIndexed { i, line ->
    val (timeStr, corr, wrong) = line.split(whitespace)
    val t = OffsetDateTime.parse(timeStr)

    val halifax = t.toZonedDateTime().withZoneSameInstant(haliZone)
    val zonedT = if (t.toLocalDateTime() == halifax.toLocalDateTime()) halifax
    else t.toZonedDateTime().withZoneSameInstant(santiZone)
    zonedT.minusMinutes(wrong.toLong()).plusMinutes(corr.toLong()).hour * (i + 1)
  }.sum())
}
