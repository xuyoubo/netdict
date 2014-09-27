package controllers

import play.api._
import play.api.mvc._
import models._
import play.api.data.Forms._
import play.api.data._
import scalikejdbc.SQLInterpolation._
import scalikejdbc._
import play.api.i18n._
import play.api.data.validation._

case class WordData(keyword: String, trans: Option[String])

object WordController extends BaseController {

  case class SearchData(keyword: String)
  val SearchForm = Form(
    mapping(
      "keyword" -> nonEmptyText
    )(SearchData.apply)(SearchData.unapply)
  )

  val keywordConstraint: Constraint[String] = Constraint("constraints.keywordConstraint")({
    plainText => {
      if (Word.countBy(sqls.eq(Word.w.keyword,plainText)) > 0) {
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
      "trans" -> optional(text)
    )(WordData.apply)(WordData.unapply)
  )

  def showWord(keyword:String) = Action { implicit request =>
    val word = Word.findByKeyword(keyword)
    Ok(views.html.words.show_word(word))
  }

  def showWord(id:Int) = Action { implicit request =>
    val word = Word.find(id)
    Ok(views.html.words.show_word(word))
  }

  def newWord = AuthenticatedAction { implicit request =>
    val w = request.getQueryString("w")
    val form = WordForm.fill(WordData(w.getOrElse(""),None))
    Ok(views.html.words.new_word(form))
  }

  def searchWord = Action { implicit request =>
    SearchForm.bindFromRequest.fold(
      formWithErrors => {
        Redirect(routes.Application.index)
      },
      searchData => {
        val words = Word.findAllBy(sqls.eq(Word.w.keyword,searchData.keyword))
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
        if (Word.countBy(sqls.eq(Word.w.keyword,word.keyword)) > 0) {
          Redirect("/").flashing("message" -> Messages("save.success"))
          Ok(views.html.words.new_word(WordForm))
        }
        else {
          val w = Word.create(word.keyword, word.trans,Some(0),Some(0))
          Redirect(routes.WordController.showWord(w.id)).flashing("success" -> Messages("save.success"))
        }
      }
    )
  }

}
