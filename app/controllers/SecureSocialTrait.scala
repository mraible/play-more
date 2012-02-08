package controllers

import play._
import play.mvc._

import controllers.securesocial.SecureSocial

trait SecureSocialTrait {
    self: Controller =>

    @Before def checkAccess = {
        SecureSocial.DeadboltHelper.beforeRoleCheck()
    }

    def currentUser = {
        SecureSocial.getCurrentUser();
    }
}