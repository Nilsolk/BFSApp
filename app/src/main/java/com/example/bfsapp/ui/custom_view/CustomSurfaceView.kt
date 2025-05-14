package com.example.bfsapp.ui.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.bfsapp.data.DrawThread
import com.example.bfsapp.data.Graph

class CustomSurfaceView(context: Context, attributeSet: AttributeSet) :
    SurfaceView(context, attributeSet), SurfaceHolder.Callback {

    private var drawThread: DrawThread? = null
    private lateinit var graph: Graph

    init {
        holder.addCallback(this)
    }

    fun setGraph(graph: Graph) {
        this.graph = graph
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        drawThread = DrawThread(this)
        drawThread?.running = true
        drawThread?.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        drawThread?.running = false
        drawThread?.join()
    }

    fun drawGraph(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)

        val paint = Paint().apply {
            color = Color.BLACK
            strokeWidth = 4f
            textSize = 40f
        }

        for ((_, node) in graph.nodes) {
            val x1 = node.posX
            val y1 = node.posY
            for (neighborId in node.neighbors) {
                val neighbor = graph.nodes[neighborId]
                if (neighbor != null) {
                    canvas.drawLine(x1, y1, neighbor.posX, neighbor.posY, paint)
                }
            }
        }

        for ((id, node) in graph.nodes) {
            paint.color = Color.LTGRAY
            canvas.drawCircle(node.posX, node.posY, 50f, paint)
            paint.color = Color.BLACK
            canvas.drawText(id.toString(), node.posX - 20f, node.posY + 15f, paint)
        }
    }
}