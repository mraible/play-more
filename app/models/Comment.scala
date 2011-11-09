package models

import play.db.anorm._
import play.db.anorm.defaults._
import java.util.Date

case class Comment(
  id: Pk[Long],
  author: String, content: String, postedAt: Date, workoutId: Long
)

object Comment extends Magic[Comment] {
  def apply(workoutId: Long, author: String, content: String) = {
    new Comment(NotAssigned, author, content, new Date(), workoutId)
  }
}