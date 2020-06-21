package com.alphasystem.anagram

import com.alphasystem.anagram.database.AnagramDatabaseVerticle
import com.alphasystem.anagram.http.HttpServerVerticle
import io.reactivex.Completable
import io.vertx.pgclient.PgConnectOptions
import io.vertx.reactivex.core.AbstractVerticle

class MainVerticle : AbstractVerticle() {

  override fun rxStart(): Completable {
    return vertx
      .rxDeployVerticle(AnagramDatabaseVerticle(PgConnectOptions.fromEnv()))
      .flatMap {
        vertx.rxDeployVerticle(HttpServerVerticle.DEPLOYMENT_NAME)
      }.ignoreElement()
  }
}
