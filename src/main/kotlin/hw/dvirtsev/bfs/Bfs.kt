package hw.dvirtsev.bfs

import hw.dvirtsev.bfs.graph.Graph

interface Bfs {
    fun run(graph: Graph, start: Int): Array<Int>
}