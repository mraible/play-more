package controllers

import play.mvc.Http

trait Scalate {

  def render(args: (Symbol, Any)*) = {
    def defaultTemplate = Http.Request.current().action.replace(".", "/")
    ScalateTemplate(defaultTemplate).render(args: _*);
  }
}