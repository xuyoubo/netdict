package models

import scalikejdbc._

case class Word(
  id: Int,
  keyword: String,
  original: Option[String] = None,
  trans: Option[String] = None,
  searchCount: Option[Int] = None,
  favourCount: Option[Int] = None,
  memberId:Option[Int] = None,
  isAudit:Option[Int] = Some(0)
  ) {

  lazy val comments: List[Comment] = Comment.findAllBy(sqls.eq(Comment.c.wordId,this.id))

  lazy val member: Option[Member] = Member.find(memberId.getOrElse(0))

  def save()(implicit session: DBSession = Word.autoSession): Word = Word.save(this)(session)

  def destroy()(implicit session: DBSession = Word.autoSession): Unit = Word.destroy(this)(session)

}


object Word extends SQLSyntaxSupport[Word] {

  override val tableName = "word"

  override val columns = Seq("id", "keyword","original", "trans","member_id", "search_count", "favour_count","is_audit")

  def apply(w: SyntaxProvider[Word])(rs: WrappedResultSet): Word = apply(w.resultName)(rs)
  def apply(w: ResultName[Word])(rs: WrappedResultSet): Word = new Word(
    id = rs.get(w.id),
    keyword = rs.get(w.keyword),
    original = rs.get(w.original),
    trans = rs.get(w.trans),
    memberId = rs.get(w.memberId),
    searchCount = rs.get(w.searchCount),
    favourCount = rs.get(w.favourCount),
    isAudit = rs.get(w.isAudit)
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
    original: Option[String] = None,
    trans: Option[String] = None,
    searchCount: Option[Int] = None,
    favourCount: Option[Int] = None,
    memberId:Option[Int] = None,
    isAudit:Option[Int] = Some(0)
    )(implicit session: DBSession = autoSession): Word = {
    val generatedKey = withSQL {
      insert.into(Word).columns(
        column.keyword,
        column.original,
        column.trans,
        column.searchCount,
        column.favourCount,
        column.memberId,
        column.isAudit
      ).values(
        keyword,
        original,
        trans,
        searchCount,
        favourCount,
        memberId,
        isAudit
      )
    }.updateAndReturnGeneratedKey.apply()

    Word(
      id = generatedKey.toInt,
      keyword = keyword,
      original = original,
      trans = trans,
      searchCount = searchCount,
      favourCount = favourCount,
      memberId = memberId,
      isAudit = isAudit
    )
  }

  def save(entity: Word)(implicit session: DBSession = autoSession): Word = {
    withSQL {
      update(Word).set(
        column.id -> entity.id,
        column.keyword -> entity.keyword,
        column.original -> entity.original,
        column.trans -> entity.trans,
        column.searchCount -> entity.searchCount,
        column.favourCount -> entity.favourCount,
        column.memberId -> entity.memberId,
        column.isAudit -> entity.isAudit
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

}
