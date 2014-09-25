package models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import org.joda.time._
import scalikejdbc._

class CommentSpec extends Specification {

  "Comment" should {

    val c = Comment.syntax("c")

    "find by primary keys" in new AutoRollback {
      val maybeFound = Comment.find(123)
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = Comment.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = Comment.countAll()
      count should be_>(0L)
    }
    "find by where clauses" in new AutoRollback {
      val results = Comment.findAllBy(sqls.eq(c.id, 123))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = Comment.countBy(sqls.eq(c.id, 123))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = Comment.create()
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = Comment.findAll().head
      // TODO modify something
      val modified = entity
      val updated = Comment.save(modified)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = Comment.findAll().head
      Comment.destroy(entity)
      val shouldBeNone = Comment.find(123)
      shouldBeNone.isDefined should beFalse
    }
  }

}
        