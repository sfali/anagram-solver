package com.alphasystem.anagram.database

import com.alphasystem.anagram.getAllPossibleFrequencyStrings
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.reactivex.pgclient.PgPool
import org.slf4j.LoggerFactory
import kotlin.math.abs

class AnagramDatabaseServiceImpl(val pgPool: PgPool) : AnagramDatabaseService {

  private val logger = LoggerFactory.getLogger(AnagramDatabaseService::class.java)

  override fun findAnagrams(word: String, handler: Handler<AsyncResult<Anagram>>): AnagramDatabaseService {
    val ids = word.getAllPossibleFrequencyStrings()
    val query = FIND_ANAGRAM_QUERY.format(ids.joinToString(separator = ",") { "'$it'" })
    pgPool
      .preparedQuery(query)
      .rxExecute()
      .subscribe(
        {
          var anagrams =
            it.map { row ->
              row.getStringArray("words").toList()
            }.flatten()

          if (anagrams.isNotEmpty()) {
            anagrams =
              anagrams.sortedWith(Comparator { o1, o2 ->
                if (o1.length == o2.length) {
                  o1.compareTo(o2)
                } else {
                  abs(o1.length - word.length) - abs((o2.length - word.length))
                }
              })
            handler.handle(Future.succeededFuture(Anagram(anagrams)))
          } else {
            handler.handle(Future.succeededFuture(null))
          }
        }, // onSuccess
        {
          logger.error("unable to query database", it)
          handler.handle(Future.failedFuture(it))
        } // onError
      )
    return this
  }

  companion object {
    private const val FIND_ANAGRAM_QUERY = "SELECT * FROM anagram.anagram WHERE frequency IN (%s)"
  }
}
