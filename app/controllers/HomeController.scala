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

package controllers

import com.ideal.linked.toposoid.common.{TRANSVERSAL_STATE, ToposoidUtils, TransversalState}
import com.ideal.linked.toposoid.protocol.model.redis.KeyValueStoreInfo
import com.typesafe.scalalogging.LazyLogging
import io.lettuce.core.api.StatefulRedisConnection

import javax.inject._
import play.api._
import play.api.libs.json.{Json, JsValue}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents, val redisConnection: StatefulRedisConnection[String, String] /*RedisのコネクションをDI*/)(implicit ec: ExecutionContext) extends BaseController with LazyLogging {

  //def setUserData() = Action(parse.json).async { request =>
  def setData():Action[JsValue] = Action(parse.json[JsValue]){ request =>
    val transversalState = Json.parse(request.headers.get(TRANSVERSAL_STATE .str).get).as[TransversalState]
    try {
      val json = request.body
      val keyValueStoreInfo:KeyValueStoreInfo = Json.parse(json.toString).as[KeyValueStoreInfo]
      val key:String = keyValueStoreInfo.identifier + "." + keyValueStoreInfo.key
      logger.info(ToposoidUtils.formatMessageForLogger("key:" + key + " value:" + keyValueStoreInfo.value, transversalState.username))

      val asyncCommands = redisConnection.sync()
      asyncCommands.set(key, keyValueStoreInfo.value)

      logger.info(ToposoidUtils.formatMessageForLogger("Data registration to redis completed.", transversalState.username))
      Ok(Json.obj("status" -> "Ok", "message" -> ""))

      /*
      import scala.jdk.FutureConverters._
      val asyncCommands = redisConnection.async()
      for {
        _ <- asyncCommands.set(key, keyValueStoreInfo.value).asScala
      } yield {
        logger.info(ToposoidUtils.formatMessageForLogger("Data registration to redis completed.", transversalState.username))
        Ok(Json.obj("status" ->"Ok", "message" -> ""))
      }
      */
    } catch {
      case e: Exception => {
        logger.error(ToposoidUtils.formatMessageForLogger(e.toString, transversalState.username), e)
        //Future(BadRequest(Json.obj("status" -> "Error", "message" -> e.toString())))
        BadRequest(Json.obj("status" -> "Error", "message" -> e.toString()))
      }
    }
  }

  def getData():Action[JsValue] = Action(parse.json[JsValue]) { request =>
    val transversalState = Json.parse(request.headers.get(TRANSVERSAL_STATE .str).get).as[TransversalState]
    try {
      val json = request.body
      val keyValueStoreInfo:KeyValueStoreInfo = Json.parse(json.toString).as[KeyValueStoreInfo]
      val key:String = keyValueStoreInfo.identifier + "." + keyValueStoreInfo.key
      val asyncCommands = redisConnection.sync()
      val value =  Option(asyncCommands.get(key)) match {
        case Some(x) => x
        case None => ""
      }
      logger.info(ToposoidUtils.formatMessageForLogger("Getting data from redis completed.[key:" + key + " value:" + value + "]", transversalState.username))
      Ok(Json.toJson(KeyValueStoreInfo(keyValueStoreInfo.identifier, keyValueStoreInfo.key, value))).as(JSON)
    } catch {
      case e: Exception => {
        logger.error(ToposoidUtils.formatMessageForLogger(e.toString, transversalState.username), e)
        BadRequest(Json.obj("status" -> "Error", "message" -> e.toString()))
      }
    }
  }

  def removeData():Action[JsValue] = Action(parse.json[JsValue]) { request =>
    val transversalState = Json.parse(request.headers.get(TRANSVERSAL_STATE.str).get).as[TransversalState]
    try {
      val json = request.body
      val keyValueStoreInfo: KeyValueStoreInfo = Json.parse(json.toString).as[KeyValueStoreInfo]
      val key: String = keyValueStoreInfo.identifier + "." + keyValueStoreInfo.key
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
