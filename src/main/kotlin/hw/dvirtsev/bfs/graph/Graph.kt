package hw.dvirtsev.bfs.graph

interface Graph {
    fun neighbors(index: Int): Array<Int>

    fun neighborsSize(index: Int): Int

    fun size(): Int
}