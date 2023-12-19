package hw.dvirtsev.bfs

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveAction
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log2
import kotlin.math.roundToInt

class PrefixSum(
    initialArray: IntArray,
    private val threadPoolSize: Int,
    private val blockSize: Int = 128,
) {
    private val prefixSum = IntArray(initialArray.size) { 0 }

    fun asList(): IntArray = prefixSum

    init {
        val sumTree = IntArray(calcSize(initialArray.size)) { 0 }
        val sumTreeTask = SumTreeBuildAction(initialArray, sumTree, 0)
        ForkJoinPool(threadPoolSize).use { pool ->
            pool.invoke(sumTreeTask)

            val prefixSumTask = PrefixBuildAction(initialArray, prefixSum, sumTree, 0, 0, 0, initialArray.size)
            pool.invoke(prefixSumTask)
        }
    }

    private inner class SumTreeBuildAction(
        private val array: IntArray,
        private val sumTree: IntArray,
        private val currentTreeNode: Int,
        private val left: Int = 0,
        private val right: Int = array.size,
    ) : RecursiveAction() {

        override fun compute() {
            //println("Sum tree thread: ${Thread.currentThread().name}")
            if (left >= right) {
                return
            }

            if (right - left < blockSize) {
                sumTree[currentTreeNode] = (left..<right).sumOf { array[it] }
                return
            }

            val middle = (right + left) / 2
            val firstTask = SumTreeBuildAction(array, sumTree, currentTreeNode * 2 + 1, left, middle)
            val secondTask = SumTreeBuildAction(array, sumTree, currentTreeNode * 2 + 2, middle, right)
            invokeAll(firstTask, secondTask)

            sumTree[currentTreeNode] = sumTree[currentTreeNode * 2 + 1] + sumTree[currentTreeNode * 2 + 2]
        }
    }

    private inner class PrefixBuildAction(
        private val array: IntArray,
        private val prefixSum: IntArray,
        private val sumTree: IntArray,
        private val currentTreeNode: Int,
        private val prevSum: Int,
        private val left: Int = 0,
        private val right: Int = array.size,
    ) : RecursiveAction() {

        override fun compute() {
            //println("Prefix sum thread: ${Thread.currentThread().name}")
            if (left >= right) {
                return
            }

            if (right - left < blockSize) {
                prefixSum[left] = prevSum + array[left]
                for (i in (left + 1)..<right) {
                    prefixSum[i] = prefixSum[i - 1] + array[i]
                }
                return
            }

            val middle = (right + left) / 2
            val firstTask = PrefixBuildAction(array, prefixSum, sumTree, currentTreeNode * 2 + 1, prevSum, left, middle)
            val secondTask = PrefixBuildAction(array, prefixSum, sumTree, currentTreeNode * 2 + 2, prevSum + sumTree[currentTreeNode * 2 + 1], middle, right)
            invokeAll(firstTask, secondTask)
        }
    }

    private fun calcSize(initialSize: Int): Int {
        var oldSize = initialSize
        var newSize = 1
        while (oldSize > 1) {
            newSize *= 2
            oldSize /= 2
        }
        if (initialSize > newSize) newSize *= 2
        return newSize * 2
    }
}