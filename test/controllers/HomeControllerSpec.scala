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

import com.ideal.linked.toposoid.common.{TRANSVERSAL_STATE, TransversalState}
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Play.materializer
import play.api.http.Status.OK
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._

class HomeControllerSpec extends PlaySpec with GuiceOneAppPerSuite  with Injecting{

  val transversalState:String = Json.toJson(TransversalState(username="guest")).toString()

  "Two accesses of setData and getData and removeData " should {
    "returns an appropriate response" in {
      val controller: HomeController = inject[HomeController]
      val fr = FakeRequest(POST, "/setUserData")
        .withHeaders("Content-type" -> "application/json", TRANSVERSAL_STATE.str -> transversalState)
        .withJsonBody(Json.parse("""{"user":"test-user", "key":"hoge", "value":"fuga"}"""))
      val result= call(controller.setUserData(), fr)
      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      assert(contentAsString(result) == """{"status":"Ok","message":""}""")

      val fr2 = FakeRequest(POST, "/getUserData")
        .withHeaders("Content-type" -> "application/json", TRANSVERSAL_STATE.str -> transversalState)
        .withJsonBody(Json.parse("""{"user":"test-user", "key":"hoge", "value":""}"""))
      val result2 = call(controller.getUserData(), fr2)
      status(result2) mustBe OK
      contentType(result2) mustBe Some("application/json")
      assert(contentAsString(result2) == """{"user":"test-user","key":"hoge","value":"fuga"}""")

      val fr3 = FakeRequest(POST, "/removeUserData")
        .withHeaders("Content-type" -> "application/json", TRANSVERSAL_STATE.str -> transversalState)
        .withJsonBody(Json.parse("""{"user":"test-user", "key":"hoge", "value":""}"""))
      val result3 = call(controller.removeUserData(), fr3)
      status(result3) mustBe OK
      contentType(result3) mustBe Some("application/json")
      assert(contentAsString(result3) == """{"status":"Ok","message":""}""")

    }
  }
}
