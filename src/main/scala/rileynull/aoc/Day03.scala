package rileynull.aoc

import better.files._

import scala.annotation.tailrec

object Day03 {
  val lines = {
    val raw = Resource.getAsStream("input/day03.txt").lines.filterNot(_.isBlank)
    raw.map(_.split("").map(_.toInt).toIndexedSeq).toSeq
  }

  def packDigits(digits: Seq[Int], radix: Int): Int =
    java.lang.Integer.parseUnsignedInt(digits.mkString, radix)

  def packDigits(bits: Seq[Boolean]): Int =
    packDigits(bits.map(bit => if (bit) 1 else 0), 2)
}

object Day03SolutionA {
  import Day03._

  def main(args: Array[String]): Unit = {
    val popCounts = lines.foldLeft(Seq.empty[Int]) { (acc, curLine) =>
      curLine.zipAll(acc, 0, 0).map {
        case (bit, sum) => bit + sum
      }
    }
    val gammaRate = packDigits(popCounts.map(_ * 2 > lines.size))
    val epsilonRate = packDigits(popCounts.map(_ * 2 < lines.size))

    println(s"The answer to part A is ${gammaRate * epsilonRate}.")
  }
}

object Day03SolutionB {
  import Day03._

  sealed trait SearchMode
  case object CO2Mode extends SearchMode
  case object OxygenMode extends SearchMode

  @tailrec
  def search(lines: Seq[Seq[Int]], mode: SearchMode, index: Int = 0): Seq[Int] = {
    if (lines.size == 1) lines.head
    else {
      // Filter the lines based on only the current column.
      val popCount = lines.map(_(index)).sum
      val requiredBit =
        if (popCount * 2 == lines.size) mode == OxygenMode
        else if (popCount * 2 < lines.size) mode != OxygenMode
        else mode == OxygenMode
      search(lines.filter(_(index) == 1 == requiredBit), mode, index + 1)
    }
  }

  def main(args: Array[String]): Unit = {
    val co2Rating = packDigits(search(lines, CO2Mode), 2)
    val oxygenRating = packDigits(search(lines, OxygenMode), 2)

    println(s"The answer to part B is ${co2Rating * oxygenRating}.")
  }
}
