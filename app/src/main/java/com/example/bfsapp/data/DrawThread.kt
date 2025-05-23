package com.example.bfsapp.data

import android.graphics.Canvas
import android.view.SurfaceHolder
import com.example.bfsapp.ui.custom_view.CustomSurfaceView

class DrawThread(
    private val graphView: CustomSurfaceView
) : Thread() {
    var running = false
    private val surfaceHolder: SurfaceHolder = graphView.holder
    override fun run() {
        while (running) {
            var canvas: Canvas? = null
            try {
                canvas = surfaceHolder.lockCanvas()
                if (canvas != null) {
                    synchronized(surfaceHolder) {
                        if (graphView.bfsStepIndex < graphView.bfsSteps.size) {
                            val nodeId = graphView.bfsSteps[graphView.bfsStepIndex]
                            graphView.visitedInOrder.add(nodeId)
                            graphView.bfsStepIndex++
                        }
                        graphView.drawGraph(canvas)
                    }
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas)
                }
            }
            sleep(500)
        }
    }
}
