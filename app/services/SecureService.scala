package services

import play.db.anorm.NotAssigned
import play.libs.Codec
import collection.mutable.{SynchronizedMap, HashMap}
import models.Athlete
import securesocial.provider.{ProviderType, UserService, SocialUser, UserId}

class SecureService extends UserService.Service {
  val activations = new HashMap[String, SocialUser] with SynchronizedMap[String, SocialUser]

  def find(userId: UserId): SocialUser = {
    val user = Athlete.find("email={email}").on("email" -> userId.id).first()

    user match {
      case Some(user) => {
        val socialUser = new SocialUser
        socialUser.id = userId
        socialUser.displayName = user.firstName
        socialUser.email = user.email
        socialUser.isEmailVerified = true
        socialUser.password = user.password
        socialUser
      }
      case None => {
        if (!userId.provider.eq(ProviderType.userpass)) {
          var socialUser = new SocialUser
          socialUser.id = userId
          socialUser
        } else {
          null
        }
      }
    }
  }

  def save(user: SocialUser) {
    if (find(user.id) == null) {
      val firstName = user.displayName
      val lastName = user.displayName
      Athlete.create(Athlete(NotAssigned, user.email, user.password, firstName, lastName))
    }
  }

  def createActivation(user: SocialUser): String = {
    val uuid: String = Codec.UUID()
    activations.put(uuid, user)
    uuid
  }

  def activate(uuid: String): Boolean = {
    val user: SocialUser = activations.get(uuid).asInstanceOf[SocialUser]
    var result = false

    if (user != null) {
      user.isEmailVerified = true
      save(user)
      activations.remove(uuid)
      result = true
    }

    result
  }

  def deletePendingActivations() {
    activations.clear()
  }
}
