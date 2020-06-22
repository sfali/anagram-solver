package com.alphasystem.anagram.util

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestAnagramSolver {

  @Test
  fun testValidAnagram() {
    val solver = AnagramSolver("cinema", "iceman")
    Assertions.assertEquals(true, solver.areAnagrams().areAnagrams)
  }

  @Test
  fun testInValidAnagram() {
    val solver = AnagramSolver("abca", "abcd")
    Assertions.assertEquals(false, solver.areAnagrams().areAnagrams)
  }

  @Test
  fun testValidAnagramWithRepetition() {
    val solver = AnagramSolver("moon", "mono")
    Assertions.assertEquals(true, solver.areAnagrams().areAnagrams)
  }

  @Test
  fun testValidAnagramWithSpace() {
    val solver = AnagramSolver("Tom Marvolo Riddle", "I am Lord Voldemort")
    Assertions.assertEquals(true, solver.areAnagrams().areAnagrams)
  }

  @Test
  fun testValidAnagramWithSpecialCharacters() {
    val solver = AnagramSolver("Church of Scientology", "rich-chosen goofy cult")
    Assertions.assertEquals(true, solver.areAnagrams().areAnagrams)
  }

  @Test
  fun testValidAnagramWithSpecialCharacters2() {
    val solver = AnagramSolver("McDonald's restaurants", "Uncle Sam's standard rot")
    Assertions.assertEquals(true, solver.areAnagrams().areAnagrams)
  }

}
