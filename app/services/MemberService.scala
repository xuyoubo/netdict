package services

import models._
import scalikejdbc._

trait MemberService {
  def authorize(name:String,password:String):Option[Member]
  def exists(name:String):Boolean
  def create(name: String, password: Option[String] = None):Member
}

class DefaultMemberService extends MemberService {
  def authorize(name:String,password:String):Option[Member] = {
    val members = Member.findAllBy(sqls.eq(Member.m.name,name).and.eq(Member.m.password,password));
    if(members.isEmpty) {
      None
    }
    else {
      Some(members.head)
    }
  }


  def exists(name:String):Boolean = {
    Member.countBy(sqls.eq(Member.m.name,name)) > 0
  }

  def create(name: String, password: Option[String] = None):Member = {
    Member.create(name,password)
  }
}

