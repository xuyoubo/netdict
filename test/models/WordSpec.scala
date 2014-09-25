package models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import org.joda.time._
import scalikejdbc._

class WordSpec extends Specification {

  "Word" should {

    val w = Word.syntax("w")

    "find by primary keys" in new AutoRollback {
      val maybeFound = Word.find(123)
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = Word.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = Word.countAll()
      count should be_>(0L)
    }
    "find by where clauses" in new AutoRollback {
      val results = Word.findAllBy(sqls.eq(w.id, 123))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = Word.countBy(sqls.eq(w.id, 123))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = Word.create(keyword = "MyString")
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = Word.findAll().head
      // TODO modify something
      val modified = entity
      val updated = Word.save(modified)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = Word.findAll().head
      Word.destroy(entity)
      val shouldBeNone = Word.find(123)
      shouldBeNone.isDefined should beFalse
    }
  }

}
        