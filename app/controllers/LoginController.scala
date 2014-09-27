package controllers

import models._
import filters._
import play.Logger
import play.api._
import play.api.data.Forms._
import play.api.data._
import play.api.i18n._
import play.api.mvc._

import scala.concurrent.Future
import controllers._
import play.api.libs.json._

object LoginController extends BaseController {

  def login = Action { request =>
    val title = Messages("login.title")
    Ok(views.html.login(title, request.flash.get("error")))
  }

  def logout = Action { request =>
    Redirect(routes.Application.index).withNewSession
  }

  case class LoginData(username: String, password: String)

  val LoginForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "password" -> nonEmptyText
    )(LoginData.apply)(LoginData.unapply)
  )

  def doLogin = Action { implicit request => 
    LoginForm.bindFromRequest.fold(
      errors => Redirect(routes.LoginController.login).flashing("error" -> Messages("login.validation.require")),
      loginData => Member.authorize(loginData.username, loginData.password) match {
        case Some(member) =>
          Redirect(routes.Application.index).withSession("username" -> member.name,"userid" -> member.id.toString)
        case None =>
          Redirect(routes.LoginController.login).flashing("error" -> Messages("login.validation.incorrent"))
      }
    )
  }
}



