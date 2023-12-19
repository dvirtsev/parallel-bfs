package hw.dvirtsev.bfs.graph

class CubicGraph(private val edgeSize: Int) : Graph {
    private val size = edgeSize * edgeSize * edgeSize

    private val squareEdge = edgeSize * edgeSize

    override fun neighbors(index: Int) = buildList {
        val x = index % edgeSize
        val y = index % squareEdge / edgeSize
        val z = index / squareEdge
        if (x < size - 1) {
            add((x + 1) + (size * y) + (size * size * z))
        }
        if (x > 0) {
            add((x - 1) + (size * y) + (size * size * z))
        }
        if (y < size - 1) {
            add(x + (size * (y + 1)) + (size * size * z))
        }
        if (y > 0) {
            add((x  + (size * (y - 1)) + (size * size * z)))
        }
        if (z < size - 1) {
            add(x + (size * y) + (size * size * (z + 1)))
        }
        if (z > 0) {
            add((x  + (size * y) + (size * size * (z - 1))))
        }
    }.toTypedArray()

    override fun neighborsSize(index: Int): Int {
        val x = index % edgeSize
        val y = index % squareEdge / edgeSize
        val z = index / squareEdge
        var result = 6
        if (x == 0 || x == size - 1) result--
        if (y == 0 || y == size - 1) result--
        if (z == 0 || z == size - 1) result--
        return result
    }

    override fun size() = size
}