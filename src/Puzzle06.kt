import java.io.File


fun unmangle(s: String) = s.toByteArray(Charsets.ISO_8859_1).toString(Charsets.UTF_8)

fun main(args: Array<String>) {
  val (words, puzzle) = File(args[0]).readText().split("\n\n")
  val decodedWords = words.lines().mapIndexed { i, line ->
    var decodedLine = line
    if ((i + 1) % 3 == 0) decodedLine = unmangle(decodedLine)
    if ((i + 1) % 5 == 0) decodedLine = unmangle(decodedLine)
    decodedLine
  }

  println(puzzle.lines().sumOf { line ->
    1 + decodedWords.indexOfFirst { line.trim().toRegex().matches(it) }
  })
}
