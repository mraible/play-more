package controllers

import play.api._
import http.{Writeable, ContentTypeOf, ContentTypes}
import mvc.Codec
import play.api.Play.current
import org.fusesource.scalate.layout.DefaultLayoutStrategy

object Scalate {

  import org.fusesource.scalate._
  import org.fusesource.scalate.util._

  var format = Play.configuration.getString("scalate.format") match {
    case Some(configuredFormat) => configuredFormat
    case _ => "test"
  }

  lazy val scalateEngine = {
    val engine = new TemplateEngine
    engine.resourceLoader = new FileResourceLoader(Some(Play.getFile("/app/views")))
    engine.layoutStrategy = new DefaultLayoutStrategy(engine, Play.getFile("/app/views/layouts/default." + format).getAbsolutePath)
    engine.classpath = Play.getFile("/tmp/classes").getAbsolutePath
    engine.workingDirectory = Play.getFile("tmp")
    engine.combinedClassPath = true
    engine.classLoader = Play.classloader
    engine
  }

  def apply(template: String) = Template(template)

  case class Template(name: String) {

    def render(args: (Symbol, Any)*) = {
      ScalateContent{
        scalateEngine.layout(name, args.map {
          case (k, v) => k.name -> v
        } toMap)
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

  // --- ROUTERS
  def action(action: => Any) = {
    //new play.api.mvc.Action(action).actionDefinition.url
    ""
  }

  /*
  implicit def validationErrors:Map[String,play.data.validation.Error] = {
    import scala.collection.JavaConverters._
    Map.empty[String,play.data.validation.Error] ++
      Validation.errors.asScala.map( e => (e.getKey, e) )
  }*/

  def asset(path:String) = "";//  play.mvc.Router.reverse(play.Play.getVirtualFile(path))

}