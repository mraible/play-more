package controllers

import play._
import play.mvc._

import controllers.securesocial.SecureSocial

/*
 * @author Jorge Aliss <jaliss@gmail.com> of Secure Social fame.
 */
trait Secure {
    self: Controller =>

    @Before def checkAccess = {
        SecureSocial.DeadboltHelper.beforeRoleCheck()
    }

    def currentUser = {
        SecureSocial.getCurrentUser();
    }
}