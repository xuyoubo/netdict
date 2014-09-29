package controllers

import models._
import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._

object Application extends BaseController {

  def index = Action { implicit request =>
    val hotWords = Word.topN(10)
    val totalWords = Word.countAll()
    Ok(views.html.index(totalWords,hotWords))
  }

}



