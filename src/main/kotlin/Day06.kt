package rileynull.aoc

import java.math.BigInteger

object Day06 {
    fun getLanternfishCount(initial: List<Int>, days: Int): BigInteger {
        val counts = MutableList(9) { _ -> BigInteger.ZERO }
        initial.map { counts[it]++ }
        repeat(days) {
            val zeroes = counts.removeAt(0)
            counts[6] += zeroes
            counts.add(zeroes)
        }
        return counts.reduce { a, b -> a.plus(b) }
    }
}

fun main() {
    val input = {}.javaClass.getResource("/day06.txt").readText().trim().split(',').map { it.toInt() }
    println("Answer to part A is ${Day06.getLanternfishCount(input, 80)}.")
    println("Answer to part B is ${Day06.getLanternfishCount(input, 256)}.")
    println("Answer to a million is e${Day06.getLanternfishCount(input, 1000000).toString(10).length - 1}.")
}
