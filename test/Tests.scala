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

  it should "create a Workout" in {

    User.create(User(Id(1), "bob@gmail.com", "secret", "Bob", "Johnson"))
    Workout.create(Workout(NotAssigned, "My first run", "With a hangover", 4, 38, new Date, 1))

    Workout.count().single() should be (1)

    val workouts = Workout.find("user_id={id}").on("id" -> 1).as(Workout*)

    workouts.length should be (1)

    val firstWorkout = workouts.headOption

    firstWorkout should not be (None)
    firstWorkout.get.user_id should be (1)
    firstWorkout.get.title should be ("My first run")
    firstWorkout.get.description should be ("With a hangover")
  }

  it should "retrieve Workouts by user" in {

    User.create(User(Id(1), "bob@gmail.com", "secret", "Bob", "Johnson"))
    Workout.create(Workout(NotAssigned, "My 1st workout", "Yee Haw!", 3.20, 70, new Date, 1))

    val workouts = Workout.allWithUser

    workouts.length should be (1)

    val (workout,user) = workouts.head

    workout.title should be ("My 1st workout")
    user.firstName should be ("Bob")
  }

  it should "support Comments" in {

    User.create(User(Id(1), "bob@gmail.com", "secret", "Bob", "Johnson"))
    Workout.create(Workout(Id(1), "My first workout", "Yee Haw!", 2, 10, new Date, 1))
    Comment.create(Comment(NotAssigned, "Jim", "Nice workout!", new Date, 1))
    Comment.create(Comment(NotAssigned, "Bryan", "Great weather today!", new Date, 1))

    User.count().single() should be (1)
    Workout.count().single() should be (1)
    Comment.count().single() should be (2)

    val Some( (post,user,comments) ) = Workout.byIdWithUserAndComments(1)

    post.title should be ("My first workout")
    user.firstName should be ("Bob")
    comments.length should be (2)
    comments(0).author should be ("Jim")
    comments(1).author should be ("Bryan")

  }
}