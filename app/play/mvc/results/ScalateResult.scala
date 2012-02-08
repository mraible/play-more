package play.mvc.results

import play.mvc.Http.{Request,Response}
import play.exceptions.UnexpectedException
import play.libs.MimeTypes

class ScalateResult(content:String,template:String) extends Result {

  def apply(request:Request, response:Response) {
    try {
      setContentTypeIfNotSet(response, MimeTypes.getContentType(template, "text/html"))
      response.out.write(content.getBytes("utf-8"))
    } catch {
      case e:Exception => throw new UnexpectedException(e)
    }
  }
}
