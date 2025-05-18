package com.example.bfsapp.data

class Graph {
    val nodes = mutableMapOf<Int, Node>()

    fun addNode(id: Int, x: Float = 0f, y: Float = 0f, name: String = "") {
        nodes[id] = Node(id = id, posX = x, posY = y, name = name)
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

    fun getLevels(start: Int): Map<Int, Int> {
        val levels = mutableMapOf<Int, Int>()
        val queue = ArrayDeque<Pair<Int, Int>>()
        val visited = mutableSetOf<Int>()

        queue.add(start to 0)
        visited.add(start)

        while (queue.isNotEmpty()) {
            val (node, level) = queue.removeFirst()
            levels[node] = level

            for (neighbor in nodes[node]?.neighbors ?: emptyList()) {
                if (neighbor !in visited) {
                    visited.add(neighbor)
                    queue.add(neighbor to level + 1)
                }
            }
        }

        return levels
    }

    fun addNodesFromInput(inputs: List<NodeInput>) {
        inputs.forEachIndexed { index, node ->
            addNode(index, name = node.name)
        }
        inputs.forEachIndexed { index, node ->
            node.connections.forEach { to ->
                addEdge(index, to)
            }
        }
    }

    fun getLevelGroups(start: Int): Map<Int, List<Int>> {
        return getLevels(start).entries.groupBy({ it.value }, { it.key })
    }

    fun bfsWithParents(start: Int): List<Pair<Int, Int>> {
        val visited = mutableSetOf<Int>()
        val queue = ArrayDeque<Int>()
        val parents = mutableMapOf<Int, Int>()

        queue.add(start)
        visited.add(start)

        val edgesInOrder = mutableListOf<Pair<Int, Int>>() // ребра обхода

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()

            for (neighbor in nodes[current]?.neighbors ?: listOf()) {
                if (neighbor !in visited) {
                    visited.add(neighbor)
                    queue.add(neighbor)
                    parents[neighbor] = current
                    edgesInOrder.add(current to neighbor)  // ребро обхода
                }
            }
        }
        return edgesInOrder
    }

}