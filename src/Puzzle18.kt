import java.io.File
import kotlin.math.abs


var RLI = '\u2067'
var LRI = '\u2066'
var PDI = '\u2069'

fun evalTerm(s: String) = when (s[0]) {
  '(' -> {
    val (value, rest) = evaluate(s.substring(1))
    Pair(value, rest.substring(1))  // ')'
  }

  else -> {
    val match = "^\\d+".toRegex().find(s)
    Pair(Rational(match!!.value.toInt(), 1), s.substring(match.range.last + 1))
  }
}

fun evaluate(s: String): Pair<Rational, String> {
  val (t1, rest) = evalTerm(s)
  val op = rest[1]
  val (t2, rest2) = evalTerm(rest.substring(3))

  return when (op) {
    '+' -> Pair(t1 + t2, rest2)
    '-' -> Pair(t1 - t2, rest2)
    '*' -> Pair(t1 * t2, rest2)
    '/' -> Pair(t1 / t2, rest2)
    else -> throw RuntimeException("Unknown op: $op")
  }
}

fun reverse(s: String) = s.reversed().map {
  when (it) {
    ')' -> '('
    '(' -> ')'
    else -> it
  }
}.joinToString("")

fun stripBiDiChars(s: String) = s.replace("[$RLI$LRI$PDI]".toRegex(), "")

fun unBiDi(s: String): String {
  var level = 0
  var inNumber = false
  val isRtl = { level % 2 == 1 }
  val levels = s.map { c ->
    if (inNumber && c !in '0'..'9') {
      level--; inNumber = false
    }
    when (c) {
      RLI -> if (!isRtl()) level++
      LRI -> if (isRtl()) level++
      PDI -> level--
      in '0'..'9' -> if (isRtl()) {
        level++; inNumber = true
      }
    }
    level
  }
  return stripBiDiChars(reduce(s, levels))
}

fun reduce(s: String, levels: List<Int>): String {
  val maxLevel = levels.max()
  if (maxLevel == 0) return s
  val startIndex = levels.indexOf(maxLevel)
  val endIndex = startIndex + levels.subList(startIndex, levels.size).indexOfFirst { it < maxLevel }
  return reduce(
    s.substring(0, startIndex) + reverse(s.substring(startIndex, endIndex)) + s.substring(endIndex),
    levels.subList(0, startIndex) + levels.subList(startIndex, endIndex).map { it - 1 } + levels.subList(
      endIndex, levels.size
    ))
}

fun main(args: Array<String>) {
  println(File(args[0]).readLines().sumOf {
    val lynx = evaluate(unBiDi(it)).first.toLong()
    val rex = evaluate(stripBiDiChars(it)).first.toLong()
    abs(lynx - rex)
  })
}
