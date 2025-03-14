import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*


fun zonedDateTime(zone: String, time: String): ZonedDateTime = ZonedDateTime.of(
  LocalDateTime.parse(
    time,
    DateTimeFormatter.ofPattern("MMM dd, yyyy, HH:mm", Locale.ENGLISH)
  ),
  ZoneId.of(zone)
)

fun main(args: Array<String>) {
  val r = """(Departure|Arrival):\s+(\S+)\s+(.*)""".toRegex()
  println(File(args[0]).readText().split("\n\n").sumOf {
    val (dep, arr) = it.split("\n")
    val (_, depZone, depTime) = r.matchEntire(dep)!!.destructured
    val (_, arrZone, arrTime) = r.matchEntire(arr)!!.destructured

    val departure = zonedDateTime(depZone, depTime)
    val arrival = zonedDateTime(arrZone, arrTime)
    ChronoUnit.MINUTES.between(departure, arrival)
  })
}
