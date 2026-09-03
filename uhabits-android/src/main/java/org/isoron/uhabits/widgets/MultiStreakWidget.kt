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

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.View
import org.isoron.platform.gui.toInt
import org.isoron.uhabits.R
import org.isoron.uhabits.activities.common.views.ColoredStreak
import org.isoron.uhabits.activities.common.views.MultiStreakChart
import org.isoron.uhabits.activities.habits.list.ListHabitsActivity
import org.isoron.uhabits.core.models.Habit
import org.isoron.uhabits.core.ui.views.WidgetTheme
import org.isoron.uhabits.widgets.views.GraphWidgetView

class MultiStreakWidget(
    context: Context,
    id: Int,
    private val habits: List<Habit>,
    stacked: Boolean = false
) : BaseWidget(context, id, stacked) {

    override val defaultHeight: Int = 250
    override val defaultWidth: Int = 250

    override fun getOnClickPendingIntent(context: Context): PendingIntent? {
        val intent = Intent(context, ListHabitsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun refreshData(view: View) {
        val widgetView = view as GraphWidgetView
        widgetView.setBackgroundAlpha(preferedBackgroundAlpha)
        if (preferedBackgroundAlpha >= 255) widgetView.setShadowAlpha(0x4f)
        val chart = widgetView.dataView as MultiStreakChart
        val merged = habits.flatMap { habit ->
            val color = WidgetTheme().color(habit.color).toInt()
            habit.streaks.getRecent(5).map { ColoredStreak(color, it) }
        }.sortedWith { a, b -> b.streak.compareNewer(a.streak) }
        if (chart.maxStreakCount > 0) {
            chart.setStreaks(merged.take(chart.maxStreakCount))
        } else {
            chart.setStreaks(merged)
        }
    }

    override fun buildView() =
        GraphWidgetView(context, MultiStreakChart(context)).apply {
            setTitle(context.getString(R.string.multi_streaks_title))
        }
}
