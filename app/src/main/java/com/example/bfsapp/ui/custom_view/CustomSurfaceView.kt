package com.example.bfsapp.ui.custom_view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.bfsapp.data.Graph
import kotlinx.coroutines.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class CustomSurfaceView(context: Context, attrs: AttributeSet) :
    SurfaceView(context, attrs), SurfaceHolder.Callback {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var graph: Graph? = null

    private var arrowX = 0f
    private var arrowY = 0f

    private var bfsEdges: List<Pair<Int, Int>> = emptyList()
    private var currentEdgeIndex = 0

    private var animJob: Job? = null

    init {
        holder.addCallback(this)
    }

    fun setGraph(graph: Graph) {
        this.graph = graph
    }

    fun startBfsAnimation(startNodeId: Int) {
        graph ?: return

        bfsEdges = graph!!.bfsWithParents(startNodeId)
        if (bfsEdges.isEmpty()) return

        currentEdgeIndex = 0

        // Стартовая точка — позиция стартовой вершины
        val startNode = graph!!.nodes[startNodeId] ?: return
        arrowX = startNode.posX
        arrowY = startNode.posY

        animJob?.cancel()
        animJob = scope.launch {
            while (currentEdgeIndex < bfsEdges.size) {
                val (fromId, toId) = bfsEdges[currentEdgeIndex]
                val fromNode = graph!!.nodes[fromId] ?: return@launch
                val toNode = graph!!.nodes[toId] ?: return@launch

                val steps = 30
                for (i in 0..steps) {
                    val t = i.toFloat() / steps
                    arrowX = lerp(fromNode.posX, toNode.posX, t)
                    arrowY = lerp(fromNode.posY, toNode.posY, t)

                    draw()

                    delay(30L)
                }

                currentEdgeIndex++
            }
            // После анимации ставим стрелку на последнюю вершину
            val lastNode = graph!!.nodes[bfsEdges.last().second] ?: return@launch
            arrowX = lastNode.posX
            arrowY = lastNode.posY
            draw()
        }
    }

    private fun lerp(a: Float, b: Float, t: Float): Float = a + (b - a) * t

    private fun draw() {
        val canvas = holder.lockCanvas() ?: return
        synchronized(holder) {
            drawGraph(canvas)
        }
        holder.unlockCanvasAndPost(canvas)
    }

    fun drawGraph(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)

        val paint = Paint().apply {
            color = Color.BLACK
            strokeWidth = 4f
            textSize = 40f
            style = Paint.Style.STROKE
            isAntiAlias = true
            textAlign = Paint.Align.CENTER  // Выравнивание текста по центру
        }

        // Рисуем рёбра
        val radius = 50f

        graph?.nodes?.forEach { (_, node) ->
            val x1 = node.posX
            val y1 = node.posY
            node.neighbors.forEach { neighborId ->
                graph?.nodes?.get(neighborId)?.let { neighbor ->

                    val dx = neighbor.posX - x1
                    val dy = neighbor.posY - y1
                    val dist = kotlin.math.sqrt(dx * dx + dy * dy)

                    if (dist > 0) {
                        val startX = x1 + dx / dist * radius
                        val startY = y1 + dy / dist * radius
                        val endX = neighbor.posX - dx / dist * radius
                        val endY = neighbor.posY - dy / dist * radius

                        canvas.drawLine(startX, startY, endX, endY, paint)
                    }
                }
            }
        }

        val textOffsetY = 20f // Отступ для текста над вершиной

        paint.style = Paint.Style.FILL

        // Множество посещённых вершин
        val visitedNodes = bfsEdges.flatMap { listOf(it.first, it.second) }.toSet()

        // Рисуем вершины
        graph?.nodes?.forEach { (id, node) ->
            paint.color = if (id in visitedNodes) Color.GREEN else Color.LTGRAY
            canvas.drawCircle(node.posX, node.posY, radius, paint)

            paint.color = Color.BLACK
            paint.style = Paint.Style.STROKE
            canvas.drawCircle(node.posX, node.posY, radius, paint)

            paint.style = Paint.Style.FILL
            // Рисуем название над вершиной
            canvas.drawText(
                node.name.ifEmpty { id.toString() },
                node.posX,
                node.posY - radius - textOffsetY,
                paint
            )
        }

        // Рисуем стрелку текущего положения
        graph?.let {
            if (currentEdgeIndex < bfsEdges.size) {
                val (fromId, _) = bfsEdges[currentEdgeIndex]
                val fromNode = it.nodes[fromId]
                if (fromNode != null) {
                    paint.color = Color.RED
                    paint.strokeWidth = 6f
                    paint.style = Paint.Style.FILL_AND_STROKE
                    drawArrow(canvas, paint, fromNode.posX, fromNode.posY, arrowX, arrowY)
                }
            } else {
                paint.color = Color.RED
                paint.style = Paint.Style.FILL
                canvas.drawCircle(arrowX, arrowY, 30f, paint)
            }
        }
    }


    private fun drawArrow(
        canvas: Canvas,
        paint: Paint,
        startX: Float,
        startY: Float,
        currentX: Float,
        currentY: Float
    ) {
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

    override fun surfaceCreated(holder: SurfaceHolder) {
        draw()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        animJob?.cancel()
        scope.cancel()
    }
}

