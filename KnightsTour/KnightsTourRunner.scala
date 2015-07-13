
import java.util.LinkedList
import java.util.concurrent.ConcurrentLinkedQueue
import scala.collection.mutable.{Buffer, Map, Stack}
import scala.collection.JavaConversions._


/**
 * Knights Tour Solution
 *
 * To run an example, simply compile: 
 *
 *   > scalac KnightsTourRunner.scala
 *
 * and then execute:
 *
 *   > scala KnightsTourRunner 5
 *
 * This is my solution to the Knight's Tour problem in Scala. Intended usage is  
 * to create a KnightsTourRunner object with size N, representing the NxN board. 
 * The KnightsTourRunner object maintains a state of branches that have already 
 * been toured, to avoid recomputing tours that have already been found. 
 * Example usage is seen in the Object companion's main method below. 
 * The x coordinates grow to the right of the board, and y coordinates grow 
 * down the board.
 *
 * I put all of the necessary classes in one file for the sake of making it 
 * simpler to review.
 *
 * I have not yet made this implementation run concurrently.
 */


// Utility case class for representing a position on the board
case class Position(x: Int, y: Int)
// Utility case class for representing a complete Knight's Tour
case class KnightsTour(closed: Boolean, path: List[Position])

object KnightsTourRunner {

  def main(args: Array[String]) = {

    val n = args(0).toInt
    if (n < 5) { throw new IllegalArgumentException("Argument must be an integer greater than 4.") }
    val k = new KnightsTourRunner(n)

    // find a single tour that begins at (0, 0)
    val aTour = k.findOneTourFor(0,0)
    println(aTour)
    println(aTour.get.path.size)
   
    // find the same tour again (will complete quickly, since it was already computed)
    val sameTour = k.findOneTourFor(0,0)
    println(sameTour)
    println(sameTour.get.path.size)
   
    // Demonstrating that this is the same tour that has been computed
    println(aTour.get == sameTour.get)

    // Print all possible tours that begin at this position
    val allTours = k.findAllToursFor(0,0)
    allTours.foreach { t =>
      println(t)
    }

  }

}

class KnightsTourRunner(n: Int) {

  lazy val fullTourLength = n * n // convenience variable
  //lazy val cores = Runtime.getRuntime().availableProcessors() // to be used to determine number of threads to create for concurrent exploration
  private lazy val closedTours = Buffer[KnightsTour]() // keeps track of all the closed tours for convenience
  private lazy val trees = Map[Position, KnightsTourTree]() // maintains the state of tours that have already been discovered


  def findOneTourFor(x: Int, y: Int): Option[KnightsTour] = {
    val pos = Position(x, y)
    if (!isValidPosition(pos)) { throw new IllegalArgumentException(s"Position must be between 0 and $n.") }
    // TODO: rotate positions until start = pos, and return it
    //closedTours.headOption.map { closedTour => return Some(closedTour) } 
    val tourTree = trees.getOrElseUpdate(pos, new KnightsTourTree(pos))
    tourTree.tourUntilOneFound()
  }

  def findAllToursFor(x: Int, y: Int): List[KnightsTour] = {
    val pos = Position(x, y)
    if (!isValidPosition(pos)) { throw new IllegalArgumentException(s"Position must be between 0 and $n.") }
    val tourTree = trees.getOrElseUpdate(pos, new KnightsTourTree(pos))
    tourTree.tourAllBranches()
  }

  def findAllClosedTours(): List[KnightsTour] = {
    (0 until n).foreach { row =>
      (0 until n).foreach { col =>
        findAllToursFor(row, col)
      }
    }
    closedTours.toList
  }

  def validMovesForPosition(pos: Position): List[Position] = possibleMovesForPosition(pos).filter { p => isValidPosition(p) }

  def possibleMovesForPosition(pos: Position): List[Position] = {
    List( Position( pos.x - 1, pos.y - 2),  // upLeft
          Position( pos.x + 1, pos.y - 2),  // upRight
          Position( pos.x - 2, pos.y - 1),  // leftUp
          Position( pos.x - 2, pos.y + 1),  // leftDown
          Position( pos.x - 1, pos.y + 2),  // downLeft
          Position( pos.x + 1, pos.y + 2),  // downRight
          Position( pos.x + 2, pos.y + 1),  // rightDown
          Position( pos.x + 2, pos.y - 1)   // rightUp
        )
  }

  def isValidPosition(pos: Position): Boolean = (pos.x >= 0 && pos.x < n && pos.y >= 0 && pos.y < n)


  /**
   *  ===== Utility classes =====
   */

  // utility class to keep track of unexplored paths
  private case class KnightsTourLeaf(start: Int, nextPosition: Position)

  // utility class to represent a state of a partially explored path
  private class KnightsTourBranch(val path: LinkedList[Position], visited: Map[Position, Boolean], leaves: Stack[KnightsTourLeaf]) {

    def addLeavesForCurrentPosition() = {
      validMovesForPosition(path.last).filter( pos => !(visited.getOrElse(pos, false)) ).foreach { pos =>
        leaves.push(KnightsTourLeaf((path.size), pos))
      }
    }

    def visitNextLeaf() = {
      val nextTourLeaf = leaves.pop()
      // Back track if we've reached a dead end
      while (path.size > nextTourLeaf.start) {
        val last = path.removeLast()
        visited.remove(last)
      }
      path.append(nextTourLeaf.nextPosition)
      visited.put(nextTourLeaf.nextPosition, true)
      addLeavesForCurrentPosition()
    }

    def completedATour(): Boolean = { path.size == fullTourLength }
    def completedAClosedTour(): Boolean = { path.size == fullTourLength && validMovesForPosition(path.last).contains(path(0)) }
    def hasLeavesToExplore(): Boolean = { leaves.size != 0 }
  }

  // utility class to represent state of paths and tours for a given position
  private class KnightsTourTree(pos: Position) {

    val completedTours = Buffer[KnightsTour]()

    // create multiple branches from possible exploration paths (when run concurrently, each thread could take their own branch)
    private val initialTourBranches = validMovesForPosition(pos).map { p => 
      val path = new LinkedList[Position](List(pos))
      val visited = Map(pos -> true)
      val leaves = Stack[KnightsTourLeaf]()
      leaves.push(KnightsTourLeaf(1, p))
      new KnightsTourBranch(path, visited, leaves)
    }
    private val unexploredTourBranches = new ConcurrentLinkedQueue(initialTourBranches)


    // explore branches until we find a complete tour, return None if no possible tours
    def tourUntilOneFound(): Option[KnightsTour] = {
      while (!hasAnyCompletedTours()) {
        val currTourBranch = unexploredTourBranches.poll()
        if (currTourBranch == null) { return None }
        while (currTourBranch.hasLeavesToExplore()) {
          currTourBranch.visitNextLeaf()
          if (currTourBranch.completedATour()) {
            addToCompletedTours(currTourBranch)
            unexploredTourBranches.add(currTourBranch)
            return completedTours.headOption
          } 
        }
      }
      completedTours.headOption
    }

    // exhaustively explore all branches
    def tourAllBranches() = {
      while (unexploredTourBranches.size > 0) {
        val currTourBranch = unexploredTourBranches.poll()
        while (currTourBranch.hasLeavesToExplore()) {
          currTourBranch.visitNextLeaf()
          if (currTourBranch.completedATour()) {
            addToCompletedTours(currTourBranch)
          } 
        }
      }
      completedTours.toList
    }

    def hasAnyCompletedTours(): Boolean = { completedTours.size != 0 }

    private def addToCompletedTours(branch: KnightsTourBranch) = {
       branch.completedAClosedTour() match {
        case true => {
          val newTour = KnightsTour(true, branch.path.toList)
          completedTours.append(newTour)
          closedTours.append(newTour)
        }
        case false => completedTours.append(KnightsTour(false, branch.path.toList))
      }
    }
  }

}

