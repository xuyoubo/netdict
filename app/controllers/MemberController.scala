package controllers

import services._
import filters._
import play.Logger
import play.api._
import play.api.data.Forms._
import play.api.data._
import play.api.i18n._
import play.api.mvc._
import play.api.data.validation._

import scala.concurrent.Future
import scalikejdbc._
import scalikejdbc.SQLInterpolation._
import controllers._
import play.api.libs.json._

case class LoginData(username: String, password: String)

case class RegistData(username: String, password: String,confirm: String)

object MemberController extends BaseController {
  val memberService:MemberService = new DefaultMemberService

  def login = Action { implicit request =>
    val title = Messages("login.title")
    Ok(views.html.login(title, request.flash.get("error")))
  }

  def logout = Action { implicit request =>
    Redirect(routes.Application.index).withNewSession
  }

  val LoginForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "password" -> nonEmptyText
    )(LoginData.apply)(LoginData.unapply)
  )

  def regist = Action { implicit request =>
    Ok(views.html.members.regist(RegistForm.fill(RegistData("","",""))))
  }

  val nameCheckConstraint: Constraint[String] = Constraint("constraints.namecheck")({
    plainText => 
      if (memberService.exists(plainText))
        Invalid(Seq(ValidationError(Messages("regist.validation.duplicate"))))
      else
        Valid
  })

  val RegistForm = Form(
    mapping(
      "username" -> nonEmptyText.verifying(nameCheckConstraint),
      "password" -> nonEmptyText,
      "confirm" -> nonEmptyText
      )(RegistData.apply)(RegistData.unapply) verifying(Messages("regist.validation.confirmError"),fields => fields match {
        case regData => {
          regData.password == regData.confirm
        }
      }) 
  )

  def doRegist = Action { implicit request =>
    RegistForm.bindFromRequest.fold(
      errorForm => {
        Ok(views.html.members.regist(errorForm))
      },
      registData => {
        val member = memberService.create(registData.username,Some(registData.password))
        Redirect(routes.Application.index).withSession("username" -> member.name,"userid" -> member.id.toString).flashing("success" -> Messages("regist.success"))
      }
    )
  }

  def doLogin = Action { implicit request =>
    LoginForm.bindFromRequest.fold(
      errors => Redirect(routes.MemberController.login).flashing("error" -> Messages("login.validation.require")),
      loginData => memberService.authorize(loginData.username, loginData.password) match {
        case Some(member) =>
          Redirect(routes.Application.index).withSession("username" -> member.name,"userid" -> member.id.toString)
        case None =>
          Redirect(routes.MemberController.login).flashing("error" -> Messages("login.validation.incorrent"))
      }
    )
  }
}



