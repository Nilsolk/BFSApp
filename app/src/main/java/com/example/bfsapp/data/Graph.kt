package com.example.bfsapp.data
class Graph {
    val nodes = mutableMapOf<Int, Node>()

    fun addNode(id: Int, x: Float = 0f, y: Float = 0f) {
        nodes[id] = Node(id, x, y)
    }

    fun addEdge(from: Int, to: Int) {
        nodes[from]?.neighbors?.add(to)
        nodes[to]?.neighbors?.add(from)
    }

    fun clear() {
        nodes.clear()
    }

    fun bfs(start: Int): List<Int> {
        val visited = mutableSetOf<Int>()
        val queue = ArrayDeque<Int>()
        val order = mutableListOf<Int>()

        queue.add(start)
        visited.add(start)

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            order.add(current)

            for (neighbor in nodes[current]?.neighbors ?: listOf()) {
                if (neighbor !in visited) {
                    visited.add(neighbor)
                    queue.add(neighbor)
                }
            }
        }

        return order
    }
}