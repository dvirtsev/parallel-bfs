package hw.dvirtsev.bfs

import hw.dvirtsev.bfs.graph.ArrayGraph
import hw.dvirtsev.bfs.graph.Graph
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseBfsTest {
    fun testGraphs() = arrayOf(
        arrayOf(
            arrayOf(
                arrayOf(1, 2),
                arrayOf(0, 2),
                arrayOf(0, 1, 3),
                arrayOf(3),
            ),
            0,
            listOf(0, 1, 1, 2),
        ),
        arrayOf(
            arrayOf(
                arrayOf(1, 3, 4),
                arrayOf(0, 4),
                arrayOf(3),
                arrayOf(0, 2),
                arrayOf(0, 1),
            ),
            1,
            listOf(1, 0, 3, 2, 1),
        ),
        arrayOf(
            arrayOf(
                arrayOf(1, 2, 3, 4),
                arrayOf(0, 2, 3, 4),
                arrayOf(0, 1, 3, 4),
                arrayOf(0, 1, 2, 4),
                arrayOf(0, 1, 2, 3),
            ),
            4,
            listOf(1, 1, 1, 1, 0),
        ),
    )

    @ParameterizedTest
    @MethodSource("testGraphs")
    fun `test bfs`(graph: Array<Array<Int>>, start: Int, expected: List<Int>) {
        val result = runBfs(ArrayGraph(graph), start)
        Assertions.assertThat(result)
            .containsExactlyElementsOf(expected)
    }

    abstract fun runBfs(graph: Graph, start: Int): Array<Int>
}