package controllers

import models._
import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import play.api.libs.json._

object Application extends BaseController {

  def index = Action { implicit request =>
    val hotWords = Word.topN(10)
    val totalWords = Word.countAll()
    Ok(views.html.index(request.flash.get("message"),totalWords,hotWords))
  }

  val favourForm = Form("id" -> number)

  def favourWord = Action { implicit request =>
    favourForm.bindFromRequest.fold (
      errors => Ok(
          JsObject(Seq(
            "status"->JsString("fail"),
            "message"->JsString("no id provide")
          ))
      ),
      id => Word.find(id) match {
        case Some(myWord) => {
          myWord.copy(favourCount = Some(myWord.favourCount.getOrElse(0) + 1)).save

          Ok(JsObject(Seq(
            "status"->JsString("ok"),
            "favourCount"->JsNumber(myWord.favourCount.get)
          )))
        }
        case None =>
          Ok(JsObject(
            Seq("status"->JsString("fail"),
            "message"->JsString("word not found"))
        ))}
    )
  }
}



