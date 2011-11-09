package models

import java.util.Date
import play.db.anorm._
import play.db.anorm.defaults._
import play.db.anorm.SqlParser._

import play.data.validation.Annotations._

case class Workout(
  id: Pk[Long],
  title: String, description: String,
  duration: Double, distance: Double,
  var postedAt: Date, var athleteId: Long
) {

  def prevNext = {
    SQL(
        """
            (
                select *, 'next' as w from workout
                where postedAt < {date} order by postedAt desc limit 1
            )
                union
            (
                select *, 'prev' as w from workout
                where postedAt > {date} order by postedAt asc limit 1
            )

            order by postedAt desc

        """
    ).on("date" -> postedAt).as(
        opt('w.is("prev")~>Workout.on("")) ~ opt('w.is("next")~>Workout.on("")) ^^ flatten
    )
  }
}

object Workout extends Magic[Workout] {

  def allWithAthlete: List[(Workout, Athlete)] =
    SQL(
      """
          select * from Workout w
          join Athlete a on w.athleteId = a.id
          order by w.postedAt desc
      """
    ).as(Workout ~< Athlete ^^ flatten *)

  def allWithAthleteAndComments: List[(Workout, Athlete, List[Comment])] =
    SQL(
      """
          select * from Workout w
          join Athlete a on w.athleteId = a.id
          left join Comment c on c.workoutId = w.id
          order by w.postedAt desc
      """
    ).as(Workout ~< Athlete ~< Workout.spanM(Comment) ^^ flatten *)

  def byIdWithAthleteAndComments(id: Long): Option[(Workout, Athlete, List[Comment])] =
    SQL(
      """
          select * from Workout w
          join Athlete a on w.athleteId = a.id
          left join Comment c on c.workoutId = w.id
          where w.id = {id}
      """
    ).on("id" -> id).as(Workout ~< Athlete ~< Workout.spanM(Comment) ^^ flatten ?)
}

