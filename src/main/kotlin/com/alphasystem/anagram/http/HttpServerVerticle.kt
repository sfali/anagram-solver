package com.alphasystem.anagram.http

import com.alphasystem.anagram.util.AnagramSolver
import io.vertx.rxjava.core.AbstractVerticle
import io.vertx.rxjava.ext.web.Router
import io.vertx.rxjava.ext.web.RoutingContext
import org.slf4j.LoggerFactory
import rx.Completable

class HttpServerVerticle : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(HttpServerVerticle::class.java)

  override fun rxStart(): Completable {
    val router = Router.router(vertx)
    router.get("/").handler { indexHandler(it) }
    router.get("/anagrams/:string1/:string2").handler { isAnagramHandler(it) }
    return vertx
      .createHttpServer()
      .requestHandler(router)
      .rxListen(8080, "0.0.0.0")
      .toCompletable()
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
    val anagram = AnagramSolver(source, target).isAnagram()
    context
      .response()
      .putHeader("Content-Type", "application/json")
      .end("""{"areAnagrams": $anagram}""")
  }
}
