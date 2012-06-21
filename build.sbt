import com.mojolly.scalate.ScalatePlugin._

seq(scalateSettings:_*)

scalateTemplateDirectory in Compile <<= (baseDirectory) { _ / "app/views" }

//scalateImports ++= Seq(
//  "import OAuth2Imports._",
//  "import model._"
//)
//
//scalateBindings ++= Seq(
//  Binding("flash", "scala.collection.Map[String, Any]", defaultValue = "Map.empty"),
//  Binding("session", "org.scalatra.Session"),
//  Binding("sessionOption", "Option[org.scalatra.Session]"))