package rileynull.aoc

import kotlin.math.absoluteValue

object Day07 {
    fun linearDistance(x1: Int, x2: Int): Int = (x2 - x1).absoluteValue

    fun triangularDistance(x1: Int, x2: Int): Int = linearDistance(x1, x2) * (linearDistance(x1, x2) + 1) / 2

    fun totalDistanceToCenter(positions: List<Int>, distanceMetric: (Int, Int) -> Int): Int =
            (positions.min()!!..positions.max()!!).map { pos ->
                positions.sumBy { distanceMetric(it, pos) }
            }.min()!!
}

fun main() {
    val input = {}.javaClass.getResource("/day07.txt").readText().trim().split(',').map { it.toInt() }
    println("Answer to part A is ${Day07.totalDistanceToCenter(input, Day07::linearDistance)}.")
    println("Answer to part B is ${Day07.totalDistanceToCenter(input, Day07::triangularDistance)}.")
}
