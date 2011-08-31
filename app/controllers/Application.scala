package controllers

import play.mvc._
import models._

object Application extends ScalateController {

  def index = {
    render('user -> User("Raible"))
  }

  def sayHello(name: String) = {
    render('name -> name)
  }
}