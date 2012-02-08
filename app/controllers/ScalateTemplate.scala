package controllers

import play.Play
import play.exceptions.{PlayException,TemplateNotFoundException}
import play.data.validation.Validation
import java.io.{StringWriter,PrintWriter}
import java.io.{File}
import play.mvc.results.ScalateResult
import play.mvc.{Scope, Http}

object ScalateTemplate {

  import org.fusesource.scalate._
  import org.fusesource.scalate.util._
  import org.fusesource.scalate.layout._

  val scalateType = "." + Play.configuration.get("scalate")

  lazy val scalateEngine = {
    val engine = new TemplateEngine
    engine.resourceLoader = new FileResourceLoader(Some(Play.getFile("/app/views")))
    engine.classpath = Play.getFile("/tmp/classes").getAbsolutePath
    engine.workingDirectory = Play.getFile("tmp")
    engine.combinedClassPath = true
    engine.classLoader = Play.classloader
    engine.layoutStrategy = new DefaultLayoutStrategy(engine,
      Play.getFile("/app/views/layouts/default" + scalateType).getAbsolutePath)
      
    engine.bindings = List(
      Binding("context", SourceCodeHelper.name(classOf[DefaultRenderContext]), true)
    )
          
    engine
  }

  case class Template(name: String) {
    
    def render(args: (Symbol, Any)*) = {
      val argsMap = populateRenderArgs(args: _*)
      
      val buffer = new StringWriter()
      var context = new DefaultRenderContext(name, scalateEngine, new PrintWriter(buffer))
      
      try {
        val templatePath = new File(Play.applicationPath+"/app/views","/"+name).toString
          .replace(new File(Play.applicationPath+"/app/views").toString,"")
        scalateEngine.layout(templatePath + scalateType, argsMap)
      } catch {
        case ex:TemplateNotFoundException => {
          if(ex.isSourceAvailable) {
            throw ex
          }
          val element = PlayException.getInterestingStrackTraceElement(ex)
          if (element != null) {
             throw new TemplateNotFoundException(name, 
               Play.classes.getApplicationClass(element.getClassName()), element.getLineNumber());
          } else {
             throw ex
          }
        }  
        case ex:InvalidSyntaxException => handleSpecialError(context,ex)
        case ex:CompilerException => handleSpecialError(context,ex)
        case ex:Exception => handleSpecialError(context,ex)
      } finally {
        if (buffer.toString.length > 0)
          throw new ScalateResult(buffer.toString,name)
      }
    }
  }

  def apply(template: String) = Template(template)

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
    renderArgs.put("errors", validationErrors)
    renderArgs.put("config", Play.configuration)

    // CSS class to add to body
    renderArgs.put("bodyClass", Http.Request.current().action.replace(".", " ").toLowerCase)
    renderArgs.data.toMap
  }

  private def handleSpecialError(context:DefaultRenderContext,ex:Exception) {
    context.attributes("javax.servlet.error.exception") = ex
    context.attributes("javax.servlet.error.message") = ex.getMessage
    try {
      scalateEngine.layout(scalateEngine.load(errorTemplate), context)
    } catch {
      case ex:Exception =>
        // TODO use logging API from Play here...
        println("Caught: " + ex)
        ex.printStackTrace

    }
  }
  
  private def errorTemplate:String = {
    val fullPath = new File(Play.applicationPath,"/app/views/errors/500.scaml").toString 
    fullPath.replace(new File(Play.applicationPath+"/app/views").toString,"")
  }
  // --- ROUTERS
  def action(action: => Any) = {
    new play.mvc.results.ScalaAction(action).actionDefinition.url
  }

  implicit def validationErrors:Map[String,play.data.validation.Error] = {
    import scala.collection.JavaConverters._
    Map.empty[String,play.data.validation.Error] ++ 
      Validation.errors.asScala.map( e => (e.getKey, e) )
  }

  def asset(path:String) = play.mvc.Router.reverse(play.Play.getVirtualFile(path))
}