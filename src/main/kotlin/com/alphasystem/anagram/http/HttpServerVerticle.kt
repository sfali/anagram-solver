package com.alphasystem.anagram.http

import io.vertx.rxjava.core.AbstractVerticle
import io.vertx.rxjava.ext.web.Router
import io.vertx.rxjava.ext.web.RoutingContext
import rx.Completable

class HttpServerVerticle : AbstractVerticle() {

  override fun rxStart(): Completable {
    val router = Router.router(vertx)
    router.get("/").handler { indexHandler(it) }
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
}
