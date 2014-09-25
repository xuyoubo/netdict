package models

import scalikejdbc._

case class Comment(
  id: Int,
  wordId: Option[Int] = None,
  memberId: Option[Int] = None,
  content: Option[String] = None) {

  def save()(implicit session: DBSession = Comment.autoSession): Comment = Comment.save(this)(session)

  def destroy()(implicit session: DBSession = Comment.autoSession): Unit = Comment.destroy(this)(session)

}


object Comment extends SQLSyntaxSupport[Comment] {

  override val tableName = "comment"

  override val columns = Seq("id", "word_id", "member_id", "content")

  def apply(c: SyntaxProvider[Comment])(rs: WrappedResultSet): Comment = apply(c.resultName)(rs)
  def apply(c: ResultName[Comment])(rs: WrappedResultSet): Comment = new Comment(
    id = rs.get(c.id),
    wordId = rs.get(c.wordId),
    memberId = rs.get(c.memberId),
    content = rs.get(c.content)
  )

  val c = Comment.syntax("c")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Comment] = {
    withSQL {
      select.from(Comment as c).where.eq(c.id, id)
    }.map(Comment(c.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Comment] = {
    withSQL(select.from(Comment as c)).map(Comment(c.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(Comment as c)).map(rs => rs.long(1)).single.apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Comment] = {
    withSQL {
      select.from(Comment as c).where.append(sqls"${where}")
    }.map(Comment(c.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(Comment as c).where.append(sqls"${where}")
    }.map(_.long(1)).single.apply().get
  }

  def create(
    wordId: Option[Int] = None,
    memberId: Option[Int] = None,
    content: Option[String] = None)(implicit session: DBSession = autoSession): Comment = {
    val generatedKey = withSQL {
      insert.into(Comment).columns(
        column.wordId,
        column.memberId,
        column.content
      ).values(
        wordId,
        memberId,
        content
      )
    }.updateAndReturnGeneratedKey.apply()

    Comment(
      id = generatedKey.toInt,
      wordId = wordId,
      memberId = memberId,
      content = content)
  }

  def save(entity: Comment)(implicit session: DBSession = autoSession): Comment = {
    withSQL {
      update(Comment).set(
        column.id -> entity.id,
        column.wordId -> entity.wordId,
        column.memberId -> entity.memberId,
        column.content -> entity.content
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Comment)(implicit session: DBSession = autoSession): Unit = {
    withSQL { delete.from(Comment).where.eq(column.id, entity.id) }.update.apply()
  }

}
