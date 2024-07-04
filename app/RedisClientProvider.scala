/*
 * Copyright 2021 Linked Ideal LLC.[https://linked-ideal.com/]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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