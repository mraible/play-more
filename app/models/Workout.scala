package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import java.util.Date

case class Workout(id: Pk[Long], var title: String, var description: String,
                   var duration: Double, distance: Double, var postedAt: Date,
                   var athleteId: Long) {
}

/**
 * Helper for pagination.
 */
case class Page[A](items: List[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

object Workout {

  /**
   * Parse a Workout from a ResultSet
   */
  val simple = {
    get[Pk[Long]]("workout.id") ~
      get[String]("workout.title") ~
      get[String]("workout.description") ~
      get[Double]("workout.duration") ~
      get[Double]("workout.distance") ~
      get[Date]("workout.postedAt") ~
      get[Long]("workout.athleteId") map {
      case id ~ title ~ description ~ duration ~ distance ~ postedAt ~ athleteId =>
        Workout(id, title, description, duration, distance, postedAt, athleteId)
    }
  }

  lazy val withAthlete = simple ~ Athlete.simple map {
    case workout ~ athlete => (workout, athlete)
  }

  lazy val withAthleteAndComments = {
    (Workout.withAthlete ~ Comment.simple *) map {
      items =>
        items.groupBy(_._1).headOption.map {
          case (workoutWithAthlete, list) => (workoutWithAthlete, list.map(_._2))
        }
    }
  }

  def find(field: String, value: String): Seq[Workout] = {
    DB.withConnection {
      implicit connection =>
        SQL("select * from workout where " + field + " = {" + field + "}")
          .on(Symbol(field) -> value).as(Workout.simple *)
    }
  }

  def allWithAthlete: List[(Workout, Athlete)] = DB.withConnection {
    implicit connection =>
      SQL(
        """
          select * from Workout w
          join Athlete a on w.athleteId = a.id
          order by w.postedAt desc
        """
      ).as(Workout.withAthlete *)
  }

  def allWithAthleteAndComments: List[((Workout, Athlete), List[Comment])] = DB.withConnection {
    implicit connection =>
      SQL(
        """
          select * from Workout w
          join Athlete a on w.athleteId = a.id
          left join Comment c on c.workoutId = w.id
          order by w.postedAt desc
        """
      ).as(Workout.withAthleteAndComments).toList
  }

  def byIdWithAthleteAndComments(id: Long): Option[((Workout, Athlete), List[Comment])] = DB.withConnection {
    implicit connection =>
      SQL(
        """
          select * from Workout w
          join Athlete a on w.athleteId = a.id
          left join Comment c on c.workoutId = w.id
          where w.id = {id}
        """
      ).on('id -> id).as(Workout.withAthleteAndComments)
  }


  /**
   * Return a page of (Workout,Athlete).
   *
   * @param page Page to display
   * @param pageSize Number of workouts per page
   * @param orderBy workout property used for sorting
   * @param filter Filter applied on the name column
   */
  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[(Workout, Athlete)] = {

    val offset = pageSize * page

    DB.withConnection {
      implicit connection =>

        val workouts = SQL(
          """
          select * from workout
          left join athlete on workout.athleteId = athlete.id
          where workout.title like {filter}
          order by {orderBy} nulls last
          limit {pageSize} offset {offset}
          """
        ).on(
          'pageSize -> pageSize,
          'offset -> offset,
          'filter -> filter,
          'orderBy -> orderBy
        ).as(Workout.withAthlete *)

        val totalRows = SQL(
          """
          select count(*) from workout
          left join athlete on workout.athleteId = athlete.id
          where workout.title like {filter}
          """
        ).on(
          'filter -> filter
        ).as(scalar[Long].single)

        Page(workouts, page, offset, totalRows)

    }
  }

  def count(): Long = {
    DB.withConnection {
      implicit connection =>
        SQL("select count(*) from workout").as(scalar[Long].single)
    }
  }

  /**
   * Update a workout.
   *
   * @param id The workout id
   * @param workout The workout values.
   */
  def update(id: Long, workout: Workout) = {
    DB.withConnection {
      implicit connection =>
        SQL(
          """
          update workout
          set title = {title}, description = {description}, duration = {duration}, distance = {distance}
          where id = {id}
          """
        ).on(
          'id -> id,
          'title -> workout.title,
          'description -> workout.description,
          'duration -> workout.duration,
          'distance -> workout.distance
        ).executeUpdate()
    }
  }

  /**
   * Insert a new workout.
   *
   * @param workout The workout values.
   */
  def create(workout: Workout) = {
    DB.withConnection {
      implicit connection =>
        SQL(
          """
          insert into workout(title, description, duration, distance, postedAt, athleteId)
          values ({title}, {description}, {duration}, {distance}, {postedAt}, {athleteId})
          """
        ).on(
          'title -> workout.title,
          'description -> workout.description,
          'duration -> workout.duration,
          'distance -> workout.distance,
          'postedAt -> new Date(),
          'athleteId -> workout.athleteId
        ).executeInsert()
    }
  }

  /**
   * Delete a workout.
   *
   * @param id Id of the workout to delete.
   */
  def delete(id: Long) = {
    DB.withConnection {
      implicit connection =>
        SQL("delete from workout where id = {id}").on('id -> id).executeUpdate()
    }
  }
}

