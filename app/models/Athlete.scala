package models

import play.db.anorm._
import play.db.anorm.defaults._

case class Athlete(
  id: Pk[Long],
  email: String, password: String, firstName: String, lastName: String
  ) {
}

object Athlete extends Magic[Athlete] {
  def connect(email: String, password: String) = {
    Athlete.find("email = {email} and password = {password}")
      .on("email" -> email, "password" -> password)
      .first()
  }

  def apply(firstName: String) = new Athlete(NotAssigned, null, null, firstName, null)
}