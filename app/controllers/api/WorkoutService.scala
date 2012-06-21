package controllers.api

import models._
import play.api.mvc.{Action, Controller}
import com.codahale.jerkson._
import org.codehaus.jackson.JsonNode

object WorkoutService extends Controller  {

  def workouts = Action { implicit request =>
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
      var json = Json.generate(Workout.allWithAthlete)
      // todo: figure out regex to replace passwords in json
      json = json.replaceAll("beer|whiskey", "XXX")
      Ok(json).as("application/json").withHeaders(
        "Access-Control-Allow-Origin" -> "*",
        "Access-Control-Allow-Methods" -> "GET,POST",
        "Access-Control-Max-Age" -> "360",
        "Access-Control-Allow-Headers" -> "x-requested-with"
      )
    }
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