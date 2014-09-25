package filters
import play.api._
import play.api.mvc._
import scala.concurrent.Future
import play.api.mvc.Result._

object AuthorizedFilter {
  def apply(actionNames: String*) = new AuthorizedFilter(actionNames)
}

class AuthorizedFilter(actionNames: Seq[String]) extends Filter {
  def apply(next: (RequestHeader) => Future[Result])(request: RequestHeader): Future[Result] = {
    if(authorizationRequired(request)) {
      next(request)
    }
    else next(request)
  }

  private def authorizationRequired(request: RequestHeader) = {
    val actionInvoked: String = request.tags.getOrElse(play.api.Routes.ROUTE_ACTION_METHOD, "")
    actionNames.contains(actionInvoked)
  }
}

