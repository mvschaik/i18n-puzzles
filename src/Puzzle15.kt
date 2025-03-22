import java.io.File
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.*


data class Location(val name: String, val tz: ZoneId, val holidays: List<LocalDate>) {
  companion object {
    private val dateFormat = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH)
    fun fromInputLine(s: String): Location {
      val (name, tzString, holidays) = s.split('\t')
      return Location(name, ZoneId.of(tzString), holidays.split(";").map { LocalDate.from(dateFormat.parse(it)) })
    }
  }
}

class Schedule(private val times: List<Pair<Instant, Boolean>>) {
  constructor(start: Instant, end: Instant) : this(listOf(start to true, end to false))
  constructor() : this(listOf())

  operator fun plus(other: Schedule): Schedule {
    val newTimes = mutableListOf<Pair<Instant, Boolean>>()
    var numActive = 0
    for ((t, enabled) in (times + other.times).sortedBy { it.first }) {
      if (enabled && numActive == 0) {
        newTimes += t to true
      }
      numActive += if (enabled) 1 else -1
      if (!enabled && numActive == 0) {
        newTimes += t to false
      }
    }
    return Schedule(newTimes)
  }

  operator fun minus(other: Schedule): Schedule = this + Schedule(other.times.map { it.first to !it.second })

  fun ranges() = times.chunked(2).map { (t0, t1) -> t0.first..t1.first }
}

fun main(args: Array<String>) {
  val (officeStr, customerStr) = File(args[0]).readText().split("\n\n")
  val offices = officeStr.lines().map(Location::fromInputLine)
  val customers = customerStr.lines().map(Location::fromInputLine)

  val searchRange = ZonedDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"))..ZonedDateTime.of(
    2023, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")
  )

  var s = Schedule()
  offices.forEach { office ->
    var t = (searchRange.start - Duration.of(7, ChronoUnit.DAYS)).withZoneSameInstant(office.tz)
    var officeSchedule = Schedule()
    repeat(54) {
      for (day in listOf(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
      )) {
        t = t.with(TemporalAdjusters.next(day)).withHour(8).withMinute(30)
        officeSchedule += Schedule(t.toInstant(), t.withHour(17).withMinute(0).toInstant())
      }
    }
    for (holiday in office.holidays) {
      officeSchedule -= Schedule(
        holiday.atStartOfDay(office.tz).toInstant(), holiday.atStartOfDay(office.tz).plusDays(1).toInstant()
      )
    }
    s += officeSchedule
  }
  s -= Schedule(Instant.MIN, searchRange.start.toInstant())
  s -= Schedule(searchRange.endInclusive.toInstant(), Instant.MAX)

  val overtimes = customers.map { customer ->
    var customerSchedule = Schedule(searchRange.start.toInstant(), searchRange.endInclusive.toInstant())

    var t = searchRange.start.withZoneSameInstant(customer.tz).with(TemporalAdjusters.previous(DayOfWeek.SATURDAY))
    repeat(54) {
      t = t.with(TemporalAdjusters.next(DayOfWeek.SATURDAY)).withHour(0).withMinute(0)
      customerSchedule -= Schedule(
        t.toInstant(), t.with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(0).withMinute(0).toInstant()
      )
    }

    for (holiday in customer.holidays) {
      customerSchedule -= Schedule(
        holiday.atStartOfDay(customer.tz).toInstant(), holiday.atStartOfDay(customer.tz).plusDays(1).toInstant()
      )
    }

    (customerSchedule - s).ranges().sumOf { r -> r.start.until(r.endInclusive, ChronoUnit.MINUTES) }
  }

  println(overtimes.max() - overtimes.min())
}
