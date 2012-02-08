import play.test.FunctionalTest
import play.test.FunctionalTest._
import play.mvc._
import play.mvc.Http._
import org.junit._

import models._
import com.codahale.jerkson.Json._

class ApiTests extends FunctionalTest {

    @Test
    def testGetWorkouts() {
        var response = GET("/api/workouts");
        assertStatus(200, response);
        assertContentType("application/json", response)
        println(response.out)
    }

}

