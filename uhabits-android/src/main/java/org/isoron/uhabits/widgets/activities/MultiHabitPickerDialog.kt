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

package org.isoron.uhabits.widgets.activities

import android.app.Activity
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import org.isoron.uhabits.HabitsApplication
import org.isoron.uhabits.R
import org.isoron.uhabits.activities.AndroidThemeSwitcher
import org.isoron.uhabits.core.preferences.WidgetPreferences
import org.isoron.uhabits.widgets.WidgetUpdater

class MultiHabitPickerDialog : Activity() {

    private var widgetId = 0
    private lateinit var widgetPreferences: WidgetPreferences
    private lateinit var widgetUpdater: WidgetUpdater
    private val habitIds = ArrayList<Long>()
    private val habitNames = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val component = (applicationContext as HabitsApplication).component
        AndroidThemeSwitcher(this, component.preferences).applyDialog()
        val habitList = component.habitList
        widgetPreferences = component.widgetPreferences
        widgetUpdater = component.widgetUpdater
        widgetId = intent.extras?.getInt(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID) ?: 0

        for (h in habitList) {
            if (h.isArchived) continue
            habitIds.add(h.id!!)
            habitNames.add(h.name)
        }

        if (habitNames.isEmpty()) {
            setContentView(R.layout.widget_empty_activity)
            findViewById<TextView>(R.id.message).setText(R.string.no_habits)
            return
        }

        setContentView(R.layout.widget_multi_history_configure_activity)
        val listView = findViewById<ListView>(R.id.listView)
        val saveButton = findViewById<Button>(R.id.buttonSave)

        with(listView) {
            choiceMode = ListView.CHOICE_MODE_MULTIPLE
            adapter = ArrayAdapter(
                this@MultiHabitPickerDialog,
                android.R.layout.simple_list_item_multiple_choice,
                habitNames
            )
        }

        saveButton.setOnClickListener {
            val checked = listView.checkedItemPositions
            val selectedIds = ArrayList<Long>()
            for (position in habitIds.indices) {
                if (checked?.get(position) == true) {
                    selectedIds.add(habitIds[position])
                }
            }
            if (selectedIds.isNotEmpty()) confirm(selectedIds)
        }
    }

    fun confirm(selectedIds: List<Long>) {
        widgetPreferences.addWidget(widgetId, selectedIds.toLongArray())
        widgetUpdater.updateWidgets()
        setResult(
            RESULT_OK,
            Intent().apply {
                putExtra(EXTRA_APPWIDGET_ID, widgetId)
            }
        )
        finish()
    }
}
