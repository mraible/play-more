package models

import java.util.Date
import play.db.anorm._
import play.db.anorm.defaults._
import play.db.anorm.SqlParser._

case class Workout(
  id: Pk[Long],
  title: String, description: String,
  duration: Double, distance: Double,
  postedAt: Date, athlete_id: Long
)

object Workout extends Magic[Workout] {

  def allWithAthlete: List[(Workout, Athlete)] =
    SQL(
      """
          select * from Workout w
          join Athlete a on w.athlete_id = a.id
          order by w.postedAt desc
      """
    ).as(Workout ~< Athlete ^^ flatten *)

  def allWithAthleteAndComments: List[(Workout, Athlete, List[Comment])] =
    SQL(
      """
          select * from Workout w
          join Athlete a on w.athlete_id = a.id
          left join Comment c on c.workout_id = w.id
          order by w.postedAt desc
      """
    ).as(Workout ~< Athlete ~< Workout.spanM(Comment) ^^ flatten *)

  def byIdWithAthleteAndComments(id: Long): Option[(Workout, Athlete, List[Comment])] =
    SQL(
      """
          select * from Workout w
          join Athlete a on w.athlete_id = a.id
          left join Comment c on c.workout_id = w.id
          where w.id = {id}
      """
    ).on("id" -> id).as(Workout ~< Athlete ~< Workout.spanM(Comment) ^^ flatten ?)
}

