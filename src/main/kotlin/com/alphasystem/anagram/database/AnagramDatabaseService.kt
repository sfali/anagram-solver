package com.alphasystem.anagram.database

import io.vertx.codegen.annotations.Fluent
import io.vertx.codegen.annotations.ProxyGen
import io.vertx.codegen.annotations.VertxGen
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.rxjava.pgclient.PgPool

@VertxGen
@ProxyGen
interface AnagramDatabaseService {
  @Fluent
  fun findAnagrams(id: String, handler: Handler<AsyncResult<Anagram?>>): AnagramDatabaseService

}

object AnagramDatabaseServiceFactory {
  fun create(pgPool: PgPool): AnagramDatabaseService = AnagramDatabaseServiceImpl(pgPool)

  fun createProxy(vertx: Vertx, address: String): AnagramDatabaseServiceVertxEBProxy =
    AnagramDatabaseServiceVertxEBProxy(vertx, address)
}
