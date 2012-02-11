package controllers.api

import play.mvc.Controller
import models._
import com.codahale.jerkson.Json._
import controllers.BasicAuth

object WorkoutService extends Controller with BasicAuth {

  def workouts = {
    response.setContentTypeIfNotSet("application/json")
    generate(Workout.find().list())
  }

  def edit(id: Long) = {
    generate(Workout.byIdWithAthleteAndComments(id))
  }

  def create() = {
    var workout = params.get("workout", classOf[Workout])
    println(workout)
  }

  def save(id: Option[Long]) = {
    var workout = params.get("workout", classOf[Workout])
    println(workout)
  }

  def delete(id: Long) = {
    println("deleting {id}", id)
    Workout.delete("id={id}").on("id" -> id).executeUpdate()
  }

}