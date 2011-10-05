package controllers

import play.mvc._
import models._

object Home extends Controller with Scalate {

  def index = {
    var user = User("Matt")
    render('user -> user)
  }

  def where() = {
    render()
  }
}