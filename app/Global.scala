import models._
import play.api._
import play.api.Play.current

import anorm._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    if (!Play.isTest) {
      println("inserting seed data...")
      InitialData.insert()
    }
  }
}

/**
 * Initial set of data to be loaded
 */
object InitialData {

  def date(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)

  def insert() {

    if (Athlete.count() == 0) {

      Seq(
        Athlete(Id(1), "mraible@gmail.com", "beer", "Matt", "Raible"),
        Athlete(Id(2), "trishmcginity@gmail.com", "whiskey", "Trish", "McGinity")
      ).foreach(Athlete.create)

      Seq(
        Workout(Id(1), "Chainsaw Trail",
          """
            A beautiful fall ride: cool breezes, awesome views and yellow leaves.

            Would do it again in a heartbeat.
          """, 7, 90, date("2011-10-13"), 1),
        Workout(Id(2), "Monarch Lake Trail",
          "Awesome morning ride through falling yellow leaves and cool fall breezes.",
          4, 90, date("2011-10-15"), 1),
        Workout(Id(3), "Creekside to Flume to Chainsaw",
          "Awesome morning ride through falling yellow leaves and cool fall breezes.",
          12, 150, date("2011-10-16"), 2)
      ).foreach(Workout.create)

      Seq(
        Comment(1, "Jim", "Nice day for it!"),
        Comment(2, "Joe", "Love that trail."),
        Comment(2, "Jack", "Where there any kittens there?")
      ).foreach(Comment.create)
    }
  }
}