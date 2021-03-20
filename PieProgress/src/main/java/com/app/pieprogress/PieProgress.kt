package com.app.pieprogress

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

private const val DEFAULT_STROKE_WIDTH = 10f
private const val DEFAULT_STROKE_MARGIN = 10f

class PieProgress @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var oldAngle: Float = 0.0f

    var currentAngle: Float = 0.0f
        private set

    var currentPercentage = 0f
        get() = MAX_ANGLE.percentOf(currentAngle).toFloat()
        private set

    private var strokeWidth = DEFAULT_STROKE_WIDTH
    private var strokeMargin = DEFAULT_STROKE_MARGIN

    private val pieRectF = RectF()
    private var strokeX: Float = 0f
    private var strokeY: Float = 0f
    private var strokeRadius: Float = 0f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintBackground = Paint(Paint.ANTI_ALIAS_FLAG)
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PieProgress,
            0, 0
        ).apply {
            this@PieProgress.rotation = getFloat(R.styleable.PieProgress_android_rotation, -90f)

            strokeWidth = getDimension(R.styleable.PieProgress_strokeWidth, DEFAULT_STROKE_WIDTH)
            strokeMargin = getDimension(R.styleable.PieProgress_strokeMargin, DEFAULT_STROKE_MARGIN)

            getColor(R.styleable.PieProgress_color, Color.BLUE).run(::setColor)
            getFloat(R.styleable.PieProgress_percentage, 0.0f).run(::setPercentage)
            recycle()
        }

        strokePaint.strokeWidth = strokeWidth
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val pieLeft = strokeMargin + strokeWidth
        val pieTop = strokeMargin + strokeWidth
        val pieRight = w.toFloat() - strokeMargin - strokeWidth
        val pieBottom = h.toFloat() - strokeMargin - strokeWidth

        pieRectF.set(pieLeft, pieTop, pieRight, pieBottom)

        strokeX = ((pieLeft + pieRight) / 2)
        strokeY = ((pieTop + pieBottom) / 2)
        strokeRadius = (w - strokeWidth) / 2
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawStroke()
        canvas?.drawPieBackground()
        canvas?.drawPieContent()
    }

    private fun startAnimation(from: Float, to: Float) {
        if (isInEditMode.not()) {
            ValueAnimator.ofFloat(from, to).apply {
                interpolator = FastOutSlowInInterpolator()
                duration = MAX_ANIM_DURATION.toLong()
                addUpdateListener {
                    currentAngle = it.animatedValue as Float
                    invalidate()
                }
            }.start()
        }
    }

    private fun Canvas.drawPieContent() {
        drawArc(pieRectF, 0.0f, currentAngle, true, paint)
    }

    private fun Canvas.drawPieBackground() {
        drawArc(pieRectF, 0.0f, 360.0f, true, paintBackground)
    }

    private fun Canvas.drawStroke() {
        drawCircle(strokeX, strokeY, strokeRadius, strokePaint)
    }

    fun setColor(color: Int) {
        paint.color = color
        strokePaint.color = Color.BLACK

        val colorStr = String.format("#22%06X", 0xFFFFFF and color)
        paintBackground.color = Color.parseColor(colorStr)
    }

    fun setPercentage(percentage: Float) {
        if (percentage.isNaN().not()) {
            val percentValue = if (percentage > 100f) 100f else percentage
            oldAngle = currentAngle
            currentAngle = MAX_ANGLE.percent(percentValue).toFloat()
            startAnimation(oldAngle, currentAngle)
        }
    }

    companion object {
        private const val MAX_ANGLE = 360F
        private const val MAX_ANIM_DURATION = 1500
    }
}

private fun Number.percent(percentage: Number) = (this.toDouble() / 100) * percentage.toDouble()

private fun Number.percentOf(value: Number) = (value.toDouble() * 100) / this.toDouble()
