import play._
import play.test._

import org.scalatest._
import org.scalatest.junit._
import org.scalatest.matchers._

class BasicTests extends UnitFlatSpec with ShouldMatchers with BeforeAndAfterEach {
    
  import models._
  import play.db.anorm._

  override def beforeEach() {
      Fixtures.deleteDatabase()
  }

  it should "create and retrieve a User" in {

      var user = User(NotAssigned, "jim@gmail.com", "secret", "Jim", "Smith")
      User.create(user)

      val jim = User.find(
          "email={email}").on("email" -> "jim@gmail.com"
      ).first()

      jim should not be (None)
      jim.get.firstName should be("Jim")

  }

  it should "connect a User" in {

      User.create(User(NotAssigned, "bob@gmail.com", "secret", "Bob", "Johnson"))

      User.connect("bob@gmail.com", "secret") should not be (None)
      User.connect("bob@gmail.com", "badpassword") should be(None)
      User.connect("tom@gmail.com", "secret") should be(None)

  }
}