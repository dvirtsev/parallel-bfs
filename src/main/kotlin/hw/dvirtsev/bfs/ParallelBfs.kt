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
        private val threadPoolSize: Int,
        private val initialArray: IntArray,
        private val filterAction: (Int) -> Boolean,
    ) {
        val result: IntArray

        init {
            Executors.newFixedThreadPool(threadPoolSize).use { service ->
                val arrays = List(threadPoolSize) { mutableListOf<Int>() }
                val filterTasks = (0..<threadPoolSize).map {
                    Callable {
                        parallelFilterAction(it, arrays[it])
                    }
                }
                service.invokeAll(filterTasks)

                val resultSize = arrays.sumOf { it.size }
                result = IntArray(resultSize)
                val startIndexes = IntArray(arrays.size) { 0 }
                (1..<arrays.size).map { startIndexes[it] = startIndexes[it - 1] + arrays[it - 1].size }
                val mergeTasks = (0..<threadPoolSize).map {
                    Callable {
                        parallelMergeAction(arrays[it], startIndexes[it])
                    }
                }
                service.invokeAll(mergeTasks)
            }
        }

        private fun parallelFilterAction(start: Int, filterResult: MutableList<Int>) {
            var curIndex = start
            while (curIndex < initialArray.size) {
                if (filterAction.invoke(initialArray[curIndex])) {
                    filterResult.add(initialArray[curIndex])
                }
                curIndex += threadPoolSize
            }
        }

        private fun parallelMergeAction(filterResult: MutableList<Int>, startIndex: Int) {
            filterResult.forEachIndexed { index, number ->
                result[startIndex + index] = number
            }
        }
    }
}