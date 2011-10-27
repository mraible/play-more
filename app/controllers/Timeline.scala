package controllers

import play.data.validation._
import play.mvc.Controller
import models._

object Timeline extends Controller with Scalate {

  def index = {
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
      NotFound("No such Workout")
    }
  }

  def postComment(postId:Long) = {
    val author = params.get("author")
    val content = params.get("content")
    Validation.required("author", author)
    Validation.required("content", content)
    if (Validation.hasErrors) {
      renderArgs.put("template", "Timeline/show")
      show(postId)
    } else {
      Comment.create(Comment(postId, author, content))
      flash += "success" -> ("Thanks for posting " + author + "!")
      Action(show(postId))
    }
  }
}