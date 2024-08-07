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

package controllers

import com.ideal.linked.toposoid.protocol.model.redis.UserInfo
import com.typesafe.scalalogging.LazyLogging
import io.lettuce.core.api.StatefulRedisConnection

import javax.inject._
import play.api._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents, val redisConnection: StatefulRedisConnection[String, String] /*RedisのコネクションをDI*/)(implicit ec: ExecutionContext) extends BaseController with LazyLogging {

  def setUserData() = Action(parse.json).async { request =>
    try {
      val json = request.body
      val userInfo:UserInfo = Json.parse(json.toString).as[UserInfo]
      val key:String = userInfo.user + "." + userInfo.key

      import scala.jdk.FutureConverters._
      val asyncCommands = redisConnection.async()
      for {
        _ <- asyncCommands.set(key, userInfo.value).asScala
      } yield Ok(Json.obj("status" ->"Ok", "message" -> ""))

    } catch {
      case e: Exception => {
        logger.error(e.toString, e)
        Future(BadRequest(Json.obj("status" -> "Error", "message" -> e.toString())))
      }
    }
  }

  def getUserData() = Action(parse.json) { request =>
    try {
      val json = request.body
      val userInfo:UserInfo = Json.parse(json.toString).as[UserInfo]
      val key:String = userInfo.user + "." + userInfo.key
      val asyncCommands = redisConnection.sync()
      val value = asyncCommands.get(key)
      Ok(Json.toJson(UserInfo(userInfo.user, userInfo.key, value))).as(JSON)

    } catch {
      case e: Exception => {
        logger.error(e.toString, e)
        BadRequest(Json.obj("status" -> "Error", "message" -> e.toString()))
      }
    }
  }


}
