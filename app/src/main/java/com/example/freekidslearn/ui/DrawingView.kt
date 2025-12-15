package com.example.freekidslearn.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Custom view for letter tracing with finger drawing
 */
class DrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val drawPath = Path()
    private val drawPaint = Paint().apply {
        color = Color.parseColor("#FF6B35")
        isAntiAlias = true
        strokeWidth = 20f
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private val canvasPaint = Paint(Paint.DITHER_FLAG)
    private var drawCanvas: Canvas? = null
    private var canvasBitmap: Bitmap? = null

    private val paths = mutableListOf<PathData>()

    data class PathData(val path: Path, val paint: Paint)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvasBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, canvasPaint)
        }

        // Draw all stored paths
        for (pathData in paths) {
            canvas.drawPath(pathData.path, pathData.paint)
        }

        // Draw current path
        canvas.drawPath(drawPath, drawPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                drawPath.moveTo(touchX, touchY)
            }

            MotionEvent.ACTION_MOVE -> {
                drawPath.lineTo(touchX, touchY)
            }

            MotionEvent.ACTION_UP -> {
                drawCanvas?.drawPath(drawPath, drawPaint)

                // Save the path
                val newPath = Path(drawPath)
                val newPaint = Paint(drawPaint)
                paths.add(PathData(newPath, newPaint))

                drawPath.reset()
            }

            else -> return false
        }

        invalidate()
        return true
    }

    /**
     * Clear the canvas
     */
    fun clearCanvas() {
        paths.clear()
        drawPath.reset()

        canvasBitmap?.let {
            drawCanvas = Canvas(it)
            drawCanvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        }

        invalidate()
    }

    /**
     * Change the drawing color
     */
    fun setDrawingColor(color: Int) {
        drawPaint.color = color
    }

    /**
     * Change the stroke width
     */
    fun setStrokeWidth(width: Float) {
        drawPaint.strokeWidth = width
    }
}
