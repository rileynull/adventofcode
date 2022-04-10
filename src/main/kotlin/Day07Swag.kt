package rileynull.aoc

import kotlin.math.absoluteValue

object Day07Swag {
    fun quickSelect(list: MutableList<Int>, k: Int, left: Int = 0, right: Int = list.size - 1): Int {
        if (left == right) return list[left]

        var dstIndex = left
        for (i in left..right) {
            if (list[i] < list[right] || i == right) {
                val tmp = list[dstIndex]
                list[dstIndex] = list[i]
                list[i] = tmp
                dstIndex++
            }
        }

        return when {
            (k < dstIndex - 1) -> quickSelect(list, k, left, dstIndex - 2)
            (k >= dstIndex) -> quickSelect(list, k, dstIndex, right)
            else -> list[k]
        }
    }

    fun linearDistance(x1: Int, x2: Int): Int = (x2 - x1).absoluteValue
    fun triangularDistance(x1: Int, x2: Int): Int = linearDistance(x1, x2) * (linearDistance(x1, x2) + 1) / 2
}

fun main() {
    val input = {}.javaClass.getResource("/day07.txt").readText().trim().split(',').map { it.toInt() }

    val median = Day07Swag.quickSelect(input.toMutableList(), input.size / 2)
    println("Answer to part A is ${input.sumBy { Day07Swag.linearDistance(it, median) }}.")

    // There are other terms besides just the sum of squares but they don't affect the final answer for my input.
    val mean = input.sum() / input.size
    println("Answer to part B is ${input.sumBy { Day07Swag.triangularDistance(it, mean) }}.")
}
