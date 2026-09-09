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
package org.isoron.uhabits.widgets

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import org.isoron.uhabits.core.models.Habit
import org.isoron.uhabits.utils.toFixedAndroidColor

const val MULTI_WIDGET_TITLE_LETTERS = 5
const val MULTI_WIDGET_TITLE_HABITS = 5

fun buildColoredTitle(habits: List<Habit>): CharSequence {
    val names = habits.take(MULTI_WIDGET_TITLE_HABITS)
    val separator = " · "
    val spannable = SpannableString(
        names.joinToString(separator) { it.name.take(MULTI_WIDGET_TITLE_LETTERS) }
    )
    var offset = 0
    for (habit in names) {
        val name = habit.name.take(MULTI_WIDGET_TITLE_LETTERS)
        val start = offset
        val end = start + name.length
        spannable.setSpan(
            ForegroundColorSpan(habit.color.toFixedAndroidColor()),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        offset = end + separator.length
    }
    return spannable
}
