package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import java.util.Date

case class Workout(id: Pk[Long], var title: String, var description: String,
                   var duration: Double, distance: Double, var postedAt: Date,
                   var athleteId: Long) {

  def prevNext: (Option[Workout], Option[Workout]) = {
    DB.withConnection {
      implicit connection =>
        val result = SQL(
          """
                (
                    select p.*, 'next' as pos from workout w
                    where postedAt < {date} order by postedAt desc limit 1
                )
                    union
                (
                    select p.*, 'prev' as pos from workout w
                    where postedAt > {date} order by postedAt asc limit 1
                )

                order by postedAt desc

          """).on("date" -> postedAt).as(
          Workout.withPrevNext *).partition(_._2 == "prev")

        (result._1 match {
          case List((post, "prev")) => Some(post)
          case _ => None
        },
          result._2 match {
            case List((post, "next")) => Some(post)
            case _ => None
          })
    }
  }
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

  lazy val withPrevNext = {
    get[Pk[Long]]("id") ~ get[String]("title") ~ get[String]("description") ~ get[Double]("duration") ~
      get[Double]("distance") ~ get[Date]("postedAt") ~ get[Long]("athleteId") ~ get[String]("pos") map {
      case id ~ title ~ description ~ duration ~ distance ~ postedAt ~ athleteId ~ pos =>
        (Workout(id, title, description, duration, distance, postedAt, athleteId), pos)
    }
  }

  val withAthleteAndComments = Workout.simple ~ Athlete.simple ~ (Comment.simple ?) map {
    case workout ~ athlete ~ comments => (workout, athlete, comments)
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

  def allWithAthleteAndComments: List[(Workout, Athlete, List[Comment])] = DB.withConnection {
    implicit connection =>
      SQL(
        """
          select w.*, a.*, c.* from Workout w
          join Athlete a on w.athleteId = a.id
          left join Comment c on c.workoutId = w.id
          order by w.postedAt desc
        """
      ).as(withAthleteAndComments *)
        .groupBy(row => (row._1, row._2))
        .mapValues(_.unzip3._3.map(_.orNull))
        .map(row => row._2 match {
          case List(null) => (row._1._1, row._1._2, List())
          case comments => (row._1._1, row._1._2, comments)
        }).toList
  }

  def byIdWithAthleteAndComments(id: Long): Option[(Workout, Athlete, List[Comment])] = DB.withConnection {
    implicit connection =>
      Some(SQL(
        """
          select * from Workout w
          join Athlete a on w.athleteId = a.id
          left join Comment c on c.workoutId = w.id
          where w.id = {id}
        """
      ).on('id -> id).as(withAthleteAndComments *)
       .groupBy(row => (row._1, row._2))
       .mapValues(_.unzip3._3.map(_.orNull))
       .map(row => row._2 match {
         case List(null) => (row._1._1, row._1._2, List())
         case comments => (row._1._1, row._1._2, comments)
       }).head)
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
  def create(workout: Workout): Workout = {
    DB.withConnection {
      implicit connection =>
        SQL(
          """
          insert into workout(id, title, description, duration, distance, postedAt, athleteId)
          values ({id}, {title}, {description}, {duration}, {distance}, {postedAt}, {athleteId})
          """
        ).on(
          'id -> workout.id,
          'title -> workout.title,
          'description -> workout.description,
          'duration -> workout.duration,
          'distance -> workout.distance,
          'postedAt -> new Date(),
          'athleteId -> workout.athleteId
        ).executeInsert()
      workout
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

  /**
   * Delete a workout.
   *
   * @param id Id of the athlete to delete workouts for.
   */
  def deleteByAthlete(id: Long) = {
    DB.withConnection {
      implicit connection =>
        SQL("delete from workout where athleteId = {id}").on('id -> id).executeUpdate()
    }
  }
}

