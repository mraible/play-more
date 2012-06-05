import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

class WebTests extends Specification {

  "Application" should {
    "run in a browser" in {
      running(TestServer(3333), HTMLUNIT) {
        browser =>
          browser.goTo("http://localhost:3333")
          browser.title() must equalTo("Hello Matt!")
      }
    }
  }

}
