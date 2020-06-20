package com.alphasystem.anagram

const val BASE_VALUE = 'a'.toInt()

const val SpecialCharactersRegex = "[._-]*"

fun String.toFrequencyArray(): IntArray {
  val frequencyArray = IntArray(26)
  this
    .replace(SpecialCharactersRegex, "")
    .replace(" ", "")
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
