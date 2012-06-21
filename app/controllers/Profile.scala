package controllers

import models._
import play.api.mvc.{Action, Controller}
import play.api.data._
import play.api.data.Forms._
import anorm.NotAssigned

object Profile extends Controller {

  val workoutForm = Form(
    mapping(
      "id" -> ignored(NotAssigned: anorm.Pk[Long]),
      "title" -> text,
      "description" -> text,
      "duration" -> nonEmptyText,
      "distance" -> nonEmptyText,
      "postedAt" -> optional(date),
      "athleteId" -> optional(longNumber)
    )((id, title, description, duration, distance, postedAt, athleteId) =>
      Workout(id, title, description, convertWatchToTime(duration), distance.toDouble, null, 0))
      ((w: Workout) =>
        Some((w.id, w.title, w.description, w.duration.toString, w.distance.toString, null, Some(0))))
  )

  def index() = Action {
    implicit request =>
      val allWorkouts = Workout.allWithAthleteAndComments
      Ok(Scalate("/Profile/index.jade").render(request,
        'front -> allWorkouts.headOption,
        'older -> allWorkouts.drop(1)
      ))
  }

  def show(id: Long) = Action {
    request =>
      Workout.byIdWithAthleteAndComments(id).map {
        w =>
          Ok(Scalate("/Profile/show.jade").render(request,
            'workout -> w,
            'pagination -> w._1.prevNext
          ))
      } getOrElse {
        NotFound("No such Profile")
      }
  }

  def edit(id: Option[Long]) = Action {
    request =>
      if (id != None) {
        val workout = Workout.find("id", id.get.toString)
        Ok(Scalate("/Profile/edit.jade").render(request, 'workout -> workout))
      } else {
        Ok(Scalate("/Profile/edit.jade").render(request))
      }

  }

  def remove(id: Long) = Action { implicit request =>
      Workout.delete(id)
      Ok {
        flash.get("success").getOrElse("Workout removed successfully.")
      }
  }

  def postComment(postId: Long) = Action {
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
    implicit request =>
      // Necessary if you want to run mobile app in local browser
      if (request.method == "OPTIONS") {
        println("OPTIONS")
        Ok.withHeaders(
          "Access-Control-Allow-Origin" -> "*",
          "Access-Control-Allow-Methods" -> "GET,POST",
          "Access-Control-Max-Age" -> "360",
          "Access-Control-Allow-Headers" -> "x-requested-with"
        )
      } else {
        workoutForm.bindFromRequest.fold(
          form => {
            println(form)
            Ok(Scalate("/Profile/edit.jade").render(request, 'errors -> form.errors))
          },
          workout => {
            println("Creating workout: " + workout)
            id match {
              case Some(id) => {
                Workout.update(workout)
              }
              case None => {
                workout.postedAt = new java.util.Date
                workout.athleteId = 1
                Workout.create(workout)
                flash.get("success").getOrElse("Nice workout!")
              }
            }

            request.headers.get("X-Requested-With") match {
              case Some("XMLHttpRequest") =>
                println("ajax")
                Created.as("application/json").withHeaders(
                          "Access-Control-Allow-Origin" -> "*",
                          "Access-Control-Allow-Methods" -> "GET,POST",
                          "Access-Control-Max-Age" -> "360",
                          "Access-Control-Allow-Headers" -> "x-requested-with"
                        )
              case None =>
                Redirect(routes.Profile.index())
            }
          }
        )
      }
  }

  def convertWatchToTime(clock: String): Double = {
    if (clock != null && clock.trim.length > 0) {
      clock.replaceAll("00:", "").toDouble
    } else {
      0
    }
  }
}