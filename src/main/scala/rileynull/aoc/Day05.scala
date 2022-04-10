package rileynull.aoc

import better.files._

import scala.collection.mutable.ArrayBuffer

object Day05 {
  case class Line(x1: Int, y1: Int, x2: Int, y2: Int)

  val pattern = """(\d+),(\d+) -> (\d+),(\d+)""".r
  val lines = Resource.getAsStream("input/day05.txt").lines.filterNot(_.isBlank).map {
    case pattern(x1, y1, x2, y2) => Line(x1.toInt, y1.toInt, x2.toInt, y2.toInt)
    case raw@_ => throw new RuntimeException(s"Parse error. Invalid line <$raw>")
  }.toSeq

  class Board {
    val data = ArrayBuffer(ArrayBuffer[Int]())

    def ensureCoordinate(x: Int, y: Int): Unit = {
      if (x >= data.size) data addAll Iterable.fill(x - data.size + 1)(ArrayBuffer[Int]())
      if (y >= data(x).size) data(x) addAll Iterable.fill(y - data(x).size + 1)(0)
    }

    def fillLine(line: Line): Unit = {
      val xs = if (line.x1 <= line.x2) line.x1 to line.x2 else line.x1 to(line.x2, -1)
      val ys = if (line.y1 <= line.y2) line.y1 to line.y2 else line.y1 to(line.y2, -1)
      val xsFilled = xs.toList prependedAll Iterable.fill(ys.size - xs.size)(xs.end)
      val ysFilled = ys.toList prependedAll Iterable.fill(xs.size - ys.size)(ys.end)

      xsFilled.zip(ysFilled).foreach {
        case (x, y) =>
          ensureCoordinate(x, y)
          data(x)(y) += 1
      }
    }

    def overlapCount = data.map(_.map(tile => if (tile > 1) 1 else 0).sum).sum
  }
}

object Day05SolutionA {
  import Day05._

  def main(args: Array[String]): Unit = {
    val board = new Board
    lines.filter(line => line.x1 == line.x2 || line.y1 == line.y2).foreach(board.fillLine)
    println(s"The overlap count without diagonals is ${board.overlapCount}")
  }
}

object Day05SolutionB {
  import Day05._

  def main(args: Array[String]): Unit = {
    val board = new Board
    lines.foreach(board.fillLine)
    println(s"The overlap count with diagonals is ${board.overlapCount}")
  }
}
