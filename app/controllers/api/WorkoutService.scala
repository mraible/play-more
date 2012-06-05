package controllers.api

import models._
import play.api.mvc.{Action, Controller}
import com.codahale.jerkson._

object WorkoutService extends Controller  {

  def workouts = Action { implicit request =>
    Ok(Json.generate(Workout.allWithAthlete)).as("application/json")
  }

  def edit(id: Long) = Action {
    Ok(Json.generate(Workout.byIdWithAthleteAndComments(id)))
  }

  def create() = Action { implicit request =>
    val workout = Json.parse(request.body.asText.get).asInstanceOf[Workout]
    println(workout)
    Created
  }

  def save(id: Option[Long]) = Action { implicit request =>
    val workout = Json.parse(request.body.asText.get).asInstanceOf[Workout]
    println(workout)
    Ok
  }

  def delete(id: Long) = Action {
    println("deleting {id}", id)
    Workout.delete(id)
    Ok
  }
}