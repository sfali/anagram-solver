package com.alphasystem.anagram.util

class AnagramSolver(private val source: String, private val target: String) {

  private val frequencyArray = IntArray(26)

  fun isAnagram(): Boolean {
    // TODO: validate input strings
    populateFrequencyArray()
    return validateAnagram()
  }

  private fun populateFrequencyArray() =
    source
      .toLowerCase()
      .chars()
      .forEach {
        val index = it - BASE_VALUE
        // for each index increment the value
        frequencyArray[index] = frequencyArray[index] + 1
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

  companion object {
    /*
     * Integer value for 'a'
     */
    private const val BASE_VALUE = 'a'.toInt()
  }
}
