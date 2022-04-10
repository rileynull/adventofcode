package rileynull.aoc

// Note that this version doesn't actually work. I didn't realize until after writing it that there are multiple
// perfect matchings for the graph with the edges that I've set up. In other words, it seems that you can't just say
// "okay, segment X appears in encoded digits of certain lengths, so X could decode to any of the segments that appear
// in every digit with one of those lengths." With that setup alone there are too many degrees of freedom for a unique
// solution, and the algorithm will produce other valid matchings besides the one we want.

// Out of frustration I just randomized the algorithm and wrapped it in an infinite loop until it spits out an answer
// that works. It produces the final result in a few seconds. That's Day08R.kt

object Day08 {
    data class Line(val inputs: List<Set<Segment>>, val outputs: List<Set<Segment>>)

    enum class Segment {
        A, B, C, D, E, F, G
    }

    object SevenSegmentDisplay {
        val digits = mapOf(
                Pair(0, setOf(Segment.A, Segment.B, Segment.C, Segment.E, Segment.F, Segment.G)),
                Pair(1, setOf(Segment.C, Segment.F)),
                Pair(2, setOf(Segment.A, Segment.C, Segment.D, Segment.E, Segment.G)),
                Pair(3, setOf(Segment.A, Segment.C, Segment.D, Segment.F, Segment.G)),
                Pair(4, setOf(Segment.B, Segment.C, Segment.D, Segment.F)),
                Pair(5, setOf(Segment.A, Segment.B, Segment.D, Segment.F, Segment.G)),
                Pair(6, setOf(Segment.A, Segment.B, Segment.D, Segment.E, Segment.F, Segment.G)),
                Pair(7, setOf(Segment.A, Segment.C, Segment.F)),
                Pair(8, setOf(Segment.A, Segment.B, Segment.C, Segment.D, Segment.E, Segment.F, Segment.G)),
                Pair(9, setOf(Segment.A, Segment.B, Segment.C, Segment.D, Segment.F, Segment.G))
        )

        fun decodeSegments(segments: Set<Segment>): Int {
            for (result in digits.filterValues { it == segments })
                return result.key
            error("No decoding for ${segments.joinToString("")}.")
        }
    }

    /**
     * Implements an undirected bipartite graph. The firsts and seconds of `edges` should be disjoint.
     */
    @ExperimentalStdlibApi
    class BipartiteGraph<T>(val edges: List<Pair<T, T>>) {
        // Adjacency lists from each U to its adjacent vertices in V in the input graph.
        private val forwardMapping = edges.groupBy({ it.first }, { it.second })

        /**
         * Finds a matching using a vaguely Ford–Fulkerson type algorithm.
         *
         * We have two disjoint sets of vertices, U and V, with edges only going between the two sets. The idea of the
         * algorithm is that at each step we augment our existing matching by using an alternating path. This is a
         * path that starts at an unmatched U and ends at an unmatched V, with 2n+1 edges alternating between being
         * in and out of the existing matching. If we can find such a path, we can improve our matching by removing the
         * n edges that are already part of the matching and adding the n+1 other edges.
         */
        fun matching(): List<Pair<T, T>> {
            // These hold our current matching at any given time, in both the forward and reverse directions.
            val uMatching = mutableMapOf<T, T>()
            val vMatching = mutableMapOf<T, T>()

            fun findAlternatingPath(): List<T> {
                val vPredecessors = mutableMapOf<T, T>()

                /**
                 * Use vPredecessors to reconstruct the path that the BFS took to arrive at the unmatched V.
                 */
                fun reconstructPath(endV: T): List<T> {
                    val path = mutableListOf<T>()
                    var v: T? = endV
                    while (v != null) {
                        val u = vPredecessors[v]!!
                        path.add(v)
                        path.add(u)
                        v = uMatching[u]
                    }
                    path.reverse()
                    return path
                }

                // Do a BFS to find the shortest alternating path from an unmatched U to an unmatched V.
                // At each step we start at a U, then check its V neighbors to see if they're matched. If no, we're
                // done. If yes, we enqueue the U that each one is matched to. This guarantees that we take an
                // alternating path.
                val uQueue = ArrayDeque<T>(forwardMapping.keys.filter { it !in uMatching })
                while (uQueue.isNotEmpty()) {
                    val u = uQueue.removeFirst()
                    for (v in forwardMapping[u]!!) {
                        if (v in vPredecessors) continue
                        vPredecessors[v] = u

                        val nextU = vMatching[v]
                        if (nextU != null) uQueue.addLast(nextU)
                        else return reconstructPath(v)
                    }
                }

                return listOf()
            }

            // Use alternating paths to repeatedly augment our current best matching.
            var alternatingPath = findAlternatingPath()
            while (alternatingPath.isNotEmpty()) {
                for (segment in alternatingPath.windowed(2, 2)) {
                    uMatching[segment[0]] = segment[1]
                    vMatching[segment[1]] = segment[0]
                }
                alternatingPath = findAlternatingPath()
            }

            return uMatching.toList()
        }
    }
}

@ExperimentalStdlibApi
fun main() {
    fun splitToSegments(str: String) =
        str.split(' ').map { it.map { Day08.Segment.valueOf(it.toUpperCase().toString()) }.toSet()}

    val lines = {}.javaClass.getResource("/day08.txt").openStream().reader().buffered().lineSequence()
            .map { it.split(" | ") }
            .map { halves -> Day08.Line(splitToSegments(halves[0]), splitToSegments(halves[1])) }
            .toList()

    val partA = lines.flatMap { it.outputs }.count { it.size == 2 || it.size == 4 || it.size == 3 || it.size == 7 }
    println("The answer to part A is $partA.")

    val partB = lines.sumBy {
        val possibleMappingsPerWord = it.inputs.map { segments ->
            val possibleMappings = Day08.SevenSegmentDisplay.digits.values
                    .filter { it.size == segments.size }
                    .reduce { acc, cur -> acc.union(cur) }
            segments.map { Pair(it, possibleMappings) }.toMap().toMutableMap()
        }
        val possibleMappings = possibleMappingsPerWord.reduce { acc, cur ->
            for (item in cur) {
                acc.merge(item.key, item.value) { old, new -> old.intersect(new) }
            }
            acc
        }
        val edges = possibleMappings.flatMap { kv -> kv.value.map { Pair(kv.key, it) } }
        val matching = Day08.BipartiteGraph(edges).matching().toMap()
        val unscrambled = it.outputs.map { it.map { matching[it]!! }.toSet() }
        unscrambled.map(Day08.SevenSegmentDisplay::decodeSegments).joinToString("").toInt()
    }
    println("The answer to part B is $partB.")
}
