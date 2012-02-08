package controllers

import play.data.validation._
import play.modules.scalate._;
import play.mvc.Controller
import models._
import play.db.anorm.NotAssigned

object Profile extends Controller with Scalate {

  def index() = {
    val allWorkouts = Workout.allWithAthleteAndComments
    render(
      'front -> allWorkouts.headOption,
      'older -> allWorkouts.drop(1)
    )
  }

  def show(id: Long) = {
    Workout.byIdWithAthleteAndComments(id).map { w =>
      render(
        'workout -> w,
        'pagination -> w._1.prevNext
      )
    } getOrElse {
      NotFound("No such Profile")
    }
  }

  def edit(id: Option[Long]) = {
    val workout = id.flatMap( id => Workout.find("id={id}").onParams(id).first());
    render('workout -> workout)
  }
  
  def remove(id: Long) = {
    Workout.delete("id={id}").on("id" -> id).executeUpdate()
  }

  def postComment(postId:Long) = {
    val author = params.get("author")
    val content = params.get("content")
    Validation.required("author", author)
    Validation.required("content", content)
    if (Validation.hasErrors) {
      renderArgs.put("template", "Profile/show")
      show(postId)
    } else {
      Comment.create(Comment(postId, author, content))
      flash += "success" -> ("Thanks for posting " + author + "!")
      Action(show(postId))
    }
  }

  def postWorkout(id: Option[Long]) = {

    var workout = params.get("workout", classOf[Workout])

    // change duration to time
    var duration = params.get("workout.duration")
    workout.duration = convertWatchToTime(duration)

    Validation.valid("workout", workout)

    if (false) {
      renderArgs.put("template", "Profile/edit")
      edit(id);
    } else {
      id match {
        case Some(id) => {
          Workout.update(workout)
        }
        case None => {
          workout.postedAt = new java.util.Date
          workout.athleteId = 1
          Workout.create(workout)
          flash += "success" -> ("Nice workout!")
        }
      }
      Action(index())
    }
  }

  def convertWatchToTime(clock: String):Double = {
    clock.replaceAll("00:", "").toDouble
  }
}