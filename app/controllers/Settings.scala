package controllers

import play.api.mvc.{Action, Controller}


object Settings extends Controller {

  def settings = Action {
    Ok(Scalate("Settings/settings.jade").render())
  }

  def about = Action {
    Ok(Scalate("Settings/about.jade").render())
  }

}