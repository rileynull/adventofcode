package rileynull.aoc

import better.files._
import scala.util.parsing.combinator._

sealed trait Command
case class Forward(distance: Int) extends Command
case class Down(distance: Int) extends Command
case class Up(distance: Int) extends Command

object Day02Parser extends RegexParsers {
  private val forward = "forward" ~ """\d+""".r ^^ {
    case _ ~ distance => Forward(distance.toInt)
  }
  private val down = "down" ~ """\d+""".r ^^ {
    case _ ~ distance => Down(distance.toInt)
  }
  private val up = "up" ~ """\d+""".r ^^ {
    case _ ~ distance => Up(distance.toInt)
  }
  private val command = forward | down | up

  def parseCommand(in: CharSequence) = parseAll(command, in)
}

object Day02SolutionA {
  case class SubmarinePosition(horizontal: Int, vertical: Int)

  def main(args: Array[String]): Unit = {
    val lines = Resource.getAsStream("input/day02.txt").lines.filterNot(_.isBlank)
    val commands = lines.map(Day02Parser.parseCommand(_).get)

    val position = commands.foldLeft(SubmarinePosition(0, 0)) { (acc, cur) =>
      cur match {
        case Forward(distance) => acc.copy(horizontal = acc.horizontal + distance)
        case Down(distance) => acc.copy(vertical = acc.vertical + distance)
        case Up(distance) => acc.copy(vertical = acc.vertical - distance)
      }
    }

    println(s"Part A answer is ${position.vertical * position.horizontal}.")
  }
}

object Day02SolutionB {
  case class SubmarineState(horizontal: Int, vertical: Int, aim: Int)

  def main(args: Array[String]): Unit = {
    val lines = Resource.getAsStream("input/day02.txt").lines.filterNot(_.isBlank)
    val commands = lines.map(Day02Parser.parseCommand(_).get)

    val state = commands.foldLeft(SubmarineState(0, 0, 0)) { (acc, cur) =>
      cur match {
        case Forward(distance) => acc.copy(
          horizontal = acc.horizontal + distance,
          vertical = acc.vertical + acc.aim * distance
        )
        case Down(distance) => acc.copy(aim = acc.aim + distance)
        case Up(distance) => acc.copy(aim = acc.aim - distance)
      }
    }

    println(s"Part B answer is ${state.vertical * state.horizontal}.")
  }
}
