import play.jobs._
import play.Play

@OnApplicationStart
class BootStrap extends Job {

  override def doJob() {

    import models._
    import play.test._

    // Import initial data if the database is empty
    if (Athlete.count().single() == 0) {
      Yaml[List[Any]]("initial-data.yml").foreach {
        _ match {
          case a: Athlete => Athlete.create(a)
          case w: Workout => Workout.create(w)
          case c: Comment => Comment.create(c)
        }
      }
    }
  }
}