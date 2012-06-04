package controllers

import play.api.mvc._
import models._

object Home extends Controller {

  def index() = Action {
    val athlete = Athlete("Matt")
    Ok(Scalate("/Home/index.jade").render('athlete -> athlete))
  }

  def where = {
    Ok(Scalate("/Home/where").render())
  }
  
  def track = {
    Ok(Scalate("/Home/track").render())
  }
}