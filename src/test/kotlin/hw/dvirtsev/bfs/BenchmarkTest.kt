package hw.dvirtsev.bfs

import hw.dvirtsev.bfs.graph.CubicGraph
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.time.Duration
import kotlin.time.measureTime

class BenchmarkTest {
    companion object {
        private const val SIZE = 500
        private const val THREAD_POOL_SIZE = 4
        private const val REPEAT_TIMES = 5
    }
    @Test
    fun compareResults() {
        val graph = CubicGraph(SIZE)
        val resSeq = SequentialBfs().run(graph, 0)
        val perSeq = ParallelBfs(THREAD_POOL_SIZE).run(graph, 0)
        Assertions.assertThat(perSeq).isEqualTo(resSeq)
    }

    @Test
    fun measureSequentialTime() = measureBfsTime(SequentialBfs())

    @Test
    fun measureParallelTime() = measureBfsTime(ParallelBfs())

    private fun measureBfsTime(bfs: Bfs) {
        // test run
        bfs.run(CubicGraph(SIZE), 0)

        val timeRuns = buildList {
            repeat(REPEAT_TIMES) {
                val graph = CubicGraph(SIZE)
                val duration = measureTime {
                    bfs.run(graph, 0)
                }
                add(duration)
            }
        }

        println("Runs: $timeRuns")
        val avgSequentialDuration = timeRuns
            .fold(Duration.ZERO) { acc, d -> acc + d }
            .div(REPEAT_TIMES)
        println("Average runtime: $avgSequentialDuration")
    }
}