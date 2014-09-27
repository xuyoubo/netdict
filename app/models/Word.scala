package models

import scalikejdbc._

case class Word(
  id: Int,
  keyword: String,
  trans: Option[String] = None,
  searchCount: Option[Int] = None,
  favourCount: Option[Int] = None
  ) {

  lazy val comments: List[Comment] = Comment.findAllBy(sqls.eq(Comment.c.wordId,this.id))

  def save()(implicit session: DBSession = Word.autoSession): Word = Word.save(this)(session)

  def destroy()(implicit session: DBSession = Word.autoSession): Unit = Word.destroy(this)(session)

}


object Word extends SQLSyntaxSupport[Word] {

  override val tableName = "word"

  override val columns = Seq("id", "keyword", "trans", "search_count", "favour_count")

  def apply(w: SyntaxProvider[Word])(rs: WrappedResultSet): Word = apply(w.resultName)(rs)
  def apply(w: ResultName[Word])(rs: WrappedResultSet): Word = new Word(
    id = rs.get(w.id),
    keyword = rs.get(w.keyword),
    trans = rs.get(w.trans),
    searchCount = rs.get(w.searchCount),
    favourCount = rs.get(w.favourCount)
  )

  val w = Word.syntax("w")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Word] = {
    withSQL {
      select.from(Word as w).where.eq(w.id, id)
    }.map(Word(w.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Word] = {
    withSQL(select.from(Word as w)).map(Word(w.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(Word as w)).map(rs => rs.long(1)).single.apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Word] = {
    withSQL {
      select.from(Word as w).where.append(sqls"${where}")
    }.map(Word(w.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(Word as w).where.append(sqls"${where}")
    }.map(_.long(1)).single.apply().get
  }

  def create(
    keyword: String,
    trans: Option[String] = None,
    searchCount: Option[Int] = None,
    favourCount: Option[Int] = None)(implicit session: DBSession = autoSession): Word = {
    val generatedKey = withSQL {
      insert.into(Word).columns(
        column.keyword,
        column.trans,
        column.searchCount,
        column.favourCount
      ).values(
        keyword,
        trans,
        searchCount,
        favourCount
      )
    }.updateAndReturnGeneratedKey.apply()

    Word(
      id = generatedKey.toInt,
      keyword = keyword,
      trans = trans,
      searchCount = searchCount,
      favourCount = favourCount)
  }

  def save(entity: Word)(implicit session: DBSession = autoSession): Word = {
    withSQL {
      update(Word).set(
        column.id -> entity.id,
        column.keyword -> entity.keyword,
        column.trans -> entity.trans,
        column.searchCount -> entity.searchCount,
        column.favourCount -> entity.favourCount
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Word)(implicit session: DBSession = autoSession): Unit = {
    withSQL { delete.from(Word).where.eq(column.id, entity.id) }.update.apply()
  }

  def topN(n:Int)(implicit session: DBSession = AutoSession): List[Word] = {
    withSQL {
      select.from(Word as w).orderBy(w.searchCount).desc.limit(n).offset(0)
    }.map(Word(w.resultName)).list.apply()
  }

  def findByKeyword(text: String)(implicit session: DBSession = AutoSession): Option[Word] = {
    val word = withSQL {
      select.from(Word as w).where.eq(w.keyword, text)
    }.map(Word(w.resultName)).first.apply()

    if(word.isDefined) {
      updateSearchCount(word.get)
    }
    word
  }

  def updateSearchCount(word:Word)(implicit session: DBSession = AutoSession) = {
    withSQL {
      update(Word).
        set(Word.column.searchCount -> (word.searchCount.getOrElse(0) + 1)).
        where.eq(Word.column.id,word.id)
    }.update.apply()
  }

}
