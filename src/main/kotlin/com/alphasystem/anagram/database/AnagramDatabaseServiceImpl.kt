package com.alphasystem.anagram.database

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.rxjava.pgclient.PgPool
import io.vertx.rxjava.sqlclient.Tuple
import org.slf4j.LoggerFactory

class AnagramDatabaseServiceImpl(val pgPool: PgPool) : AnagramDatabaseService {

  private val logger = LoggerFactory.getLogger(AnagramDatabaseService::class.java)

  override fun findAnagrams(id: String, handler: Handler<AsyncResult<Anagram?>>): AnagramDatabaseService {
    pgPool
      .preparedQuery(FIND_ANAGRAM_QUERY)
      .rxExecute(Tuple.of(id))
      .subscribe(
        {
          val maybeRow = it.firstOrNull()
          maybeRow?.let { row ->
            val anagram = Anagram(row.getStringArray("words").toList())
            handler.handle(Future.succeededFuture(anagram))
          } ?: handler.handle(Future.succeededFuture(null))
        }, // onSuccess
        {
          logger.error("unable to query database", it)
          handler.handle(Future.failedFuture(it))
        } // onError
      )
    return this
  }

  companion object {
    private const val FIND_ANAGRAM_QUERY = "SELECT * FROM anagram.anagram WHERE frequency=$1"
  }
}
