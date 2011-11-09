package controllers

import play.modules.scalate._;
import play.mvc._
import models._

object Home extends Controller with Scalate {

  def index() = {
    val athlete = Athlete("Matt")
    render('athlete -> athlete)
  }

  def where = {
    render()
  }
  
  def track = {
    render()
  }
}