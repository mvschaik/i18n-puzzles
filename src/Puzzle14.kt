import java.io.File
import java.math.BigInteger


const val numbers = "0一二三四五六七八九"

val modifiers = mapOf(
  '十' to 10L,
  '百' to 100L,
  '千' to 1000L,
)

val myriads = mapOf(
  '万' to 10_000L, '億' to 10_000 * 10_000L
)

val units = mapOf(
  '尺' to Rational(10, 33),
  '間' to Rational(6 * 10, 33),
  '丈' to Rational(10 * 10, 33),
  '町' to Rational(360 * 10, 33),
  '里' to Rational(12960 * 10, 33),
  '寸' to Rational(10, 33 * 10),
  '分' to Rational(10, 33 * 100),
  '厘' to Rational(10, 33 * 1000),
  '毛' to Rational(10, 33 * 10_000),
)

class Rational(private val n: BigInteger, private val d: BigInteger) {
  constructor(n: Int, d: Int) : this(n.toBigInteger(), d.toBigInteger())
  constructor(n: Long, d: Long) : this(n.toBigInteger(), d.toBigInteger())

  operator fun times(r: Rational): Rational = Rational(n.times(r.n), r.d.times(d))
  fun toLong() = (n / d).toLong()
}

fun parseNum(s: String): Long {
  var n = 0L
  var currentGroup = 0L
  var currentMyriad = 0L
  for (c in s) {
    when (c) {
      in numbers -> {
        currentMyriad += currentGroup
        currentGroup = numbers.indexOf(c).toLong()
      }

      in modifiers -> {
        if (currentGroup == 0L) currentGroup = 1
        currentMyriad += currentGroup * modifiers[c]!!
        currentGroup = 0
      }

      in myriads -> {
        currentMyriad += currentGroup
        currentGroup = 0
        if (currentMyriad == 0L) currentMyriad = 1
        n += currentMyriad * myriads[c]!!
        currentMyriad = 0
      }
    }
  }
  return n + currentMyriad + currentGroup
}

fun parseToM2(s: String) = Rational(
  parseNum(s.substring(0..s.length - 2)), 1
) * units[s[s.length - 1]]!!

fun main(args: Array<String>) {
  assert(parseNum("四") == 4L)
  assert(parseNum("十一") == 11L)
  assert(parseNum("十二") == 12L)
  assert(parseNum("二十") == 20L)
  assert(parseNum("四十二") == 42L)
  assert(parseNum("四十二") == 42L)
  assert(parseNum("千") == 1000L)
  assert(parseNum("十万") == 100_000L)
  assert(parseNum("十万") == 100_000L)
  assert(parseNum("百万") == 1_000_000L)
  assert(parseNum("千万") == 10_000_000L)
  assert(parseNum("三百") == 300L)
  assert(parseNum("三百二十一") == 321L)
  assert(parseNum("四千") == 4_000L)
  assert(parseNum("五万") == 50_000L)
  assert(parseNum("五万") == 50_000L)
  assert(parseNum("九万九千九百九十九") == 99_999L)
  assert(parseNum("四十二万四十二") == 420_042L)
  assert(parseNum("九億八千七百六十五万四千三百二十一") == 987_654_321L)
  assert(parseNum("七十八") == 78L)
  assert(parseNum("二十一万七千八百") == 217800L)
  assert(parseNum("七万二千三百五十八") == 72358L)
  assert(parseNum("六百十二") == 612L)

  println(File(args[0]).readLines().sumOf { line ->
    val (n1, n2) = line.split(" × ")
    (parseToM2(n1) * parseToM2(n2)).toLong()
  })
}
