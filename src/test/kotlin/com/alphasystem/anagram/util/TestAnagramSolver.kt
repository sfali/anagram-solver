package com.alphasystem.anagram.util

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestAnagramSolver {

  @Test
  fun testValidAnagram() {
    val solver = AnagramSolver("cinema", "iceman")
    Assertions.assertEquals(true, solver.isAnagram())
  }

  @Test
  fun testInValidAnagram() {
    val solver = AnagramSolver("abca", "abcd")
    Assertions.assertEquals(false, solver.isAnagram())
  }

  @Test
  fun testValidAnagramWithRepetition() {
    val solver = AnagramSolver("moon", "mono")
    Assertions.assertEquals(true, solver.isAnagram())
  }

}
