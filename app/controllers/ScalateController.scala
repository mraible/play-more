package controllers

import play.mvc.{Http, Controller}
import models.User._

class ScalateController extends Controller {

  def render(args: (Symbol, Any)*) = {
    def defaultTemplate = Http.Request.current().action.replace(".", "/")
    Scalate(defaultTemplate).render(args: _*);
  }
}