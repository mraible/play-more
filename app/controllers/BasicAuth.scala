package controllers

import _root_.securesocial.provider.{UserService, ProviderType, UserId}
import play._
import play.mvc._
import play.libs.Crypto

import controllers.securesocial.SecureSocial

/*
 * @author Jorge Aliss <jaliss@gmail.com> of Secure Social fame.
 */
trait BasicAuth {
  self: Controller =>

  @Before def checkAccess = {
    if (currentUser != null) {
      // this allows SecureSocial.getCurrentUser() to work.
      renderArgs.put("user", currentUser)
      Continue
    }

    val realm =
      Play.configuration.getProperty("securesocial.basicAuth.realm",
        "Unauthorized")

    if (request.user == null || request.password == null) {
      Unauthorized(realm)
    } else {
      val userId = new UserId
      userId.id = request.user
      userId.provider = ProviderType.userpass
      val user = UserService.find(userId)

      if (user == null ||
        !Crypto.passwordHash(request.password).equals(user.password)) {
        Unauthorized(realm)
      } else {
        // this allows SecureSocial.getCurrentUser() to work.
        renderArgs.put("user", user)
        Continue
      }
    }
  }

  def currentUser = {
    SecureSocial.getCurrentUser()
  }
}