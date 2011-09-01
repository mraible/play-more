package controllers

import play.mvc._
import models._

object Application extends Controller with Scalate {

  def index = {
    render('user -> User("Raible"))
  }

  def sayHello(name: String) = {
    render('name -> name)
  }
}