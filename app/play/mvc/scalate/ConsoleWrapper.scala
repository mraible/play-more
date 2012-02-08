package play.mvc.scalate

import java.io.File
import org.fusesource.scalate._
import play._

class ConsoleWrapper(context:DefaultRenderContext) extends org.fusesource.scalate.console.ConsoleHelper(context) {
  
  override def servletContext = null
  override def realPath(uri: String) = {
    new File(Play.applicationPath,"/app/views/"+uri).toString
  }  

}