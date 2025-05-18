package com.example.bfsapp.data

import com.example.bfsapp.ui.custom_view.CustomSurfaceView
import kotlinx.coroutines.*


class DrawThread(
    private val surfaceView: CustomSurfaceView
) {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var animJob: Job? = null

    fun startBfs(startId: Int) {
        val graph = surfaceView.graph ?: return
        val bfsOrder = graph.bfs(startId)
        val bfsEdges = graph.bfsWithParents(startId)
        if (bfsEdges.isEmpty()) return

        val startNode = graph.nodes[startId] ?: return
        surfaceView.arrowX = startNode.posX
        surfaceView.arrowY = startNode.posY
        surfaceView.currentEdgeIndex = 0
        surfaceView.bfsOrder = bfsOrder

        animJob?.cancel()

        animJob = scope.launch {
            for (i in bfsOrder.indices) {
                surfaceView.bfsOrder = bfsOrder.subList(0, i + 1)
                surfaceView.draw()
                delay(200L)
            }

                for ((from, to) in bfsEdges) {
                val fromNode = graph.nodes[from] ?: continue
                val toNode = graph.nodes[to] ?: continue

                val steps = 30
                for (step in 0..steps) {
                    val t = step / steps.toFloat()
                    surfaceView.arrowX = lerp(fromNode.posX, toNode.posX, t)
                    surfaceView.arrowY = lerp(fromNode.posY, toNode.posY, t)
                    surfaceView.currentEdgeIndex++
                    surfaceView.draw()
                    delay(30L)
                }
            }

            val lastNode = graph.nodes[bfsEdges.last().second]
            if (lastNode != null) {
                surfaceView.arrowX = lastNode.posX
                surfaceView.arrowY = lastNode.posY
                surfaceView.draw()
            }
        }
    }

    fun stop() {
        animJob?.cancel()
        scope.cancel()
    }

    private fun lerp(a: Float, b: Float, t: Float): Float = a + (b - a) * t
}
