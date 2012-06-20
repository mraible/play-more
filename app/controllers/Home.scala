package controllers

import play.api.mvc._
import models._

object Home extends Controller {

  def index() = Action { request =>
    val athlete = Athlete("Matt")
    Ok(Scalate("/Home/index.jade").render(request, 'athlete -> athlete))
  }

  def where = Action { request =>
    Ok(Scalate("/Home/where").render(request))
  }
  
  def track = Action { request =>
    Ok(Scalate("/Home/track").render(request))
  }
}