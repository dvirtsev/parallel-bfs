package hw.dvirtsev.bfs

import hw.dvirtsev.bfs.graph.Graph

class SequentialBfsTest : BaseBfsTest() {
    override fun runBfs(graph: Graph, start: Int): Array<Int> {
        return SequentialBfs().run(graph, start)
    }
}