package models

import scalikejdbc._

case class Member(
  id: Int,
  name: String,
  password: Option[String] = None) {

  def save()(implicit session: DBSession = Member.autoSession): Member = Member.save(this)(session)

  def destroy()(implicit session: DBSession = Member.autoSession): Unit = Member.destroy(this)(session)

}


object Member extends SQLSyntaxSupport[Member] {

  override val tableName = "member"

  override val columns = Seq("id", "name", "password")

  def apply(m: SyntaxProvider[Member])(rs: WrappedResultSet): Member = apply(m.resultName)(rs)
  def apply(m: ResultName[Member])(rs: WrappedResultSet): Member = new Member(
    id = rs.get(m.id),
    name = rs.get(m.name),
    password = rs.get(m.password)
  )

  val m = Member.syntax("m")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Member] = {
    withSQL {
      select.from(Member as m).where.eq(m.id, id)
    }.map(Member(m.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Member] = {
    withSQL(select.from(Member as m)).map(Member(m.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(Member as m)).map(rs => rs.long(1)).single.apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Member] = {
    withSQL {
      select.from(Member as m).where.append(sqls"${where}")
    }.map(Member(m.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(Member as m).where.append(sqls"${where}")
    }.map(_.long(1)).single.apply().get
  }

  def create(
    name: String,
    password: Option[String] = None)(implicit session: DBSession = autoSession): Member = {
    val generatedKey = withSQL {
      insert.into(Member).columns(
        column.name,
        column.password
      ).values(
        name,
        password
      )
    }.updateAndReturnGeneratedKey.apply()

    Member(
      id = generatedKey.toInt,
      name = name,
      password = password)
  }

  def save(entity: Member)(implicit session: DBSession = autoSession): Member = {
    withSQL {
      update(Member).set(
        column.id -> entity.id,
        column.name -> entity.name,
        column.password -> entity.password
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Member)(implicit session: DBSession = autoSession): Unit = {
    withSQL { delete.from(Member).where.eq(column.id, entity.id) }.update.apply()
  }

  def authorize(name:String,password:String):Option[Member] = {
    val members = Member.findAllBy(sqls.eq(m.name,name).and.eq(m.password,password));
    if(members.isEmpty) {
      None
    }
    else {
      Some(members.head)
    }
  }
}
