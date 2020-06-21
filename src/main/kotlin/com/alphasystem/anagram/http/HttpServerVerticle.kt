package com.alphasystem.anagram.http

import com.alphasystem.anagram.database.AnagramDatabaseServiceFactory
import com.alphasystem.anagram.database.AnagramDatabaseVerticle
import com.alphasystem.anagram.database.reactivex.AnagramDatabaseService
import com.alphasystem.anagram.toFrequencyString
import com.alphasystem.anagram.util.AnagramSolver
import io.reactivex.Completable
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext
import org.slf4j.LoggerFactory

class HttpServerVerticle : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(HttpServerVerticle::class.java)

  private lateinit var dbService: AnagramDatabaseService

  override fun rxStart(): Completable {
    dbService =
      AnagramDatabaseServiceFactory
        .createProxy(
          vertx.delegate,
          AnagramDatabaseVerticle.DATABASE_SERVICE_ADDRESS
        )

    val router = Router.router(vertx)
    router.get("/").handler { indexHandler(it) }
    router.get("/anagrams/:string1").handler { findAnagramsHandler(it) }
    router.get("/anagrams/:string1/:string2").handler { isAnagramHandler(it) }
    return vertx
      .createHttpServer()
      .requestHandler(router)
      .rxListen(8080, "0.0.0.0")
      .ignoreElement()
  }

  private fun indexHandler(context: RoutingContext) {
    context
      .response()
      .putHeader("content-type", "text/plain")
      .end("Hello from Vert.x!")
  }

  private fun isAnagramHandler(context: RoutingContext) {
    val source = context.request().getParam("string1")
    val target = context.request().getParam("string2")
    logger.info("isAnagram: source={}, target={}", source, target)
    val result = AnagramSolver(source, target).areAnagrams()
    context
      .response()
      .putHeader("Content-Type", "application/json")
      .end(Json.encodePrettily(result))
  }

  private fun findAnagramsHandler(context: RoutingContext) {
    val source = context.request().getParam("string1")
    if (!source.matches(FIND_ANAGRAMS_VALIDATION_REGEX)) {
      context
        .response()
        .setStatusCode(400)
        .setStatusMessage("BadRequest")
        .end(
          JsonObject()
            .put("code", "BadRequest")
            .put("message", "Invalid input: $source")
            .encodePrettily()
        )
      return
    }
    logger.info("Find anagram, source={}", source)
    dbService
      .rxFindAnagrams(source.toFrequencyString())
      .subscribe(
        {
          if (it == null) {
            context
              .response()
              .setStatusCode(404)
              .setStatusMessage("Not Found")
              .end(
                JsonObject()
                  .put("code", "NotFound")
                  .put("message", "No anagram found with the given word: $source")
                  .encodePrettily()
              )
          } else {
            context
              .response()
              .setStatusCode(200)
              .setStatusMessage("OK")
              .putHeader("Content-Type", "application/json")
              .end(Json.encodePrettily(it))
          }
        },
        {
          logger.error("Unable to find anagram: source=$source", it)
          context
            .response()
            .setStatusCode(500)
            .setStatusMessage("InternalServerError")
            .end(
              JsonObject()
                .put("code", "InternalServerError")
                .put(
                  "message",
                  "The server encountered an unexpected condition which prevented it from fulfilling the request."
                )
                .encodePrettily()
            )
        }
      )
  }

  companion object {
    const val DEPLOYMENT_NAME = "com.alphasystem.anagram.http.HttpServerVerticle"
    val FIND_ANAGRAMS_VALIDATION_REGEX = "[A-Za-z]*".toRegex()
  }
}
