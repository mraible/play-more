package controllers

import play.mvc._
import models._

object Home extends Controller with Scalate {
  import com.codahale.jerkson.Json._

  def index = {
    val user = User("Matt")
    //generate(user)
  }

  def where = {
    render()
  }
  
  def track = {
    render()
  }
}