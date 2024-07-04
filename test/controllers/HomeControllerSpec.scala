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

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Play.materializer
import play.api.http.Status.OK
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._

class HomeControllerSpec extends PlaySpec with BeforeAndAfter with BeforeAndAfterAll with GuiceOneAppPerSuite  with Injecting with LazyLogging {


  before {
  }

  override def beforeAll(): Unit = {
  }

  override def afterAll(): Unit = {
  }

  val controller: HomeController = inject[HomeController]

  "Two accesses of setData and getData " should {
    "returns an appropriate response" in {
      val fr = FakeRequest(POST, "/setUserData")
        .withHeaders("Content-type" -> "application/json")
        .withJsonBody(Json.parse("""{"user":"test-user", "key":"hoge", "value":"fuga"}"""))
      val result= call(controller.setUserData(), fr)
      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      assert(contentAsString(result) == """{"status":"Ok","message":""}""")

      val fr2 = FakeRequest(POST, "/getUserData")
        .withHeaders("Content-type" -> "application/json")
        .withJsonBody(Json.parse("""{"user":"test-user", "key":"hoge", "value":""}"""))
      val result2 = call(controller.getUserData(), fr2)
      status(result2) mustBe OK
      contentType(result2) mustBe Some("application/json")
      assert(contentAsString(result2) == """{"user":"test-user","key":"hoge","value":"fuga"}""")

    }
  }
}
