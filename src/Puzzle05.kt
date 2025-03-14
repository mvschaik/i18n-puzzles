import java.io.File
import kotlin.streams.toList


fun main(args: Array<String>) {
  val poo = 0x1F4A9
  val lines = File(args[0]).readLines().map { it.codePoints().toList() }
  println(lines.mapIndexed { row, line -> if (line[(row * 2) % line.size] == poo) 1 else 0 }.sum())
}
