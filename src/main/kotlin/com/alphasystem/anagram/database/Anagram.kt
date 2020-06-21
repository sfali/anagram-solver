package com.alphasystem.anagram.database

import io.vertx.codegen.annotations.DataObject
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

@DataObject
data class Anagram(val id: String, val words: List<String>) {
  constructor(jsonObject: JsonObject) : this(
    jsonObject.getString("frequency"),
    jsonObject.getJsonArray("words").toList() as List<String>
  )

  fun toJson(): JsonObject = JsonObject().put("frequency", id).put("words", JsonArray(words))

}
