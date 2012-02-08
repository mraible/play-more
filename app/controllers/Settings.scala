package controllers

import play.modules.scalate._
import play.mvc.Controller

object Settings extends Controller with Scalate {

  def settings = {
    render()
  }

  def about = {
    render()
  }

}