package com.alphasystem.anagram.http

import com.alphasystem.anagram.database.Anagram
import com.alphasystem.anagram.database.AnagramDatabaseServiceFactory
import com.alphasystem.anagram.database.AnagramDatabaseVerticle
import com.alphasystem.anagram.util.AnagramsResult
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.ext.web.codec.BodyCodec
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.pgclient.PgConnectOptions
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(VertxExtension::class)
class ApiTest {

  private lateinit var vertex: Vertx
  private lateinit var webClient: WebClient

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
          AnagramDatabaseServiceFactory.createProxy(vertex, AnagramDatabaseVerticle.DATABASE_SERVICE_ADDRESS)
          context.completeNow()
        })

    vertex.deployVerticle(HttpServerVerticle(), context.completing())
    webClient = WebClient.create(
      vertex, WebClientOptions()
        .setDefaultHost("localhost")
        .setDefaultPort(8080)
    )
  }

  @AfterAll
  fun finish(context: VertxTestContext) {
    vertex.close(context.completing())
  }

  @Test
  fun testIsAnagrams(context: VertxTestContext) {
    webClient
      .get("/anagrams/cinema/iceman")
      .`as`(BodyCodec.string())
      .send(context.succeeding {
        Assertions.assertEquals(Json.encodePrettily(AnagramsResult(true)), it.body())
        context.completeNow()
      })
  }

  @Test
  fun testIsInvalidAnagrams(context: VertxTestContext) {
    webClient
      .get("/anagrams/abca/abcd")
      .`as`(BodyCodec.string())
      .send(context.succeeding {
        Assertions.assertEquals(Json.encodePrettily(AnagramsResult(false)), it.body())
        context.completeNow()
      })
  }

  @Test
  fun testGetAnagrams(context: VertxTestContext) {
    webClient
      .get("/anagrams/moon")
      .`as`(BodyCodec.string())
      .send(context.succeeding {
        Assertions.assertEquals(Json.encodePrettily(Anagram(listOf("mono", "moon", "mon", "mn", "mo", "nm", "no", "on"))), it.body())
        context.completeNow()
      })
  }

}
