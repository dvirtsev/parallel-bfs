package hw.dvirtsev.bfs.graph

class ArrayGraph(private val graph: Array<Array<Int>>) : Graph {
    override fun neighbors(index: Int) = graph[index]

    override fun neighborsSize(index: Int) = graph[index].size

    override fun size() = graph.size
}