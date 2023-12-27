package hw.dvirtsev.bfs

import hw.dvirtsev.bfs.graph.Graph
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicIntegerArray

class ParallelBfs(private val threadPoolSize: Int = 4) : Bfs {
    override fun run(graph: Graph, start: Int): Array<Int> {

        val visited = AtomicIntegerArray(graph.size())
        val result = Array(graph.size()) { -1 }
        var frontier = IntArray(1) { start }
        result[start] = 0

        visited.compareAndSet(start, 0, 1)

        while (frontier.isNotEmpty()) {
            val deg = IntArray(frontier.size)
            val currentNeighbors = Array<Array<Int>>(frontier.size) { arrayOf() }

            ParallelFor(threadPoolSize, 0..deg.size) {
                currentNeighbors[it] = graph.neighbors(frontier[it])
                deg[it] = currentNeighbors[it].size
            }

            val prefixSum = PrefixSum(deg, threadPoolSize).asList()

            val newFrontier = IntArray(prefixSum[deg.size - 1]) { -1 }

            ParallelFor(threadPoolSize, 0..deg.size) { index ->
                val frontierNode = frontier[index]
                var shift = prefixSum[index] - deg[index]

                for (neighborNode in currentNeighbors[index]) {
                    if (visited.compareAndSet(neighborNode, 0, 1)) {
                        result[neighborNode] = result[frontierNode] + 1
                        newFrontier[shift] = neighborNode
                        shift++
                    }
                }
            }

            frontier = ParallelFilter(threadPoolSize, newFrontier) { it != -1 }.result
        }

        return result
    }

    private class ParallelFor(
        private val threadPoolSize: Int,
        private val range: IntRange,
        private val loopAction: (Int) -> Unit,
    ) {
        init {
            Executors.newFixedThreadPool(threadPoolSize).use { service ->
                val tasks = (0..<threadPoolSize).map {
                    Callable {
                        parallelForAction(it)
                    }
                }
                service.invokeAll(tasks)
            }
        }

        private fun parallelForAction(start: Int) {
            var curIndex = start
            while (curIndex < range.last) {
                loopAction.invoke(curIndex)
                curIndex += threadPoolSize
            }
        }
    }

    private class ParallelFilter(
        threadPoolSize: Int,
        private val initialArray: IntArray,
        private val filterAction: (Int) -> Boolean,
    ) {
        val result: IntArray

        init {
            if (initialArray.isEmpty()) {
                result = IntArray(0)
            } else {
                val flags = initialArray.clone()
                ParallelFor(threadPoolSize, 0..flags.size) {
                    if (filterAction.invoke(flags[it])) {
                        flags[it] = 1
                    } else {
                        flags[it] = 0
                    }
                }

                val prefSumOnFlags = PrefixSum(flags, threadPoolSize).asList()

                result = IntArray(prefSumOnFlags.last())

                ParallelFor(threadPoolSize, 0..prefSumOnFlags.size) {
                    if (flags[it] == 1) {
                        result[prefSumOnFlags[it] - 1] = initialArray[it]
                    }
                }
            }
        }
    }
}