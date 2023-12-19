package hw.dvirtsev.bfs

import hw.dvirtsev.bfs.graph.ArrayGraph
import hw.dvirtsev.bfs.graph.Graph
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.random.Random

class ParallelBfsTest : BaseBfsTest() {
    override fun runBfs(graph: Graph, start: Int): Array<Int> {
        return ParallelBfs().run(graph, start)
    }

    @Test
    fun compareWithSequential() {
        val block = 50
        repeat(50) {
            val graph = ArrayGraph(
                (0..<block).map { left ->
                    (0..<block).mapNotNull { _ ->
                        if (Random.nextBoolean()) {
                            val rand = (0..<block).random()
                            if (rand == left) null else rand
                        } else {
                            null
                        }
                    }
                    .distinct()
                    .sorted()
                    .toTypedArray()
                }.toTypedArray()
            )

            val seqResult = SequentialBfs().run(graph, 0)
            val parallelResult = ParallelBfs().run(graph, 0)
            Assertions.assertThat(parallelResult).isEqualTo(seqResult)
        }
    }
}