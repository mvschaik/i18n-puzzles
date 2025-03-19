import java.io.File
import java.nio.charset.Charset


fun main(args: Array<String>) {
  val (wordsPart, puzzlePart) = File(args[0]).readText().split("\n\n")
  val encodedWords = wordsPart.lines().map { line ->
    line.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
  }

  val bom = """^[\uFEFF\uFFEF]""".toRegex()
  val isLatin = "\\p{IsLatin}+".toRegex()
  val encodings = listOf("UTF-16", "UTF-16LE", "UTF-16BE", "UTF-8", "ISO-8859-1")

  val words = encodedWords.map { word ->
    encodings.map { encoding -> word.toString(Charset.forName(encoding)).replace(bom, "") }
      .first { isLatin.matches(it) }
  }

  println(puzzlePart.trim().lines().map { it.trim().toRegex() }
            .sumOf { r -> words.indexOfFirst { r.matches(it) } + 1 })
}