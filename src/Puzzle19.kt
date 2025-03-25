import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import org.joda.time.tz.ZoneInfoCompiler
import org.joda.time.tz.ZoneInfoProvider
import java.io.File


/*
To fetch the TZ files:

for v in 2018c 2018g 2021b 2023d; do
  mkdir -p input/tzdata$v
  curl -o - https://data.iana.org/time-zones/releases/tzdata$v.tar.gz | tar zxvf - -C input/tzdata$v
done
*/

fun main(args: Array<String>) {

  val versions = listOf("2018c", "2018g", "2021b", "2023d")

  val zic = ZoneInfoCompiler()
  val files = listOf("africa", "antarctica", "asia", "australasia", "europe", "northamerica", "southamerica")
  val timezones = versions.associateWith { version ->
    val file = File("tz/$version")
    if (file.exists()) {
      val zip = ZoneInfoProvider(file)
      zip.availableIDs.associateWith { zip.getZone(it) }
    } else {
      zic.compile(file, files.map { File("input/tzdata$version", it) }.toTypedArray())
    }
  }

  val fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
  val timesWithPlaces = File(args[0]).readLines().flatMap { line ->
    val (time, zoneId) = line.split("; ")
    versions.map { version ->
      val tz = timezones[version]!![zoneId]
      fmt.parseDateTime(time).withZoneRetainFields(tz).withZone(DateTimeZone.UTC) to zoneId
    }
  }
  val numPlacesPerTime = timesWithPlaces.groupBy { it.first }.mapValues { it.value.map { it.second }.toSet().size }
  println(
    numPlacesPerTime.maxBy { it.value }.key.toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ"))
  )
}
