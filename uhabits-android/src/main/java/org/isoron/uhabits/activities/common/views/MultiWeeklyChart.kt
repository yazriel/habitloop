/*
 * Copyright (C) 2016-2025 Álinson Santos Xavier <git@axavier.org>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isoron.uhabits.activities.common.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import org.isoron.platform.gui.toInt
import org.isoron.platform.time.LocalDate
import org.isoron.platform.time.LocalDateFormatter
import org.isoron.uhabits.core.models.Entry.Companion.NO
import org.isoron.uhabits.core.models.Entry.Companion.SKIP
import org.isoron.uhabits.core.models.Entry.Companion.UNKNOWN
import org.isoron.uhabits.core.models.Entry.Companion.YES_AUTO
import org.isoron.uhabits.core.models.Entry.Companion.YES_MANUAL
import org.isoron.uhabits.core.ui.views.WidgetTheme
import org.isoron.uhabits.utils.InterfaceUtils.dpToPixels
import kotlin.math.min

data class MultiWeeklyRow(
    val color: Int,
    val label: String,
    val entries: List<Int>
)

class MultiWeeklyChart : View {
    private var paint: Paint? = null
    private var rect: RectF? = null
    private var rows: List<MultiWeeklyRow> = emptyList()
    private var weekStart: LocalDate = LocalDate(2020, 1, 1)
    private var dateFormatter: LocalDateFormatter? = null
    private var headerColor = 0
    private var borderColor = 0
    private var em = 0f
    private var baseSize = 0f
    private var labelWidth = 0f

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    fun setRows(rows: List<MultiWeeklyRow>) {
        this.rows = rows
        requestLayout()
    }

    fun setWeekStart(weekStart: LocalDate) {
        this.weekStart = weekStart
    }

    fun setDateFormatter(dateFormatter: LocalDateFormatter) {
        this.dateFormatter = dateFormatter
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (rows.isEmpty()) return
        val width = width.toFloat()

        paint!!.textSize = baseSize * 0.4f
        em = paint!!.fontSpacing

        drawHeaders(canvas, width)
        var top = baseSize
        for (row in rows) {
            drawRow(canvas, width, top, row)
            top += baseSize
        }
    }

    private fun drawHeaders(canvas: Canvas, width: Float) {
        val blockWidth = (width - labelWidth) / 7f
        paint!!.color = headerColor
        paint!!.textAlign = Paint.Align.CENTER
        val df = dateFormatter ?: return
        for (i in 0 until 7) {
            val date = weekStart.plus(i)
            val centerX = blockWidth * i + blockWidth / 2
            val label = df.shortWeekdayName(date).take(2).uppercase()
            canvas.drawText(label, centerX, baseSize / 2 + 0.3f * em, paint!!)
        }
    }

    private fun drawRow(canvas: Canvas, width: Float, top: Float, row: MultiWeeklyRow) {
        val blockWidth = (width - labelWidth) / 7f
        for (i in 0 until 7) {
            val left = blockWidth * i
            drawBlock(canvas, left, top, blockWidth, row.entries[i], row.color)
        }
        paint!!.color = row.color
        paint!!.textAlign = Paint.Align.RIGHT
        val centerX = width - labelWidth / 2
        canvas.drawText(row.label, centerX, top + baseSize / 2 + 0.3f * em, paint!!)
    }

    private fun drawBlock(
        canvas: Canvas,
        left: Float,
        top: Float,
        blockWidth: Float,
        value: Int,
        color: Int
    ) {
        val padding = blockWidth * 0.2f
        rect!![left + padding, top + padding, left + blockWidth - padding] =
            top + blockWidth - padding
        val radius = dpToPixels(context, 2f)

        when (value) {
            YES_MANUAL, YES_AUTO -> {
                paint!!.style = Paint.Style.FILL
                paint!!.color = color
                canvas.drawRoundRect(rect!!, radius, radius, paint!!)
            }
            SKIP -> {
                paint!!.style = Paint.Style.FILL
                paint!!.color = setAlpha(color, 0.5f)
                canvas.drawRoundRect(rect!!, radius, radius, paint!!)
                drawHatch(canvas)
            }
            UNKNOWN, NO -> {
                paint!!.style = Paint.Style.STROKE
                paint!!.strokeWidth = dpToPixels(context, 1f)
                paint!!.color = borderColor
                canvas.drawRoundRect(rect!!, radius, radius, paint!!)
            }
            else -> {
                paint!!.style = Paint.Style.STROKE
                paint!!.strokeWidth = dpToPixels(context, 1f)
                paint!!.color = borderColor
                canvas.drawRoundRect(rect!!, radius, radius, paint!!)
            }
        }
        paint!!.style = Paint.Style.FILL
    }

    private fun drawHatch(canvas: Canvas) {
        paint!!.style = Paint.Style.STROKE
        paint!!.strokeWidth = dpToPixels(context, 1f)
        paint!!.color = borderColor
        val left = rect!!.left
        val top = rect!!.top
        val right = rect!!.right
        val bottom = rect!!.bottom
        var k = rect!!.width() / 5
        val size = rect!!.width()
        repeat(3) {
            canvas.drawLine(left + k, top, left, top + k)
            canvas.drawLine(right - k, bottom, right, bottom - k)
            k += size / 4
        }
        paint!!.style = Paint.Style.FILL
    }

    private fun setAlpha(color: Int, fraction: Float): Int {
        return android.graphics.Color.argb(
            (255 * fraction).toInt(),
            android.graphics.Color.red(color),
            android.graphics.Color.green(color),
            android.graphics.Color.blue(color)
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        baseSize = min(height / 4.5f, width / 8.2f)
        paint!!.textSize = baseSize * 0.35f
        em = paint!!.fontSpacing
        paint!!.textAlign = Paint.Align.CENTER
        labelWidth = 0f
        for (row in rows) {
            labelWidth = maxOf(labelWidth, paint!!.measureText(row.label))
        }
        labelWidth += 0.3f * baseSize
    }

    private fun init() {
        paint = Paint()
        paint!!.isAntiAlias = true
        rect = RectF()
        val theme = WidgetTheme()
        headerColor = theme.mediumContrastTextColor.toInt()
        borderColor = theme.mediumContrastTextColor.toInt()
        labelColor = theme.highContrastTextColor.toInt()
    }
}
