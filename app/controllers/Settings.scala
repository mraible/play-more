package controllers

import play.api.mvc.{Action, Controller}


object Settings extends Controller {

  def settings = Action { request =>
    Ok(Scalate("Settings/settings.jade").render(request))
  }

  def about = Action { request =>
    Ok(Scalate("Settings/about.jade").render(request))
  }

}