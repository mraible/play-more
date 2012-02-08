package controllers.api

import play.mvc.Controller
import models._
import com.codahale.jerkson.Json._

object WorkoutService extends Controller {

  def workouts() = {
    generate(Workout.find().list())
  }
}