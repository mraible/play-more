package controllers

import play.api._
import http.{Writeable, ContentTypeOf, ContentTypes}
import mvc.{RequestHeader, Request, AnyContent, Codec}
import play.api.Play.current
import org.fusesource.scalate.layout.DefaultLayoutStrategy
import collection.mutable

object Scalate {

  import org.fusesource.scalate._
  import org.fusesource.scalate.util._

  var format = Play.configuration.getString("scalate.format") match {
    case Some(configuredFormat) => configuredFormat
    case _ => "test"
  }

  lazy val scalateEngine = {
    val engine = new TemplateEngine
    engine.resourceLoader = new FileResourceLoader(Some(Play.getFile("app/views")))
    engine.layoutStrategy = new DefaultLayoutStrategy(engine,
      Play.getFile("app/views/layouts/default." + format).getCanonicalPath)
    engine.classpath = Play.getFile("/tmp/classes").getAbsolutePath
    engine.workingDirectory = Play.getFile("tmp")
    engine.combinedClassPath = true
    engine.classLoader = Play.classloader
    engine
  }

  def apply(template: String) = Template(template)

  case class Template(name: String) {

    def render(implicit request: RequestHeader, args: (Symbol, Any)*) = {

      val argsMap = populateRenderArgs(name, args: _*)

      ScalateContent {
        scalateEngine.layout(name, argsMap)
      }
    }
  }

  case class ScalateContent(val cont: String)

  implicit def writeableOf_ScalateContent(implicit codec: Codec): Writeable[ScalateContent] = {
    Writeable[ScalateContent](scalate => codec.encode(scalate.cont))
  }

  implicit def contentTypeOf_ScalateContent(implicit codec: Codec): ContentTypeOf[ScalateContent] = {
    ContentTypeOf[ScalateContent](Some(ContentTypes.HTML))
  }


  implicit def validationErrors: Map[String, play.api.data.validation.ValidationError] = {
    Map.empty[String, play.api.data.validation.ValidationError] //      Validation.errors.asScala.map( e => (e.getKey, e) )
  }

  def populateRenderArgs(name: String, args: (Symbol, Any)*): Map[String, Any] = {
    val renderArgs = new mutable.HashMap[String, Any]

    args.foreach {
      o =>
        renderArgs.put(o._1.name, o._2)
    }

    // todo: figure out how to add flash to args

    // CSS class to add to body
    var bodyClass = name.replace("/", " ").toLowerCase
    bodyClass = bodyClass.replace("." + format, "").trim

    renderArgs.put("bodyClass", bodyClass)
    renderArgs.toMap
  }

}