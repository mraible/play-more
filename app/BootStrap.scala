import play.jobs._
import play.Play

@OnApplicationStart
class BootStrap extends Job {

  override def doJob() {

    import models._
    import play.test._

    if (Play.configuration.get("db") != null) {
      // Import initial data if the database is empty
      if (User.count().single() == 0) {
        Yaml[List[Any]]("initial-data.yml").foreach {
          _ match {
            case u: User => User.create(u)
            case w: Workout => Workout.create(w)
            case c: Comment => Comment.create(c)
          }
        }
      }
    }
  }
}