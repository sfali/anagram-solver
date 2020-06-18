package com.alphasystem.anagram.http

import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(VertxExtension::class)
class TestHttpServer {

  private lateinit var vertex: Vertx
  private lateinit var webClient: WebClient

  @BeforeAll
  fun prepare(context: VertxTestContext) {
    vertex = Vertx.vertx()
    vertex.deployVerticle(HttpServerVerticle(), context.completing())
    webClient = WebClient.create(
      vertex, WebClientOptions()
        .setDefaultHost("localhost")
        .setDefaultPort(8888)
    )
  }

  @AfterAll
  fun finish(context: VertxTestContext) {
    vertex.close(context.completing())
  }

  @Test
  fun test() {

  }
}
