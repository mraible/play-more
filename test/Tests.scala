import java.util.Date
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

  it should "create and retrieve a Athlete" in {

      var user = Athlete(NotAssigned, "jim@gmail.com", "secret", "Jim", "Smith")
      Athlete.create(user)

      val jim = Athlete.find(
          "email={email}").on("email" -> "jim@gmail.com"
      ).first()

      jim should not be (None)
      jim.get.firstName should be("Jim")

  }

  it should "connect a Athlete" in {

      Athlete.create(Athlete(NotAssigned, "bob@gmail.com", "secret", "Bob", "Johnson"))

      Athlete.connect("bob@gmail.com", "secret") should not be (None)
      Athlete.connect("bob@gmail.com", "badpassword") should be(None)
      Athlete.connect("tom@gmail.com", "secret") should be(None)
  }

  it should "create a Workout" in {

    Athlete.create(Athlete(Id(1), "bob@gmail.com", "secret", "Bob", "Johnson"))
    Workout.create(Workout(NotAssigned, "My first run", "With a hangover", 4, 38, new Date, 1))

    Workout.count().single() should be (1)

    val workouts = Workout.find("athleteId={id}").on("id" -> 1).as(Workout*)

    workouts.length should be (1)

    val firstWorkout = workouts.headOption

    firstWorkout should not be (None)
    firstWorkout.get.athlete_id should be (1)
    firstWorkout.get.title should be ("My first run")
    firstWorkout.get.description should be ("With a hangover")
  }

  it should "retrieve Workouts by athlete" in {

    Athlete.create(Athlete(Id(1), "bob@gmail.com", "secret", "Bob", "Johnson"))
    Workout.create(Workout(NotAssigned, "My 1st workout", "Yee Haw!", 3.20, 70, new Date, 1))

    val workouts = Workout.allWithAthlete

    workouts.length should be (1)

    val (workout,user) = workouts.head

    workout.title should be ("My 1st workout")
    user.firstName should be ("Bob")
  }

  it should "support Comments" in {

    Athlete.create(Athlete(Id(1), "bob@gmail.com", "secret", "Bob", "Johnson"))
    Workout.create(Workout(Id(1), "My first workout", "Yee Haw!", 2, 10, new Date, 1))
    Comment.create(Comment(NotAssigned, "Jim", "Nice workout!", new Date, 1))
    Comment.create(Comment(NotAssigned, "Bryan", "Great weather today!", new Date, 1))

    Athlete.count().single() should be (1)
    Workout.count().single() should be (1)
    Comment.count().single() should be (2)

    val Some( (post,user,comments) ) = Workout.byIdWithAthleteAndComments(1)

    post.title should be ("My first workout")
    user.firstName should be ("Bob")
    comments.length should be (2)
    comments(0).author should be ("Jim")
    comments(1).author should be ("Bryan")
  }

  it should "load a complex graph from Yaml" in {

    Yaml[List[Any]]("data.yml").foreach {
      _ match {
        case a:Athlete => Athlete.create(a)
        case w:Workout => Workout.create(w)
        case c:Comment => Comment.create(c)
      }
    }

    Athlete.count().single() should be (2)
    Workout.count().single() should be (3)
    Comment.count().single() should be (3)

    Athlete.connect("mraible@gmail.com", "beer") should not be (None)
    Athlete.connect("trishmcginity@gmail.com", "whiskey") should not be (None)
    Athlete.connect("trishmcginity@gmail.com", "badpassword") should be (None)
    Athlete.connect("fred@gmail.com", "secret") should be (None)

    val allWorkoutsWithAthleteAndComments = Workout.allWithAthleteAndComments

    allWorkoutsWithAthleteAndComments.length should be (3)

    val (workout,athlete,comments) = allWorkoutsWithAthleteAndComments(1)
    workout.title should be ("Monarch Lake Trail")
    athlete.firstName should be ("Matt")
    comments.length should be (2)

    // We have a referential integrity error
    evaluating {
      Athlete.delete("email={email}")
        .on("email" -> "mraible@gmail.com").executeUpdate()
    } should produce[java.sql.SQLException]

    Workout.delete("athleteId={id}")
      .on("id" -> 1).executeUpdate() should be (2)

    Athlete.delete("email={email}")
      .on("email" -> "mraible@gmail.com").executeUpdate() should be (1)

    Athlete.count().single() should be (1)
    Workout.count().single() should be (1)
    Comment.count().single() should be (0)
  }
}