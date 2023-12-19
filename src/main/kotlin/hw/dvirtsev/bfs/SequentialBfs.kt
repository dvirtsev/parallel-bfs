package hw.dvirtsev.bfs

import hw.dvirtsev.bfs.graph.Graph
import java.util.LinkedList

class SequentialBfs : Bfs {
    override fun run(graph: Graph, start: Int): Array<Int> {
        val visited = MutableList(graph.size()) { false }
        val result = Array(graph.size()) { -1 }
        val nodesQueue = LinkedList<Int>()

        visited[start] = true
        nodesQueue.push(start)
        result[start] = 0

        while (nodesQueue.isNotEmpty()) {
            val currentNode = nodesQueue.pop()
            for (node in graph.neighbors(currentNode)) {
                if (!visited[node]) {
                    visited[node] = true
                    result[node] = result[currentNode] + 1
                    nodesQueue.add(node)
                }
            }
        }

        return result
    }
}