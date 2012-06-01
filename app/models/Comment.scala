package models

import anorm._
import anorm.SqlParser._

import java.util.Date

import play.api.db.DB
import play.api.Play.current

case class Comment(
  id: Pk[Long],
  author: String, content: String, postedAt: Date, workoutId: Long
)

object Comment {
  def apply(workoutId: Long, author: String, content: String) = {
    new Comment(NotAssigned, author, content, new Date(), workoutId)
  }

  val simple = {
    get[Pk[Long]]("comment.id") ~
    get[String]("comment.author") ~
    get[String]("comment.content") ~
    get[Long]("comment.postedAt") ~
    get[Long]("comment.workoutId") map {
      case id~author~content~postedAt~workoutId =>
        Comment(id, author, content, new Date(postedAt), workoutId)
    }
  }

  def count(): Long = {
    DB.withConnection {
      implicit connection =>
        SQL("select count(*) from comment").as(scalar[Long].single)
    }
  }

  def create(comment: Comment) = {
    DB.withConnection {
      implicit connection =>
        SQL("insert into comment (author, content, postedAt, workoutId) values " +
          "({author}, {content}, {postedAt}, {workoutId})").on(
          'author -> comment.author,
          'content -> comment.content,
          'postedAt -> new Date(),
          'workoutId -> comment.workoutId
        ).executeInsert()
    }
  }
}