package models

import play.db.anorm._
import play.db.anorm.defaults._

case class User (
  id: Pk[Long],
  email: String, password: String, firstName: String, lastName: String
) {
}

object User extends Magic[User](Some("user")) {
    def connect(email: String, password: String) = {
        User.find("email = {email} and password = {password}")
            .on("email" -> email, "password" -> password)
            .first()
    }

    def apply(firstName: String) = new User(NotAssigned, null, null, firstName, null)
}