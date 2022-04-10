package rileynull.aoc

import better.files._

import scala.annotation.tailrec
import scala.collection.mutable

object Day04 {
  case class Tile(number: Int, marked: Boolean)
  class Board(state: IndexedSeq[mutable.IndexedSeq[Tile]]) {
    def this(boardData: Seq[Seq[Int]]) =
      this(boardData.map(_.map(Tile(_, false)).to(mutable.IndexedSeq)).toIndexedSeq)

    def mark(numberToMark: Int): Boolean = {
      state.foreach(_.mapInPlace {
        case t@Tile(curNumber, marked) =>
          t.copy(marked = marked || curNumber == numberToMark)
      })
      hasVictory
    }

    def hasVictory: Boolean = {
      val rowVictory = state
        .map(row => row.forall(_.marked))
        .reduce(_ || _)
      val columnVictory = (0 until 5)
        .map(columnIndex => state.map(_(columnIndex)))
        .map(column => column.forall(_.marked))
        .reduce(_ || _)

      rowVictory || columnVictory
    }

    def getUnmarkedSum: Int = state.map(_.view.filterNot(_.marked).map(_.number).sum).sum
  }

  val lines = Resource.getAsStream("input/day04-auti.txt").lines.filterNot(_.isBlank).toSeq
  val moves = lines.head.split(',').map(_.toInt).toList
  def boards = lines.tail.map(_.trim.split("""\s+""").map(_.toInt).toSeq)
    .grouped(5).map(new Board(_)).toSeq
}

object Day04SolutionA {
  import Day04._

  @tailrec
  def findFirstWinningScore(boards: Seq[Board], moves: List[Int]): Int = {
    moves match {
      case curMove :: remainingMoves =>
        boards.collect {
          case board if board.mark(curMove) =>
            board.getUnmarkedSum
        }.ensuring(_.size <= 1).headOption match {
          case Some(unmarkedSum) =>
            unmarkedSum * curMove
          case None =>
            findFirstWinningScore(boards, remainingMoves)
        }
      case Nil => ???
    }
  }

  def main(args: Array[String]): Unit = {
    val winningScore = findFirstWinningScore(boards, moves)
    println(s"The first winning score was $winningScore.")
  }
}

object Day04SolutionB {
  import Day04._

  @tailrec
  def findLastWinningScore(boards: Seq[Board], moves: List[Int]): Int = {
    moves match {
      case curMove :: remainingMoves =>
        boards.foreach(_.mark(curMove))

        if (boards.size > 1)
          findLastWinningScore(boards.filterNot(_.hasVictory), remainingMoves)
        else if (boards.head.hasVictory)
          boards.head.getUnmarkedSum * curMove
        else
          findLastWinningScore(boards, remainingMoves)
      case Nil => ???
    }
  }

  def main(args: Array[String]): Unit = {
    val winningScore = findLastWinningScore(boards, moves)
    println(s"The last winning score was $winningScore.")
  }
}
