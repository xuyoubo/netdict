package models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import org.joda.time._
import scalikejdbc._

class WordsSpec extends Specification {

  "Words" should {

    val w = Words.syntax("w")

    "find by primary keys" in new AutoRollback {
      val maybeFound = Words.find(123)
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = Words.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = Words.countAll()
      count should be_>(0L)
    }
    "find by where clauses" in new AutoRollback {
      val results = Words.findAllBy(sqls.eq(w.id, 123))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = Words.countBy(sqls.eq(w.id, 123))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = Words.create(keyword = "MyString")
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = Words.findAll().head
      // TODO modify something
      val modified = entity
      val updated = Words.save(modified)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = Words.findAll().head
      Words.destroy(entity)
      val shouldBeNone = Words.find(123)
      shouldBeNone.isDefined should beFalse
    }
  }

}
        