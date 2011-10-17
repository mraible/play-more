package controllers

import play.Play
import play.data.validation.Validation
import play.mvc.{Http, Scope}

object ScalateTemplate {

  import org.fusesource.scalate._
  import org.fusesource.scalate.util._
  import org.fusesource.scalate.layout._

  val scalateType = "." + Play.configuration.get("scalate")

  lazy val scalateEngine = {
    val engine = new TemplateEngine
    engine.resourceLoader = new FileResourceLoader(Some(Play.getFile("/app/templates")))
    engine.classpath = Play.getFile("/tmp/classes").getAbsolutePath
    engine.workingDirectory = Play.getFile("tmp")
    engine.combinedClassPath = true
    engine.classLoader = Play.classloader
    engine.layoutStrategy = new DefaultLayoutStrategy(engine,
      Play.getFile("/app/templates/layouts/default" + scalateType).getAbsolutePath)
    engine
  }

  case class Template(name: String) {

    def render(args: (Symbol, Any)*) = {
      val argsMap = populateRenderArgs(args: _*)

      scalateEngine.layout(name + scalateType, argsMap)
    }
  }

  import scala.collection.JavaConversions._

  def populateRenderArgs(args: (Symbol, Any)*): Map[String, Any] = {
    val renderArgs = Scope.RenderArgs.current();

    args.foreach {
      o =>
        renderArgs.put(o._1.name, o._2)
    }

    renderArgs.put("session", Scope.Session.current())
    renderArgs.put("request", Http.Request.current())
    renderArgs.put("flash", Scope.Flash.current())
    renderArgs.put("params", Scope.Params.current())
    renderArgs.put("errors", Validation.errors())

    // CSS class to add to body
    renderArgs.put("bodyClass", Http.Request.current().action.replace(".", " ").toLowerCase)
    renderArgs.data.toMap
  }

  def apply(template: String) = Template(template)
}