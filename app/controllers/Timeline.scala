package controllers

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
      render('workout -> w)
    } getOrElse {
      NotFound("No such Workout")
    }
  }
}