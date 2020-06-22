package com.alphasystem.anagram

import com.google.common.collect.Sets
import java.util.stream.Collectors

const val BASE_VALUE = 'a'.toInt()

val SpecialCharactersRegex = "[.' _-]*".toRegex()

fun String.toFrequencyArray(): IntArray {
  val frequencyArray = IntArray(26)
  this
    .replace(SpecialCharactersRegex, "")
    .toLowerCase()
    .chars()
    .forEach {
      val index = it - BASE_VALUE
      // for each index increment the value
      frequencyArray[index] = frequencyArray[index] + 1
    }
  return frequencyArray
}

fun String.toFrequencyString(): String = toFrequencyArray().joinToString(separator = "")

fun String.getAllPossibleFrequencyStrings(): List<String> {
  val set = this.toCharArray().toSet()
  var result = listOf(this.toFrequencyString())
  for (len in 2 until this.length) {
    val s = Sets.combinations(set, len).map { it.joinToString("").toFrequencyString() }
    result = result.plus(s)
  }
  return result.distinct()
}
