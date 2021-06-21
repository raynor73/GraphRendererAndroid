package ilapin.graphrenderer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.SystemClock
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import org.joml.Random
import org.joml.Vector2f

/**
 * @author igorlapin on 20/06/2021.
 */
class GraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val scrollPosition = Vector2f()

    private val nodes = ArrayList<RenderableNode>()
    private val connections = ArrayList<ArrayList<Int>>()
    private val forces = ArrayList<ArrayList<Vector2f>>()

    private val random = Random()

    private val nodePaint = Paint()
    private val labelPaint = Paint()
    private val connectionPaint = Paint()
    private val selectionPaint = Paint()

    private val tmpVector = Vector2f()
    private val accumulatorVector = Vector2f()

    private var prevTimestamp: Long? = null

    init {
        nodePaint.color = NODE_COLOR
        nodePaint.style = Paint.Style.FILL
        nodePaint.isAntiAlias = true

        labelPaint.color = LABEL_COLOR
        labelPaint.textSize = 2 * NODE_RADIUS

        connectionPaint.apply {
            color = CONNECTION_COLOR
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        selectionPaint.apply {
            color = SELECTION_COLOR
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
    }

    fun addNode(node: RenderableNode) {
        nodes += node

        connections += ArrayList<Int>()
        forces += ArrayList<Vector2f>()

        connections.forEachIndexed { i, row ->
            if (i != connections.size - 1) {
                row += 0
            } else {
                repeat(connections.size) { row += 0 }
            }
        }
        forces.forEachIndexed { i, row ->
            if (i != forces.size - 1) {
                row += Vector2f()
            } else {
                repeat(forces.size) { row += Vector2f() }
            }
        }
    }

    fun makeConnection(from: Int, to: Int) {
        connections[from][to] = 1
        connections[to][from] = 1
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                nodes.forEach { node ->
                    val transformedPositionX = node.position.x() + scrollPosition.x()
                    val transformedPositionY = node.position.y() + scrollPosition.y()
                    if (
                        event.x > transformedPositionX - NODE_RADIUS &&
                        event.x < transformedPositionX + NODE_RADIUS &&
                        event.y > transformedPositionY - NODE_RADIUS &&
                        event.y < transformedPositionY + NODE_RADIUS
                    ) {
                        node.isSelected = !node.isSelected
                    }
                }

                true
            }
            else -> super.onTouchEvent(event)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        scrollPosition.x = measuredWidth / 2f
        scrollPosition.y = measuredHeight / 2f
    }

    override fun onDraw(canvas: Canvas) {
        val currentTimestamp = SystemClock.elapsedRealtimeNanos()
        prevTimestamp?.let { prevTimestamp ->
            val dt = (currentTimestamp - prevTimestamp) / NANOSECONDS_IN_SECOND

            canvas.drawColor(BACKGROUND_COLOR)
            canvas.translate(scrollPosition.x, scrollPosition.y)

            updateNodePositions(dt)

            connections.indices.forEach { i ->
                connections.indices.forEach { j ->
                    if (connections[i][j] == 1) {
                        canvas.drawLine(
                            nodes[i].position.x(),
                            nodes[i].position.y(),
                            nodes[j].position.x(),
                            nodes[j].position.y(),
                            connectionPaint
                        )
                    }
                }
            }

            nodes.forEach { renderNode(canvas, it) }
        }
        prevTimestamp = currentTimestamp

        invalidate()
    }

    private fun updateNodePositions(dt: Float) {
        calculateForces()
        applyForces(dt)
        applyConstraints()
    }

    private fun applyConstraints() {
        connections.indices.forEach { i ->
            connections[i].indices.forEach { j ->
                if (i != j && connections[i][j] == 1) {
                    nodes[j].position.sub(nodes[i].position, tmpVector)
                    if (tmpVector.length() > MAX_CONNECTED_NODES_DISTANCE) {
                        val reduceFactor = MAX_CONNECTED_NODES_DISTANCE / tmpVector.length()
                        tmpVector.mul(reduceFactor)
                        accumulatorVector.set(nodes[i].position)
                        accumulatorVector.add(tmpVector)
                        nodes[j].position = accumulatorVector
                    }
                }
            }
        }
    }

    private fun applyForces(dt: Float) {
        forces.indices.forEach { i ->
            accumulatorVector.zero()
            forces[i].forEach { force ->
                force.mul(NODES_VELOCITY * dt, tmpVector)
                accumulatorVector.add(tmpVector)
            }
            tmpVector.set(nodes[i].position)
            tmpVector.add(accumulatorVector)
            nodes[i].position = tmpVector
        }
    }

    private fun calculateForces() {
        connections.indices.forEach { i ->
            connections[i].indices.forEach { j ->
                if (i != j) {
                    nodes[j].position.sub(nodes[i].position, tmpVector)
                    if (tmpVector.length() == 0f) {
                        tmpVector.set(random.nextFloat(), random.nextFloat())
                    }

                    if (tmpVector.length() < MAX_FORCE_DISTANCE) {
                        tmpVector.negate().normalize()
                        forces[i][j].set(tmpVector)
                    } else {
                        forces[i][j].zero()
                    }
                }
            }
        }
    }

    private fun renderNode(canvas: Canvas, node: RenderableNode) {
        if (node.isSelected) {
            canvas.drawRect(
                node.position.x() - NODE_RADIUS,
                node.position.y() - NODE_RADIUS,
                node.position.x() + NODE_RADIUS,
                node.position.y() + NODE_RADIUS,
                selectionPaint
            )
        }
        canvas.drawCircle(node.position.x(), node.position.y(), NODE_RADIUS, nodePaint)
        canvas.drawText(
            node.value.toString(),
            node.position.x() - NODE_RADIUS / 2,
            node.position.y() + NODE_RADIUS / 2,
            labelPaint
        )
    }

    companion object {

        private const val NODE_RADIUS = 10f
        private const val BACKGROUND_COLOR = 0xff000000.toInt()
        private const val NODE_COLOR = 0xff000080.toInt()
        private const val LABEL_COLOR = 0xffffffff.toInt()
        private const val CONNECTION_COLOR = 0xff00ffff.toInt()
        private const val SELECTION_COLOR = 0xffffff00.toInt()

        private const val NANOSECONDS_IN_SECOND = 1e9f

        private const val MAX_CONNECTED_NODES_DISTANCE = 100f
        private const val MAX_FORCE_DISTANCE = 40f
        private const val NODES_VELOCITY = 50f
    }
}