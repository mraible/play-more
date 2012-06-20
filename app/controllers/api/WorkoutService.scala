package controllers.api

import models._
import play.api.mvc.{Action, Controller}
import com.codahale.jerkson._
import org.codehaus.jackson.JsonNode

object WorkoutService extends Controller  {

  def workouts = Action { implicit request =>
    var json = Json.generate(Workout.allWithAthlete)
    // todo: figure out regex to replace passwords in json
    json = json.replaceAll("beer|whiskey", "XXX")
    Ok(json).as("application/json")
  }

  def show(id: Long) = Action {
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