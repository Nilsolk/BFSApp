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
import kotlin.math.hypot
import kotlin.math.sin


class CustomSurfaceView(context: Context, attrs: AttributeSet) :
    SurfaceView(context, attrs), SurfaceHolder.Callback {

    internal var graph: Graph? = null

    internal var bfsOrder = listOf<Int>()
    internal var currentEdgeIndex = 0
    internal var arrowX = 0f
    internal var arrowY = 0f
    internal var bfsEdges = emptyList<Pair<Int,Int>>()

    private lateinit var drawThread: DrawThread

    init {
        holder.addCallback(this)
    }

    fun setGraph(graph: Graph) {
        this.graph = graph
        val startId = graph.nodes.keys.firstOrNull() ?: return
        graph.layoutTree(startId, width.toFloat(), height.toFloat())
        draw()
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

        val edgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = 4f
            style = Paint.Style.STROKE
        }
        graph?.let { g ->
            g.nodes.forEach { (_, node) ->
                node.neighbors.forEach { nbrId ->
                    g.nodes[nbrId]?.let { neighbor ->
                        val dx = neighbor.posX - node.posX
                        val dy = neighbor.posY - node.posY
                        val dist = hypot(dx, dy)
                        if (dist > radius) {
                            val sx = node.posX + dx / dist * radius
                            val sy = node.posY + dy / dist * radius
                            val ex = neighbor.posX - dx / dist * radius
                            val ey = neighbor.posY - dy / dist * radius

                            val idx = bfsEdges.indexOfFirst { it.first == node.id && it.second == nbrId }
                            edgePaint.color = if (idx == currentEdgeIndex) Color.RED else Color.BLACK

                            canvas.drawLine(sx, sy, ex, ey, edgePaint)
                        }
                    }
                }
            }
        }


        if (currentEdgeIndex in bfsEdges.indices) {
            val (from, to) = bfsEdges[currentEdgeIndex]
            graph?.nodes?.get(from)?.let { fromNode ->
                val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.RED
                    strokeWidth = 6f
                    style = Paint.Style.FILL_AND_STROKE
                }

                canvas.drawLine(fromNode.posX, fromNode.posY, arrowX, arrowY, arrowPaint)
                drawArrow(canvas, arrowPaint, fromNode.posX, fromNode.posY, arrowX, arrowY)
            }
        }

        val nodePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            textSize = 40f
            textAlign = Paint.Align.CENTER
        }
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = Color.BLACK
            strokeWidth = 4f
        }
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 40f
            textAlign = Paint.Align.CENTER
        }

        graph?.nodes?.forEach { (id, node) ->
            nodePaint.color = if (id in bfsOrder) Color.GREEN else Color.LTGRAY
            canvas.drawCircle(node.posX, node.posY, radius, nodePaint)
            canvas.drawCircle(node.posX, node.posY, radius, borderPaint)
            nodePaint.style = Paint.Style.FILL
            canvas.drawText(
                node.name.ifEmpty { id.toString() },
                node.posX,
                node.posY - radius - textOffsetY,
                textPaint
            )
        }

        if (currentEdgeIndex in bfsEdges.indices) {
            val targetId = bfsEdges[currentEdgeIndex].second
            graph?.nodes?.get(targetId)?.let { target ->
                val smallRadius = radius / 2f
                val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.RED
                    style = Paint.Style.FILL
                }
                canvas.drawCircle(target.posX, target.posY, smallRadius, highlightPaint)
            }
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
