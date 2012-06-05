import org.specs2.mutable.Specification

import play.api.libs.ws.WS
import play.api.test._
import play.api.test.Helpers._

class ApiTests extends Specification {

  "Workout API" should {
    "return a list of workouts" in {
      running(TestServer(7777)) {
        await(WS.url("http://localhost:7777/api/workouts").get).status must equalTo(OK)
      }
    }

    "return workouts as json" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val result = controllers.api.WorkoutService.workouts()(FakeRequest())
        status(result) must equalTo(OK)
        contentType(result) must beSome("application/json")
        // TODO: Serialize to objects and verify there's 3
      }
    }
  }

}

