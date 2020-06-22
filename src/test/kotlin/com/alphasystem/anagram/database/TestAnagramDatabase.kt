package com.alphasystem.anagram.database

import com.alphasystem.anagram.database.reactivex.AnagramDatabaseService
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.pgclient.PgConnectOptions
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(VertxExtension::class)
class TestAnagramDatabase {

  private lateinit var vertex: Vertx
  private lateinit var service: AnagramDatabaseService

  @BeforeAll
  fun prepare(context: VertxTestContext) {
    vertex = Vertx.vertx()
    val pgConnectOptions =
      PgConnectOptions()
        .setHost("localhost")
        .setPort(5432)
        .setUser("postgres")
        .setPassword("postgres")
        .setDatabase("anagram")
    vertex.deployVerticle(
      AnagramDatabaseVerticle(pgConnectOptions),
      DeploymentOptions(),
      context
        .succeeding {
          service = AnagramDatabaseServiceFactory.createProxy(vertex, AnagramDatabaseVerticle.DATABASE_SERVICE_ADDRESS)
          context.completeNow()
        })
  }

  @AfterAll
  fun finish(context: VertxTestContext) {
    vertex.close(context.completing())
  }

/*  @Test
  fun testNonExistingRecord(context: VertxTestContext) {
    service.findAnagrams("x",
      context.succeeding {
        println(it)
        it?.let { context.failNow(RuntimeException("should not have found anything: $it")) } ?: context.completeNow()
      })
  }*/

  @Test
  fun testExistingRecord(context: VertxTestContext) {
    val id = "moon"
    service.findAnagrams(
      id,
      context.succeeding {
        it?.let {
          Assertions.assertEquals(
            Anagram(
              listOf("mono", "moon", "mon", "mn", "mo", "nm", "no", "on")
            ), it
          )
          context.completeNow()
        } ?: context.failNow(java.lang.RuntimeException("Should have returned record: $id"))
      })
  }
}
