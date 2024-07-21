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

import com.ideal.linked.toposoid.common.{TRANSVERSAL_STATE, ToposoidUtils, TransversalState}
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
    val transversalState = Json.parse(request.headers.get(TRANSVERSAL_STATE .str).get).as[TransversalState]
    try {
      val json = request.body
      val userInfo:UserInfo = Json.parse(json.toString).as[UserInfo]
      val key:String = userInfo.user + "." + userInfo.key
      logger.info(ToposoidUtils.formatMessageForLogger("key:" + key + " value:" + userInfo.value, transversalState.username))
      import scala.jdk.FutureConverters._
      val asyncCommands = redisConnection.async()
      for {
        _ <- asyncCommands.set(key, userInfo.value).asScala
      } yield {
        logger.info(ToposoidUtils.formatMessageForLogger("Data registration to redis completed.", transversalState.username))
        Ok(Json.obj("status" ->"Ok", "message" -> ""))
      }

    } catch {
      case e: Exception => {
        logger.error(ToposoidUtils.formatMessageForLogger(e.toString, transversalState.username), e)
        Future(BadRequest(Json.obj("status" -> "Error", "message" -> e.toString())))
      }
    }
  }

  def getUserData() = Action(parse.json) { request =>
    val transversalState = Json.parse(request.headers.get(TRANSVERSAL_STATE .str).get).as[TransversalState]
    try {
      val json = request.body
      val userInfo:UserInfo = Json.parse(json.toString).as[UserInfo]
      val key:String = userInfo.user + "." + userInfo.key
      val asyncCommands = redisConnection.sync()
      val value = asyncCommands.get(key)
      logger.info(ToposoidUtils.formatMessageForLogger("Getting data from redis completed.[key:" + key + " value:" + value + "]", transversalState.username))
      Ok(Json.toJson(UserInfo(userInfo.user, userInfo.key, value))).as(JSON)
    } catch {
      case e: Exception => {
        logger.error(ToposoidUtils.formatMessageForLogger(e.toString, transversalState.username), e)
        BadRequest(Json.obj("status" -> "Error", "message" -> e.toString()))
      }
    }
  }

  def removeUserData() = Action(parse.json) { request =>
    val transversalState = Json.parse(request.headers.get(TRANSVERSAL_STATE.str).get).as[TransversalState]
    try {
      val json = request.body
      val userInfo: UserInfo = Json.parse(json.toString).as[UserInfo]
      val key: String = userInfo.user + "." + userInfo.key
      val asyncCommands = redisConnection.sync()
      asyncCommands.del(key)
      logger.info(ToposoidUtils.formatMessageForLogger("Removing data from redis completed.[key:" + key + "]", transversalState.username))
      Ok(Json.obj("status" ->"Ok", "message" -> ""))
    } catch {
      case e: Exception => {
        logger.error(ToposoidUtils.formatMessageForLogger(e.toString, transversalState.username), e)
        BadRequest(Json.obj("status" -> "Error", "message" -> e.toString()))
      }
    }
  }

}
