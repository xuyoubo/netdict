package controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import models._

object CommentController extends BaseController {
  case class CommentData(wordId:Int,content:String)

  val CommentForm = Form(
    mapping(
      "wordId" -> number,
      "content" -> nonEmptyText
    )(CommentData.apply)(CommentData.unapply)
  )

  def createComment = AuthenticatedAction { implicit request =>
    CommentForm.bindFromRequest.fold (
      errors => {
        Redirect(routes.WordController.showWord(1))
      },
      commentData => {
        val word = Word.find(commentData.wordId)

        Comment.create(Some(word.get.id),Some(request.session("userid").toInt),Some(commentData.content))
        Redirect(routes.WordController.showWord(commentData.wordId))
      }
    )
  }
}

