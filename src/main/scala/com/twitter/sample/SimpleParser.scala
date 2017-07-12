package com.twitter.sample

/**
  * Created by mac on 2017. 7. 10..
  */

case class SimpleParsed(id: Long, text: String)

class SimpleParser {
  val tweetRegex = "\"id: (.*),\"text\":\"(.*)\"".r

  def parse(str: String) = {
    tweetRegex.findFirstMatchIn(str) match {
      case Some(m) => {
        val id = str.substring(m.start(1), m.end(1)).toInt
        val text = str.substring(m.start(2), m.end(2))
        Some(SimpleParsed(id, text))
      }
      case _ => None
    }
  }
}
