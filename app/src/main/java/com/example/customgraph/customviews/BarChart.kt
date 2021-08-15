package com.example.customgraph.customviews

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import com.example.customgraph.R
import kotlin.math.min

class BarChart(context: Context, attrs: AttributeSet?) : View(context, attrs) {


    private var finalHeight = 0
    private var finalWidth = 0

    private var barHolderWidth = 0

    private val barPointsList = mutableListOf<Int>()

    private var barGap = 10f
    private var barCornerRadius = 50f
    private var extraSpace = 50

    private var padding = 0

    private var verticalScalingFactor = 0

    private val pathsMap = mutableMapOf<Int, Path>()

    private val colorsList = mutableListOf<Int>(
        ContextCompat.getColor(context, R.color.lightest_purple),
        ContextCompat.getColor(context, R.color.soft_purple),
        ContextCompat.getColor(context, R.color.dark_purple)
    )

    private val barPainter = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = colorsList[0]
        this.style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = setTextSize()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val parentHeight = MeasureSpec.getSize(heightMeasureSpec)
        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)

        finalWidth = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.EXACTLY -> parentWidth
            MeasureSpec.AT_MOST -> parentWidth - 20
            else -> parentWidth - 20
        }

        finalHeight = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.EXACTLY -> parentHeight
            MeasureSpec.AT_MOST -> min(parentHeight, finalWidth / 2)
            else -> min(parentHeight, 1 * finalWidth / 2)
        }

        setMeasuredDimension(finalWidth, finalHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        initAttributes()
        handleAnyPendingDraw()
        setBarPoints(listOf(500, 100, 600, 320, 600, 500, 400))
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            colorsList.forEachIndexed { index, color ->
                pathsMap[index % 3]?.let { path ->
                    barPainter.color = color
                    canvas.drawPath(path, barPainter)
                }
            }
            for (i in 0..6) {
                val dayString = dayStringForNumber(i)
                canvas.drawText(
                    dayString, ((i * barHolderWidth + (i + 1) * barHolderWidth) / 2 + padding - barGap).toFloat(),
                    (finalHeight - extraSpace/3).toFloat(), textPaint
                )
            }
        }
    }

    private fun drawVerticalText(canvas: Canvas, forValue: Int) {
        textPaint.textSize = setTextSize(8f)
        canvas.drawText(
            forValue.toString(), 2*barGap, getVerticalMarks(forValue), textPaint
        )
    }

    fun setBarPoints(pointsList: List<Int>) {
        barPointsList.clear()
        barPointsList.addAll(pointsList)
        pathsMap.clear()

        val maxValue = barPointsList.maxOrNull() ?: verticalScalingFactor

        var colorIndex = 0

        barPointsList.forEachIndexed { index, top ->
            val newTopValue = (top.toFloat() / maxValue.toFloat()) * verticalScalingFactor
            val indexPath = pathsMap[colorIndex % 3] ?: Path()
            indexPath.addRoundRect(
                index * barHolderWidth + barGap + padding,
                (finalHeight - extraSpace - newTopValue),
                (index + 1) * barHolderWidth - barGap + padding,
                (finalHeight - extraSpace).toFloat(),
                barCornerRadius,
                barCornerRadius,
                Path.Direction.CW
            )
            pathsMap[colorIndex % 3] = indexPath
            colorIndex++
        }

        postInvalidate()
    }

    private fun initAttributes() {
        padding = finalWidth / 20
        barHolderWidth = (finalWidth - 2 * padding) / 7
        barGap = barHolderWidth / 15f
        barCornerRadius = 3 * (barHolderWidth - 2 * barGap) / 8
        extraSpace = finalHeight / 10
        verticalScalingFactor = finalHeight - 2 * extraSpace
    }

    private fun handleAnyPendingDraw() {
        val givenList = mutableListOf<Int>().apply {
            addAll(barPointsList)
        }
        setBarPoints(givenList)
    }

    private fun setTextSize(size: Float = 10f): Float {
        val resources = context?.resources ?: Resources.getSystem()
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size, resources.displayMetrics)
    }

    private fun dayStringForNumber(index: Int) = when (index) {
        0 -> "S"
        1 -> "M"
        2 -> "T"
        3 -> "W"
        4 -> "Th"
        5 -> "F"
        6 -> "Sa"
        else -> ""
    }

    private fun getVerticalMarks(barValue: Int) : Float {
        val pointsMax = barPointsList.maxOrNull() ?: verticalScalingFactor
        val newValue = (barValue.toFloat() / pointsMax.toFloat()) * verticalScalingFactor
        return (finalHeight - extraSpace - newValue)
    }
}

//ignore for now
enum class Days(val dayVal: Int) {
    Sunday(0),
    Monday(1),
    Tuesday(2),
    Wednesday(3),
    Thursday(4),
    Friday(5),
    Saturday(6),
    None(7)
}

data class PathDay(val path: Path, val isSelectedDate: Boolean)