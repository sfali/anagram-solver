package com.alphasystem.anagram.http

import com.alphasystem.anagram.database.AnagramDatabaseServiceFactory
import com.alphasystem.anagram.database.AnagramDatabaseVerticle
import com.alphasystem.anagram.database.reactivex.AnagramDatabaseService
import com.alphasystem.anagram.toFrequencyString
import com.alphasystem.anagram.util.AnagramSolver
import io.reactivex.Completable
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.Json
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.core.http.HttpServer
import io.vertx.reactivex.ext.web.RoutingContext
import io.vertx.reactivex.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import org.slf4j.LoggerFactory

class HttpServerVerticle : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(HttpServerVerticle::class.java)

  private lateinit var dbService: AnagramDatabaseService
  private lateinit var httpServer: HttpServer

  override fun rxStart(): Completable {
    dbService =
      AnagramDatabaseServiceFactory
        .createProxy(
          vertx.delegate,
          AnagramDatabaseVerticle.DATABASE_SERVICE_ADDRESS
        )

    httpServer = vertx.createHttpServer(HttpServerOptions().setPort(8080).setHost("0.0.0.0"))

    return OpenAPI3RouterFactory
      .rxCreate(vertx, "anagram.yaml")
      .flatMap {
        it
          .addHandlerByOperationId("areAnagrams") { context -> isAnagramHandler(context) }
          .addHandlerByOperationId("findAnagrams") { context -> findAnagramsHandler(context) }
        httpServer
          .requestHandler(it.router)
          .rxListen()
      }
      .ignoreElement()
  }

  override fun stop() {
    httpServer.close()
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
              .end()
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
            .setStatusCode(503)
            .setStatusMessage("Internal Error")
            .end()
        }
      )
  }

  companion object {
    const val DEPLOYMENT_NAME = "com.alphasystem.anagram.http.HttpServerVerticle"
  }
}
