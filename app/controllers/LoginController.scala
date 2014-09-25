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
import scalikejdbc.SQLInterpolation._
import scalikejdbc._
import controllers._
import play.api.libs.json._

object LoginController extends BaseController {

  def index = Action { implicit request =>
    val hotWords = Word.topN(10)
    Ok(views.html.index(request.flash.get("message"),hotWords))
  }

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
      loginData => {
        if (Member.authorize(loginData.username, loginData.password)) {
          Redirect(routes.Application.index).withSession("username" -> loginData.username)
        }
        else {
          Redirect(routes.LoginController.login).flashing("error" -> Messages("login.validation.incorrent"))
        }
      }
    )
  }
}



