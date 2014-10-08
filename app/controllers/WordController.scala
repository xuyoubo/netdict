package controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import scalikejdbc.SQLInterpolation._
import scalikejdbc._
import play.api.i18n._
import play.api.data.validation._
import play.api.libs.json._
import services._

case class WordData(keyword: String, original:Option[String], trans: Option[String])

object WordController extends BaseController {
  val wordService:WordService = new DefaultWordService

  case class SearchData(keyword: String)
  val SearchForm = Form(
    mapping(
      "keyword" -> nonEmptyText
    )(SearchData.apply)(SearchData.unapply)
  )

  val keywordConstraint: Constraint[String] = Constraint("constraints.keywordConstraint")({
    plainText => {
      if (wordService.exists(plainText)) {
        Invalid(ValidationError(Messages("keyword.duplicate")))
      }
      else {
        Valid
      }
    }
  })

  val WordForm = Form(
    mapping(
      "keyword" -> nonEmptyText.verifying(keywordConstraint),
      "original" -> optional(text),
      "trans" -> optional(text)
    )(WordData.apply)(WordData.unapply)
  )

  def showWord(id:Int) = Action { implicit request =>
    val word = wordService.find(id)
    println(word)
    Ok(views.html.words.show_word(word))
  }

  def newWord = AuthenticatedAction { implicit request =>
    val w = request.getQueryString("w")
    val form = WordForm.fill(WordData(w.getOrElse(""),None,None))
    Ok(views.html.words.new_word(form))
  }

  def searchWord = Action { implicit request =>
    SearchForm.bindFromRequest.fold(
      formWithErrors => {
        Redirect(routes.Application.index)
      },
      searchData => {
        val words = wordService.search(searchData.keyword)
        if(words.size == 1) {
          Ok(views.html.words.show_word(Some(words.head)))
        }
        else {
          Ok(views.html.words.list_words(words, searchData.keyword))
        }
      }
    )
  }

  def createWord = AuthenticatedAction { implicit request =>
    WordForm.bindFromRequest.fold(
      formWithErrors => {
        Ok(views.html.words.new_word(formWithErrors))
      },
      word => {
        if (wordService.exists(word.keyword)) {
          Redirect("/").flashing("message" -> Messages("save.success"))
          Ok(views.html.words.new_word(WordForm))
        }
        else {
          val w = wordService.create(word.keyword, word.original, word.trans)
          Redirect(routes.WordController.showWord(w.id)).flashing("success" -> Messages("save.success"))
        }
      }
    )
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
      id => wordService.find(id) match {
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
