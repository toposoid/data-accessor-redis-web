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

import com.ideal.linked.toposoid.common.{TRANSVERSAL_STATE, TransversalState}
import com.ideal.linked.toposoid.protocol.model.redis.KeyValueStoreInfo
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Play.materializer
import play.api.http.Status.OK
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._

import scala.io.Source

class HomeControllerSpec extends PlaySpec with GuiceOneAppPerSuite  with Injecting{

  val transversalState:String = Json.toJson(TransversalState(userId = "testuser", roleId = -1, username="guest", csrfToken="")).toString()

  "Access with unregistered key" should {
    "returns an appropriate response" in {
      val controller: HomeController = inject[HomeController]
      val fr2 = FakeRequest(POST, "/getData")
        .withHeaders("Content-type" -> "application/json", TRANSVERSAL_STATE.str -> transversalState)
        .withJsonBody(Json.parse("""{"identifier":"test-user", "key":"xyz", "value":""}"""))
      val result2 = call(controller.getData(), fr2)
      status(result2) mustBe OK
      contentType(result2) mustBe Some("application/json")
      assert(contentAsString(result2) == """{"identifier":"test-user","key":"xyz","value":""}""")

    }
  }

  "Two accesses of setData and getData and removeData " should {
    "returns an appropriate response" in {
      val controller: HomeController = inject[HomeController]
      val fr = FakeRequest(POST, "/setData")
        .withHeaders("Content-type" -> "application/json", TRANSVERSAL_STATE.str -> transversalState)
        .withJsonBody(Json.parse("""{"identifier":"test-user", "key":"hoge", "value":"fuga"}"""))
      val result= call(controller.setData(), fr)
      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      assert(contentAsString(result) == """{"status":"Ok","message":""}""")

      val fr2 = FakeRequest(POST, "/getData")
        .withHeaders("Content-type" -> "application/json", TRANSVERSAL_STATE.str -> transversalState)
        .withJsonBody(Json.parse("""{"identifier":"test-user", "key":"hoge", "value":""}"""))
      val result2 = call(controller.getData(), fr2)
      status(result2) mustBe OK
      contentType(result2) mustBe Some("application/json")
      assert(contentAsString(result2) == """{"identifier":"test-user","key":"hoge","value":"fuga"}""")

      val fr3 = FakeRequest(POST, "/removeData")
        .withHeaders("Content-type" -> "application/json", TRANSVERSAL_STATE.str -> transversalState)
        .withJsonBody(Json.parse("""{"identifier":"test-user", "key":"hoge", "value":""}"""))
      val result3 = call(controller.removeData(), fr3)
      status(result3) mustBe OK
      contentType(result3) mustBe Some("application/json")
      assert(contentAsString(result3) == """{"status":"Ok","message":""}""")

    }
  }


  /*
  "Two accesses of setData and getData and removeData2 " should {
    "returns an appropriate response" in {
      val value = Source.fromResource("resources/json2-2.txt").mkString
      val controller: HomeController = inject[HomeController]

      val fr = FakeRequest(POST, "/setData")
        .withHeaders("Content-type" -> "application/json", TRANSVERSAL_STATE.str -> transversalState)
        .withJsonBody(Json.toJson(KeyValueStoreInfo(identifier = "test-user", key="hoge", value=value)))
      val result = call(controller.setData(), fr)
      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      assert(contentAsString(result) == """{"status":"Ok","message":""}""")
    }
  }
   */

}
