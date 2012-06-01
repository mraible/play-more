import org.specs2.mutable._

import play.api.db.DB
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import play.api.test._
import play.api.test.Helpers._

class EvolutionsTest extends Specification {

  "Evolutions" should {
    "be applied without errors" in {
      evolutionFor("default")
      running(FakeApplication()) {
        DB.withConnection {
          implicit connection =>
            SQL("select count(1) from athlete").execute()
            SQL("select count(1) from workout").execute()
            SQL("select count(1) from comment").execute()
        }
      }
      success
    }
  }
}