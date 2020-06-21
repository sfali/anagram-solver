package com.alphasystem.anagram.database

import io.vertx.codegen.annotations.DataObject
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

@DataObject
data class Anagram(val anagrams: List<String>) {
  constructor(jsonObject: JsonObject) : this(
    jsonObject.getJsonArray("words").toList() as List<String>
  )

  fun toJson(): JsonObject = JsonObject().put("words", JsonArray(anagrams))
}
