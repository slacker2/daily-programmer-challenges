//http://www.reddit.com/r/dailyprogrammer/comments/3c9a9h/20150706_challenge_222_easy_balancing_words/


object DPG07062015 {

  def main(args: Array[String]) = {
    args.foreach { arg => findBalancePoint(arg) }
  }

  def findBalancePoint(word: String): Boolean = {

    word.zipWithIndex.map { case (c, i) => 

      if (i == (word.length-1)) { 
        println(word + " DOES NOT BALANCE")
        return false
      }

      val leftSide = word.slice(0,i)
      val leftWeight = leftSide.reverse.zipWithIndex.map { case (e, i) => (e-64) * (i+1) }.fold(0)( (f,v) => f + v )
      val rightSide = word.slice(i+1,word.size)
      val rightWeight = rightSide.zipWithIndex.map { case (e, i) => (e-64) * (i+1) }.fold(0)( (f, v) => f + v )

      if (leftWeight == rightWeight) {
        println (leftSide + " " + c + " " + rightSide + " - " + leftWeight)
        return true
      }
    }

    false
  }

}
