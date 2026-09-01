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

package org.isoron.uhabits.notifications

import android.content.Context
import android.content.SharedPreferences
import android.media.RingtoneManager.getRingtone
import android.net.Uri
import android.preference.PreferenceManager
import me.tatarka.inject.annotations.Inject
import org.isoron.uhabits.core.AppScope
import org.isoron.uhabits.core.ui.CompletionSoundPlayer
import org.isoron.uhabits.inject.AppContext

@Inject
@AppScope
class AndroidCompletionSoundPlayer(
    @AppContext private val context: Context
) : CompletionSoundPlayer {

    private val prefs: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    override fun play() {
        val uriString = prefs.getString(KEY, null)
        if (uriString.isNullOrEmpty()) return
        val ringtone = getRingtone(context, Uri.parse(uriString)) ?: return
        ringtone.play()
    }

    companion object {
        const val KEY = "pref_completion_sound_uri"
    }
}
