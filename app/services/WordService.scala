package services

import models._
import scalikejdbc._

trait WordService {
  def exists(keyword:String):Boolean
  def find(id:Int):Option[Word]
  def search(keyword:String):List[Word]
  def create(keyword: String, memberId:Option[Int], original: Option[String] = None, trans: Option[String] = None):Word
}

class DefaultWordService extends WordService{
  def exists(keyword:String):Boolean = {
    Word.countBy(sqls.eq(Word.w.keyword,keyword)) > 0
  }

  def find(id:Int):Option[Word] = {
    val word = Word.find(id)
    word.foreach { w => w.copy(searchCount = Some(w.searchCount.getOrElse(0) + 1)).save }
    word
  }

  def search(keyword:String):List[Word] = {
    Word.findAllBy(sqls.like(Word.w.keyword,keyword))
  }

  def create(keyword: String, memberId:Option[Int], original:Option[String] = None,trans: Option[String] = None):Word = {
    Word.create(keyword, original, trans,Some(0),Some(0),memberId)
  }
}

