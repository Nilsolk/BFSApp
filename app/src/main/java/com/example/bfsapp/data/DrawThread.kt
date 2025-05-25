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
        val bfsEdges = graph.bfsWithParents(startId)
        if (bfsEdges.isEmpty()) return


        surfaceView.bfsOrder = listOf(startId)
        surfaceView.bfsEdges = bfsEdges
        surfaceView.currentEdgeIndex = 0


        graph.nodes[startId]?.let {
            surfaceView.arrowX = it.posX
            surfaceView.arrowY = it.posY
        }

        animJob?.cancel()
        animJob = scope.launch {

            for ((edgeIndex, edge) in bfsEdges.withIndex()) {
                val (from, to) = edge
                val n1 = graph.nodes[from]!!
                val n2 = graph.nodes[to]!!

                val steps = 30
                repeat(steps + 1) { step ->
                    val t = step / steps.toFloat()
                    surfaceView.arrowX = lerp(n1.posX, n2.posX, t)
                    surfaceView.arrowY = lerp(n1.posY, n2.posY, t)
                    surfaceView.currentEdgeIndex = edgeIndex
                    surfaceView.draw()
                    delay(30L)
                }


                val visited = surfaceView.bfsOrder.toMutableList()
                visited.add(to)
                surfaceView.bfsOrder = visited
                surfaceView.draw()


                delay(200L)
            }
        }
    }

    fun stop() {
        animJob?.cancel()
        scope.cancel()
    }

    private fun lerp(a: Float, b: Float, t: Float): Float = a + (b - a) * t
}
