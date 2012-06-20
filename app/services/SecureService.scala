package services

import play.api.Application
import securesocial.core.{UserServicePlugin, UserId, SocialUser}

/**
 * A Sample In Memory user service in Scala
 *
 * IMPORTANT: This is just a sample and not suitable for a production environment since
 * it stores everything in memory.
 */
class InMemoryUserService(application: Application) extends UserServicePlugin(application) {
  private var users = Map[String, SocialUser]()

  def find(id: UserId) = {
    users.get(id.id + id.providerId)
  }

  def save(user: SocialUser) {
    users = users + (user.id.id + user.id.providerId -> user)
  }
}