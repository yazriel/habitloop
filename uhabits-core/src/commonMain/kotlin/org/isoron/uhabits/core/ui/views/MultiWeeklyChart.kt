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

package org.isoron.uhabits.core.ui.views

import org.isoron.platform.gui.Canvas
import org.isoron.platform.gui.DataView
import org.isoron.platform.gui.TextAlign
import org.isoron.platform.time.DayOfWeek
import org.isoron.platform.time.LocalDate
import org.isoron.platform.time.LocalDateFormatter
import org.isoron.uhabits.core.models.Entry
import org.isoron.uhabits.core.models.PaletteColor
import kotlin.math.min
import kotlin.math.round

data class MultiWeeklyData(
    val paletteColor: PaletteColor,
    val entries: List<Int>
)

class MultiWeeklyChart(
    var dateFormatter: LocalDateFormatter,
    var firstWeekday: DayOfWeek,
    var habits: List<MultiWeeklyData>,
    var theme: Theme,
    var today: LocalDate,
    var weekStart: LocalDate,
    var padding: Double = 0.0
) : DataView {

    override var dataOffset = 0
    override val dataColumnWidth: Double
        get() = 0.0

    private var blockSize = 0.0
    private var width = 0.0
    private var height = 0.0

    override fun draw(canvas: Canvas) {
        width = canvas.getWidth()
        height = canvas.getHeight()

        canvas.setColor(theme.cardBackgroundColor)
        canvas.fill()

        if (habits.isEmpty()) return

        val numHabits = habits.size
        blockSize = round((height - 2 * padding) / (numHabits + 1.2))
        val headerHeight = blockSize * 1.0
        val gridWidth = 7 * blockSize
        val startX = padding + (width - 2 * padding - gridWidth) / 2

        canvas.setFontSize(min(17.0, blockSize * 0.55))

        drawHeaders(canvas, startX)

        habits.forEachIndexed { rowIndex, habit ->
            drawRow(canvas, headerHeight + rowIndex * blockSize, startX, habit)
        }
    }

    private fun drawHeaders(canvas: Canvas, startX: Double) {
        canvas.setColor(theme.mediumContrastTextColor)
        canvas.setTextAlign(TextAlign.CENTER)

        for (i in 0 until 7) {
            val date = weekStart.plus(i)
            val label = dateFormatter.shortWeekdayName(date).take(1).uppercase()
            val x = startX + i * blockSize + blockSize / 2
            canvas.drawText(label, x, blockSize * 0.7)
        }
    }

    private fun drawRow(canvas: Canvas, y: Double, startX: Double, habit: MultiWeeklyData) {
        val color = theme.color(habit.paletteColor.paletteIndex)
        val blockSpacing = blockSize * 0.12
        val blockInner = blockSize - blockSpacing * 2
        val cornerRadius = blockInner * 0.2

        for (i in 0 until 7) {
            val x = startX + i * blockSize + blockSpacing
            val blockY = y + blockSpacing
            val value = if (i < habit.entries.size) habit.entries[i] else Entry.UNKNOWN

            when (value) {
                Entry.YES_MANUAL, Entry.YES_AUTO -> {
                    canvas.setColor(color)
                    canvas.fillRoundRect(x, blockY, blockInner, blockInner, cornerRadius)
                }
                Entry.SKIP -> {
                    canvas.setColor(color.blendWith(theme.cardBackgroundColor, 0.5))
                    canvas.fillRoundRect(x, blockY, blockInner, blockInner, cornerRadius)
                    drawHatch(canvas, x, blockY, blockInner)
                }
                else -> {
                    canvas.setColor(theme.lowContrastTextColor)
                    canvas.setStrokeWidth(1.0)
                    canvas.drawRect(x, blockY, blockInner, blockInner)
                }
            }
        }
    }

    private fun drawHatch(canvas: Canvas, x: Double, y: Double, size: Double) {
        canvas.setStrokeWidth(0.5)
        canvas.setColor(theme.cardBackgroundColor)
        var k = size / 5
        repeat(3) {
            canvas.drawLine(x + k, y, x, y + k)
            canvas.drawLine(x + size - k, y + size, x + size, y + size - k)
            k += size / 4
        }
    }

    override fun onClick(x: Double, y: Double) {}
    override fun onLongClick(x: Double, y: Double) {}
}
