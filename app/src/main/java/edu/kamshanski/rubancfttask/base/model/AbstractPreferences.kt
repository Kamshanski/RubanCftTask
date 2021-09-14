package edu.kamshanski.tomskpolytechnicuniversityclassschedule.model.preferences

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Base for custom [SharedPreferences] wrapper class. Allows to clear and delete preferences.
 * Shared Preferences name [preferencesName] must be overridden.
 *
 * @property preferencesName Name of preferences file
 */
abstract class AbstractPreferences(context: Context) {
    abstract val preferencesName: String
    internal val prefs: SharedPreferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)

    fun clear() {
        prefs.edit().clear().apply()
    }

    fun delete(application: Application) {
            application.deleteSharedPreferences(preferencesName)
    }
}