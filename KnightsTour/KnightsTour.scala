//package knightstour

import java.util.LinkedList
import java.util.concurrent.ConcurrentLinkedQueue
import scala.collection.mutable.{Buffer, Map, Stack}
import scala.collection.JavaConversions._


case class Position(x: Int, y: Int)
case class Tour(closed: Boolean, path: List[Position])

object KnightsTour {

  def main(args: Array[String]) = {

    val n = args(0).toInt

    if (n < 5) { throw new IllegalArgumentException("Argument must be an integer greater than 4.") }

    val k = new KnightsTour(n)
    
    val t = k.findOneTourFor(0,0)
    println(t)
    println(t.get.path.size)
    
    val tr = k.findOneTourFor(0,0)
    println(tr)
    println(tr.get.path.size)
    
    println(t.get == tr.get)

  }

}

class KnightsTour(n: Int) {

  lazy val fullTourLength = n * n
  lazy val cores = Runtime.getRuntime().availableProcessors()
  private lazy val closedTours = Buffer[Tour]()
  private lazy val trees = Map[Position, TourTree]()


  def findOneTourFor(x: Int, y: Int): Option[Tour] = {

    val pos = Position(x, y)
    if (!isValidPosition(pos)) { throw new IllegalArgumentException(s"Position must be between 0 and $n.") }

    // TODO: rotate positions until start = pos, and return it
    closedTours.headOption.map { closedTour => return Some(closedTour) } 

    val tourTree = trees.getOrElseUpdate(pos, new TourTree(pos))
    
    while (!tourTree.hasAnyCompletedTours()) {
      val currTourBranch = tourTree.getBranch()

      if (currTourBranch == null) { return None }

      while (currTourBranch.hasLeavesToExplore()) {
        tour(tourTree, currTourBranch)
        if (tourTree.hasAnyCompletedTours()) { 
          tourTree.unexploredTourBranches.add(currTourBranch)
          return tourTree.completedTours.headOption
        }
      }
    }

    tourTree.completedTours.headOption
  }

  def findAllToursFor(x: Int, y: Int): List[Tour] = {

    val pos = Position(x, y)
    if (!isValidPosition(pos)) { throw new IllegalArgumentException(s"Position must be between 0 and $n.") }

    val tourTree = trees.getOrElseUpdate(pos, new TourTree(pos))
    
    while (tourTree.unexploredTourBranches.size != 0) {
      val currTourBranch = tourTree.getBranch()
      while (currTourBranch.hasLeavesToExplore()) {
        tour(tourTree, currTourBranch)
      }
    }

    tourTree.completedTours.toList
  }

  def findAllClosedTours(): List[Tour] = {
    (0 until n).foreach { row =>
      (0 until n).foreach { col =>
        findAllToursFor(row, col)
      }
    }
    closedTours.toList
  }

  def tour(tree: TourTree, branch: TourBranch) = {

    branch.visitNextLeaf()

    if (branch.completedATour()) {
      tree.addToCompletedTours(branch)
    } 

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
  private case class TourLeaf(start: Int, nextPosition: Position)

  class TourBranch(val path: LinkedList[Position], visited: Map[Position, Boolean], leaves: Stack[TourLeaf]) {

    def addLeavesForCurrentPosition() = {
      validMovesForPosition(path.last).filter( pos => !(visited.getOrElse(pos, false)) ).foreach { pos =>
        leaves.push(TourLeaf((path.size), pos))
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


  class TourTree(pos: Position) {

    val completedTours = Buffer[Tour]()

    private val initialTourBranches = validMovesForPosition(pos).map { p => 
      val path = new LinkedList[Position](List(pos))
      val visited = Map(pos -> true)
      val leaves = Stack[TourLeaf]() 
      leaves.push(TourLeaf(1, p))
      new TourBranch(path, visited, leaves)
    }
    val unexploredTourBranches = new ConcurrentLinkedQueue(initialTourBranches)

    def hasAnyCompletedTours(): Boolean = { completedTours.size != 0 }
    def getBranch() = { unexploredTourBranches.poll() }
    def addToCompletedTours(branch: TourBranch) = {
       branch.completedAClosedTour() match {
        case true => {
          val newTour = Tour(true, branch.path.toList)
          completedTours.append(newTour)
          closedTours.append(newTour)
        }
        case false => completedTours.append(Tour(false, branch.path.toList))
      }
    }
  }

}

