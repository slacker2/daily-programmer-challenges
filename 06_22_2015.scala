// http://www.reddit.com/r/dailyprogrammer/comments/3aqvjn/20150622_challenge_220_easy_mangling_sentences/

object DPG06222015 {

  def main(args: Array[String]) = {
    println(mangle(args(0)))
  }

  def mangle(sentence: String): String = {

    sentence.split(' ').map { word =>
      val sortedAlphaNumChars = word.toLowerCase.toCharArray.filter(_.isLetterOrDigit).sorted
      var sortedWord = ""
      var i = 0

      word.toCharArray.foreach { char =>
        char match {
          case capitalLetter if char.isUpper => sortedWord += sortedAlphaNumChars(i).toUpper; i += 1
          case normalLetter if char.isLower | char.isDigit => sortedWord += sortedAlphaNumChars(i); i += 1
          case _ => sortedWord += char
        }
      }
      sortedWord
    }.mkString(" ")
  }

}
