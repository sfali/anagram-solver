package com.alphasystem.anagram

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Promise

class MainVerticle : AbstractVerticle() {

  override fun start(startPromise: Promise<Void>) {
    val httpVerticlePromise = Promise.promise<String>()
    vertx.deployVerticle(
      "com.alphasystem.anagram.http.HttpServerVerticle",
      DeploymentOptions(), httpVerticlePromise
    )
    httpVerticlePromise.future()
      .onComplete {
        if (it.succeeded()) {
          startPromise.complete()
        } else {
          startPromise.fail(it.cause())
        }
      }
  }
}
