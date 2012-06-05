package controllers

import play.api.data.validation._
import models._
import play.api.mvc.{Action, Controller}

object Profile extends Controller {

  def index() = Action {
    val allWorkouts = Workout.allWithAthleteAndComments
    Ok(Scalate("/Profile/index.jade").render(
      'front -> allWorkouts.headOption,
      'older -> allWorkouts.drop(1)
    ))
  }

  def show(id: Long) = Action {
    Workout.byIdWithAthleteAndComments(id).map { w =>
      Ok(Scalate("/Profile/show.jade").render(
        'workout -> w,
        'pagination -> w._1.prevNext
      ))
    } getOrElse {
      NotFound("No such Profile")
    }
  }

  def edit(id: Option[Long]) = Action {
    val workout = Workout.find("id", "" + id)
    Ok(Scalate("/Profile/edit.jade").render('workout -> workout))
  }

  def remove(id: Long) = Action {
    Workout.delete(id)
    //flash.success("Workout removed successfully.");
    Ok
  }

  def postComment(postId:Long) = Action {
    /*val author = params.get("author")
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
    }  */
    NotImplemented
  }

  def postWorkout(id: Option[Long]) = Action {
    println("posting")
    /*var workout = params.get("workout", classOf[Workout])
    // handle update from content editable
    if (id != null && workout.id == null) {
      val w = Workout.find("id={id}").on("id" -> id).first()
      w.get.title = workout.title
      w.get.description = workout.description
      workout = w.get
    } else {
      // change duration to time
      var duration = params.get("workout.duration")
      workout.duration = convertWatchToTime(duration)
      Validation.valid("workout", workout)
    }

    if (false) { // TODO: Fix me
      renderArgs.put("template", "Profile/edit")
      edit(id);
    } else {
      println("Creating workout: " + workout)
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
    }*/
    Ok
  }

  def convertWatchToTime(clock: String):Double = {
    if (clock != null && clock.trim.length > 0) {
      clock.replaceAll("00:", "").toDouble
    } else {
      0
    }
  }
}