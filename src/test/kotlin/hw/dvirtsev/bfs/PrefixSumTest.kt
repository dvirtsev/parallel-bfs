package hw.dvirtsev.bfs

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.random.Random

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PrefixSumTest {
    fun testArrays() = arrayOf(
        arrayOf(
            listOf(),
            listOf(),
        ),
        arrayOf(
            listOf(1),
            listOf(1),
        ),
        arrayOf(
            listOf(1, 2, 3, 4, 5),
            listOf(1, 3, 6, 10, 15),
        ),
    )

    @ParameterizedTest
    @MethodSource("testArrays")
    fun `test sum`(initial: List<Int>, expected: List<Int>) {
        val result = PrefixSum(initial.toIntArray(), 4).asList()
        Assertions.assertThat(result).isEqualTo(expected.toIntArray())
    }

    @Test
    fun `test sum long random`() {
        repeat(5) {
            val array = (1..1000).map { Random.nextInt() }.toIntArray()
            val expectedResult = array.clone()
            for (i in 1..<expectedResult.size) {
                expectedResult[i] += expectedResult[i - 1]
            }
            val result = PrefixSum(array, 4).asList()
            Assertions.assertThat(result).isEqualTo(expectedResult)
        }
    }
}