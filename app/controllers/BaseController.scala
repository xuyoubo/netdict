package controllers

import play.api._
import play.api.mvc._
import scala.concurrent.Future

trait BaseController extends Controller {
  object AuthenticatedAction extends ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
      request.session.get("username") match {
        case Some(username) =>
          block(request)
        case None =>
          Future.successful(Redirect(routes.LoginController.login))
      }
    }
  }
}
