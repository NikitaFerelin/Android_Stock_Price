package com.ferelin.stockprice.custom.chart

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.ferelin.stockprice.R
import com.ferelin.stockprice.custom.utils.BezierPoint
import com.ferelin.stockprice.custom.utils.Marker
import com.ferelin.stockprice.custom.utils.Point
import com.ferelin.stockprice.utils.px
import kotlin.math.abs

class ChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mMarkers: List<Marker> = emptyList()
    private var mBezierMarkers: HashMap<Marker, BezierPoint> = hashMapOf()

    private var mCharHeight: Int = 0
    private var mChartWidth: Int = 0

    private var mMaxValue: Float = 0F
    private var mMinValue: Float = 0F

    private var mSuggestionView: View = LayoutInflater.from(context).inflate(
        R.layout.activity_main,
        this.parent as ViewGroup?,
        true
    )

    private val mGradientColors = intArrayOf(
        ContextCompat.getColor(context, R.color.colorEnd),
        ContextCompat.getColor(context, R.color.colorStart)
    )

    private val mLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 2.px.toFloat()
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.black)
    }

    private var mGradientZeroY: Float = 0F
    private var mGradient: LinearGradient? = null
    private val mGradientPath: Path = Path()
    private lateinit var mGradientPaint: Paint

    private val mLinePath: Path = Path()

    private var mZeroY: Float = 0F
    private var mPxPerUnit: Float = 0F

    private var mOnTouchListener: ((marker: Marker) -> Unit)? = null
    private var mLastNearestPoint: Marker? = null

    override fun onDraw(canvas: Canvas) {
        drawGradient(canvas)
        drawLine(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        mChartWidth = widthSize
        mCharHeight = heightSize
        calcAndInvalidate()

        setMeasuredDimension(mChartWidth, mCharHeight)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var nearestPoint = Marker(Point(0F, 0F))
        for (index in 1 until mMarkers.size - 1) {
            val item = mMarkers[index]
            val itemPosition = item.position
            if (abs(event.x - itemPosition.x) < abs(event.x - nearestPoint.position.x)) {
                nearestPoint = item
            }
        }
        mLastNearestPoint = nearestPoint
        performClick()
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()

        mOnTouchListener!!.invoke(mLastNearestPoint!!)

        return true
    }

    fun setMarkers(markers: List<Marker>) {
        mMaxValue = markers.maxByOrNull { it.value }!!.value
        mMinValue = markers.minByOrNull { it.value }!!.value

        mMarkers = markers.toMutableList().apply {
            // Add start/end points
            val controlPoint = mMinValue + (mMinValue * 5 / 100)
            add(0, Marker(value = controlPoint))
            add(Marker(value = controlPoint))
        }.toList()
    }

    fun setOnTouchListener(func: (marker: Marker) -> Unit) {
        mOnTouchListener = func
    }

    private fun calcAndInvalidate() {
        if (mMarkers.isNotEmpty()) {
            calculatePositions()
            buildGradient()
            invalidate()
        }
    }

    private fun buildGradient() {
        mGradientPaint = Paint().apply {
            this.style = Paint.Style.FILL
            this.shader = mGradient
            this.isAntiAlias = true
        }
        mGradient = LinearGradient(
            0F,
            paddingTop.toFloat(),
            0F,
            mGradientZeroY,
            mGradientColors,
            null,
            Shader.TileMode.CLAMP
        )
    }

    private fun drawGradient(canvas: Canvas) {
        if (mMarkers.isNotEmpty()) {
            mGradientPath.apply {
                val firstItem = mMarkers.first().position
                moveTo(firstItem.x, mZeroY)
                lineTo(firstItem.x, firstItem.y)

                for (index in 1 until mMarkers.size) {
                    val marker = mMarkers[index]
                    val code = mBezierMarkers[mMarkers[index]]!!
                    mGradientPath.cubicTo(
                        code.x1,
                        code.y1,
                        code.x2,
                        code.y2,
                        marker.position.x,
                        marker.position.y
                    )
                }

                val lastItem = mMarkers.last().position
                lineTo(lastItem.x, mZeroY)

                close()
            }

            canvas.drawPath(mGradientPath, mGradientPaint)
        }
    }

    private fun drawLine(canvas: Canvas) {
        val firstItem = mMarkers.first()
        mLinePath.moveTo(firstItem.position.x, firstItem.position.y)

        for (index in 1 until mMarkers.size) {
            val marker = mMarkers[index]
            val bezierPoint = mBezierMarkers[mMarkers[index]]!!
            mLinePath.cubicTo(
                bezierPoint.x1,
                bezierPoint.y1,
                bezierPoint.x2,
                bezierPoint.y2,
                marker.position.x,
                marker.position.y
            )
        }

        canvas.drawPath(mLinePath, mLinePaint)
    }

    private fun calculatePositions() {
        mPxPerUnit = (mCharHeight - paddingTop - paddingBottom) / (mMaxValue - mMinValue)
        mZeroY = mMaxValue * mPxPerUnit + paddingTop
        mGradientZeroY = mZeroY - (mMinValue - (mMinValue * 5 / 100)) * mPxPerUnit

        val step = (mChartWidth) / (mMarkers.size - 1)
        mMarkers.first().apply {
            position.x = (step * 0).toFloat()
            position.y = mZeroY - value * mPxPerUnit
        }

        for (index in 1 until mMarkers.size) {
            val marker = mMarkers[index].apply {
                position.x = (step * index).toFloat()
                position.y = mZeroY - value * mPxPerUnit
            }
            mBezierMarkers[mMarkers[index]] = BezierPoint(
                x1 = (marker.position.x + mMarkers[index - 1].position.x) / 2,
                y1 = mMarkers[index - 1].position.y,
                x2 = (marker.position.x + mMarkers[index - 1].position.x) / 2,
                y2 = marker.position.y
            )
        }
    }
}