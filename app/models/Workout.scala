package models

import java.util.Date
import play.db.anorm._
import play.db.anorm.defaults._
import play.db.anorm.SqlParser._

case class Workout (
    id: Pk[Long],
    title: String, description: String,
    duration: Double, distance: Double,
    postedAt: Date, user_id: Long
)

object Workout extends Magic[Workout] {

  def allWithUser:List[(Workout,User)] =
    SQL(
        """
            select * from Workout w
            join User u on w.user_id = u.id
            order by w.postedAt desc
        """
    ).as( Workout ~< User ^^ flatten * )

  def allWithUserAndComments:List[(Workout,User,List[Comment])] =
    SQL(
        """
            select * from Workout w
            join User u on w.user_id = u.id
            left join Comment c on c.workout_id = w.id
            order by w.postedAt desc
        """
    ).as( Workout ~< User ~< Workout.spanM( Comment ) ^^ flatten * )

  def byIdWithUserAndComments(id: Long):Option[(Workout,User,List[Comment])] =
    SQL(
        """
            select * from Workout w
            join User u on w.user_id = u.id
            left join Comment c on c.workout_id = w.id
            where w.id = {id}
        """
    ).on("id" -> id).as( Workout ~< User ~< Workout.spanM( Comment ) ^^ flatten ? )
}

