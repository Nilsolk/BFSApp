package com.example.bfsapp.ui.custom_view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.bfsapp.data.DrawThread
import com.example.bfsapp.data.Graph
import kotlinx.coroutines.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


class CustomSurfaceView(context: Context, attrs: AttributeSet) :
    SurfaceView(context, attrs), SurfaceHolder.Callback {

    internal var graph: Graph? = null

    internal var bfsOrder = listOf<Int>()
    internal var currentEdgeIndex = 0
    internal var arrowX = 0f
    internal var arrowY = 0f

    private lateinit var drawThread: DrawThread

    init {
        holder.addCallback(this)
    }

    fun setGraph(graph: Graph) {
        this.graph = graph
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        graph?.let { g ->
            val startId = g.nodes.keys.firstOrNull() ?: return@let
            g.layoutTree(startId, width.toFloat(), height.toFloat())
        }
        draw()

        drawThread = DrawThread(this)
    }

    fun startBfsAnimation(startNodeId: Int) {
        if (::drawThread.isInitialized) {
            drawThread.startBfs(startNodeId)
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        if (::drawThread.isInitialized) {
            drawThread.stop()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    internal fun draw() {
        val canvas = holder.lockCanvas() ?: return
        synchronized(holder) {
            drawGraph(canvas)
        }
        holder.unlockCanvasAndPost(canvas)
    }

    private val radius = 50f
    private val textOffsetY = 20f

    internal fun drawGraph(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            strokeWidth = 4f
            textSize = 40f
            style = Paint.Style.STROKE
            textAlign = Paint.Align.CENTER
        }

        // Рисуем рёбра
        graph?.nodes?.forEach { (_, node) ->
            node.neighbors.forEach { neighborId ->
                graph?.nodes?.get(neighborId)?.let { neighbor ->
                    val dx = neighbor.posX - node.posX
                    val dy = neighbor.posY - node.posY
                    val dist = kotlin.math.hypot(dx, dy)
                    if (dist > radius) {
                        val startX = node.posX + dx / dist * radius
                        val startY = node.posY + dy / dist * radius
                        val endX = neighbor.posX - dx / dist * radius
                        val endY = neighbor.posY - dy / dist * radius
                        canvas.drawLine(startX, startY, endX, endY, paint)
                    }
                }
            }
        }

        val visitedSet = bfsOrder.toSet()

        paint.style = Paint.Style.FILL
        graph?.nodes?.forEach { (id, node) ->
            paint.color = if (id in visitedSet) Color.GREEN else Color.LTGRAY
            canvas.drawCircle(node.posX, node.posY, radius, paint)

            paint.color = Color.BLACK
            paint.style = Paint.Style.STROKE
            canvas.drawCircle(node.posX, node.posY, radius, paint)

            paint.style = Paint.Style.FILL
            canvas.drawText(
                node.name.ifEmpty { id.toString() },
                node.posX,
                node.posY - radius - textOffsetY,
                paint
            )
        }

        // Рисуем анимированную стрелку
        paint.color = Color.RED
        paint.strokeWidth = 6f
        paint.style = Paint.Style.FILL

        if (currentEdgeIndex < (graph?.bfsWithParents(bfsOrder.firstOrNull() ?: -1)?.size ?: 0)) {
            val fromId = graph?.bfsWithParents(bfsOrder.firstOrNull() ?: -1)?.get(currentEdgeIndex)?.first ?: -1
            graph?.nodes?.get(fromId)?.let {
                drawArrow(canvas, paint, it.posX, it.posY, arrowX, arrowY)
            }
        } else {
            canvas.drawCircle(arrowX, arrowY, 30f, paint)
        }
    }

    private fun drawArrow(canvas: Canvas, paint: Paint, startX: Float, startY: Float, currentX: Float, currentY: Float) {
        val dx = currentX - startX
        val dy = currentY - startY
        val angle = atan2(dy, dx)

        canvas.drawLine(startX, startY, currentX, currentY, paint)

        val arrowHeadLength = 40f
        val arrowHeadAngle = Math.toRadians(45.0)

        val x1 = currentX - arrowHeadLength * cos(angle - arrowHeadAngle).toFloat()
        val y1 = currentY - arrowHeadLength * sin(angle - arrowHeadAngle).toFloat()
        val x2 = currentX - arrowHeadLength * cos(angle + arrowHeadAngle).toFloat()
        val y2 = currentY - arrowHeadLength * sin(angle + arrowHeadAngle).toFloat()

        val path = Path()
        path.moveTo(currentX, currentY)
        path.lineTo(x1, y1)
        path.lineTo(x2, y2)
        path.close()

        canvas.drawPath(path, paint)
    }
}
