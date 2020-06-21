package com.alphasystem.anagram.database

import io.vertx.pgclient.PgConnectOptions
import io.vertx.rxjava.core.AbstractVerticle
import io.vertx.rxjava.pgclient.PgPool
import io.vertx.serviceproxy.ServiceBinder
import io.vertx.sqlclient.PoolOptions

class AnagramDatabaseVerticle(private val pgConnectOptions: PgConnectOptions) : AbstractVerticle() {

  override fun start() {
    ServiceBinder(vertx.delegate)
      .setAddress(DATABASE_SERVICE_ADDRESS)
      .register(
        AnagramDatabaseService::class.java,
        AnagramDatabaseServiceFactory.create(PgPool.pool(vertx, pgConnectOptions, PoolOptions()))
      )
  }

  companion object {
    const val DATABASE_SERVICE_ADDRESS = "database-service"
  }
}
