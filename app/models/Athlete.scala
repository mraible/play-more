package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Athlete(id: Pk[Long], email: String, password: String, firstName: String, lastName: String)

object Athlete {

  val simple = {
    get[Pk[Long]]("id") ~
      get[String]("email") ~
      get[String]("password") ~
      get[String]("firstName") ~
      get[String]("lastName") map {
      case id ~ email ~ password ~ firstName ~ lastName => Athlete(id, email, password, firstName, lastName)
    }
  }

  def apply(firstName: String) = new Athlete(NotAssigned, null, null, firstName, null)

  def connect(email: String, password: String): Option[Athlete] = {
    DB.withConnection {
      implicit connection =>
        SQL("select * from athlete where email = {email} and password = {password}")
          .on("email" -> email, "password" -> password).using(simple).singleOpt()
    }
  }

  def count(): Long = {
    DB.withConnection {
      implicit connection =>
        SQL("select count(*) from athlete").as(scalar[Long].single)
    }
  }

  def find(field: String, value: String): Seq[Athlete] = {
    DB.withConnection {
      implicit connection =>
        SQL("select * from athlete where " + field + " = {" + field + "}")
          .on(Symbol(field) -> value).as(Athlete.simple *)
    }
  }

  def findBy(id: Pk[Long]): Athlete = {
    DB.withConnection {
      implicit connection =>
        SQL("select * from athlete where id = {id}")
          .on("id" -> id.get).using(simple).single()
    }
  }

  def findAll(): Seq[Athlete] = {
    DB.withConnection {
      implicit connection =>
        SQL("select * from athlete").as(Athlete.simple *)
    }
  }

  def update(athlete: Athlete) = {
    DB.withConnection {
      implicit connection =>
        SQL("update athlete set email = {email}, password = {password}" +
          "firstName = {firstName}, lastName = {lastName} where id = {id}").on(
          'email -> athlete.email,
          'password -> athlete.password,
          'firstName -> athlete.firstName,
          'lastName -> athlete.lastName,
          'id -> athlete.id
        ).executeUpdate()
    }
  }

  def create(athlete: Athlete) = {
    DB.withConnection {
      implicit connection =>
        SQL("insert into athlete(email, password, firstName, lastName) values " +
          "({email}, {password}, {firstName}, {lastName})").on(
          'email -> athlete.email,
          'password -> athlete.password,
          'firstName -> athlete.firstName,
          'lastName -> athlete.lastName
        ).executeInsert()
    }
  }

  def delete(id: Long) = {
    DB.withConnection { implicit connection =>
      SQL("delete from athlete where id = {id}").on('id -> id).executeUpdate()
    }
  }

}