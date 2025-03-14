import java.io.File


fun main(args: Array<String>) {
  println(File(args[0]).readLines().map { line ->
    val sms = line.encodeToByteArray().size <= 160
    val tweet = line.length <= 140
    if (tweet && sms) 13 else if (sms) 11 else if (tweet) 7 else 0
  }.sum())
}
