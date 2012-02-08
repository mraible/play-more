package controllers

import play.mvc.{Scope, Http}

trait Scalate {

  def render(args: (Symbol, Any)*) = {
    var template = Scope.RenderArgs.current().get("template")
    if (template == null) {
      template = Http.Request.current().action.replace(".", "/")
    }

    renderTemplate(template.toString, args: _*);
  }

  def renderTemplate(template: String, args: (Symbol, Any)*) = {
    ScalateTemplate(template).render(args: _*);
  }
}