/*
 * Copyright (C) 2025  Linked Ideal LLC.[https://linked-ideal.com/]
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import javax.inject.{ Inject, Provider, Singleton }
import play.api.Configuration
import play.api.inject.ApplicationLifecycle

import scala.jdk.FutureConverters._

@Singleton
class RedisClientProvider @Inject()(
                                     config: Configuration,
                                     lifecycle: ApplicationLifecycle
                                   ) extends Provider[RedisClient] {
  private val redisClient =
    RedisClient.create(config.get[String]("lettuce.redis-uri"))

  lifecycle.addStopHook { () =>
    redisClient.shutdownAsync().asScala
  }
  override val get: RedisClient = redisClient
}


@Singleton
class StatefulRedisConnectionProvider @Inject()(
                                                 redisClient: RedisClient,
                                                 lifecycle: ApplicationLifecycle
                                               ) extends Provider[StatefulRedisConnection[String, String]] {
  private val connection = redisClient.connect()

  lifecycle.addStopHook { () =>
    connection.closeAsync().asScala
  }

  override val get: StatefulRedisConnection[String, String] = connection
}