package com.alphasystem.anagram.util

import com.alphasystem.anagram.BASE_VALUE
import com.alphasystem.anagram.toFrequencyArray

class AnagramSolver(source: String, private val target: String) {

  private val frequencyArray = source.toFrequencyArray()

  fun isAnagram(): Boolean {
    // TODO: validate input strings
    return validateAnagram()
  }

  private fun validateAnagram(): Boolean {
    var result = true

    target
      .toLowerCase()
      .chars()
      .forEach {
        val index = it - BASE_VALUE
        // for each index decrement the value
        frequencyArray[index] = frequencyArray[index] - 1
        if (frequencyArray[index] < 0) {
          // if current value go below 0, we don't have anagram
          result = false
          return@forEach
        }
      }

    if (result) {
      // sanity check, count of each element must be 0
      frequencyArray
        .forEach {
          // each value should be 0, any other value should be an error
          if (it < 0 || it >= 1) {
            result = false
            return@forEach
          }
        }
    }

    return result
  }
}
