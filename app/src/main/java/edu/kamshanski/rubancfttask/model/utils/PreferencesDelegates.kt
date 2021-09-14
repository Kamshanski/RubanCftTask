package edu.kamshanski.tomskpolytechnicuniversityclassschedule.model.preferences

import android.annotation.SuppressLint
import android.content.SharedPreferences
import java.time.LocalDateTime
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
/*
 Based on https://habr.com/ru/post/461161/#comment_20443827
 Convenient kotlin delegates for SharedPreferences
 */

/**
 * Delegate base
 * @property key - preferences property key
 */
abstract class PreferencesDelegate<T>(val key: String) : ReadWriteProperty<AbstractPreferences, T>

/**
 * Delegate base with already created default value. Use for primitives or light-weighted objects
 *
 * @param T - property type
 * @property defaultValue - returned if property is not absent
 * @property getter - get operator behaviour
 * @property setter - set operator behaviour
 */
abstract class AbstractPreferencesDelegate<T>(key: String,
                                              val defaultValue: T,
                                              val getter: SharedPreferences.(key: String, defVal: T) -> T,
                                              val setter: SharedPreferences.Editor.(key: String, value: T) -> SharedPreferences.Editor,
) : PreferencesDelegate<T>(key) {
    @SuppressLint("CommitPrefEdits")
    final override fun setValue(thisRef: AbstractPreferences, property: KProperty<*>, value: T) {
        setter(thisRef.prefs.edit(), key, value).apply()
    }

    final override fun getValue(thisRef: AbstractPreferences, property: KProperty<*>): T {
        return getter(thisRef.prefs, key, defaultValue)
    }
}

/**
 * Delegate base with lazy default value. Use for huge objects (set, array...)
 *
 * @param T - property type
 * @property defaultValue invoked and the result of which is returned if property is not absent
 * @property getter - get operator behaviour
 * @property setter - set operator behaviour
 */
abstract class AbstractPreferencesDelegateWithLazyDefault<T>(
    key: String,
    val defaultValue: () -> T,
    val getter: SharedPreferences.(key: String, defVal: () -> T) -> T,
    val setter: SharedPreferences.Editor.(key: String, value: T) -> SharedPreferences.Editor,
) : PreferencesDelegate<T>(key) {
    final override fun setValue(thisRef: AbstractPreferences, property: KProperty<*>, value: T) {
        setter(thisRef.prefs.edit(), key, value).apply()
    }

    final override fun getValue(thisRef: AbstractPreferences, property: KProperty<*>): T {
        return getter(thisRef.prefs, key, defaultValue)
    }
}

/** Delegate for logical preferences property */
class BooleanPreferencesDelegate(key: String,
                                 defaultValue: Boolean
) : AbstractPreferencesDelegate<Boolean>(key,
                                         defaultValue,
                                         SharedPreferences::getBoolean,
                                         SharedPreferences.Editor::putBoolean)


/** Delegate for text preferences property */
class StringPreferencesDelegate(key: String,
                                defaultValue: String
) : AbstractPreferencesDelegate<String>(
    key, defaultValue,
    { k, defVal -> getString(k, defVal) ?: defVal },
    SharedPreferences.Editor::putString
)

/** Delegate for integer preferences property */
class IntPreferencesDelegate(key: String,

                             defaultValue: Int
) : AbstractPreferencesDelegate<Int>(
    key, defaultValue,
    SharedPreferences::getInt,
    SharedPreferences.Editor::putInt
)

/** Delegate for long preferences property */
class LongPreferencesDelegate(key: String,
                              defaultValue: Long
) : AbstractPreferencesDelegate<Long>(
    key, defaultValue,
    SharedPreferences::getLong,
    SharedPreferences.Editor::putLong
)

/** Delegate for set of string preferences property */
class StringSetPreferencesDelegate(key: String,
                                   defaultValue: () -> Set<String>
) : AbstractPreferencesDelegateWithLazyDefault<Set<String>>(
    key, defaultValue,
    { k, _ -> getStringSet(k, null) ?: defaultValue() },
    SharedPreferences.Editor::putStringSet
)

/** Delegate for float preferences property */
class FloatPreferencesDelegate(key: String,
                               defaultValue: Float
) : AbstractPreferencesDelegate<Float>(
    key, defaultValue,
    SharedPreferences::getFloat,
    SharedPreferences.Editor::putFloat
)

/** Delegate for time preferences property */
class LocalDateTimePreferencesDelegate(key: String,
                                       defaultValue: LocalDateTime
) : AbstractPreferencesDelegate<LocalDateTime>(
    key, defaultValue,
    { k, def -> LocalDateTime.parse(getString(k, def.toString())) },
    { k, v -> putString(k, v.toString()) }
)

// convenient extensions
fun AbstractPreferences.boolPref(key: String, defValue: Boolean = false) = BooleanPreferencesDelegate(key, defValue)
fun AbstractPreferences.intPref(key: String, defValue: Int = 0) = IntPreferencesDelegate(key, defValue)
fun AbstractPreferences.longPref(key: String, defValue: Long = 0L) = LongPreferencesDelegate(key, defValue)
fun AbstractPreferences.floatPref(key: String, defValue: Float = 0f) = FloatPreferencesDelegate(key, defValue)
fun AbstractPreferences.stringPref(key: String, defValue: String = "") = StringPreferencesDelegate(key, defValue)
fun AbstractPreferences.stringSetPref(key: String, defValue: () -> Set<String> = { HashSet(1) }) = StringSetPreferencesDelegate(key, defValue)
fun AbstractPreferences.localDateTimePref(key: String, defValue: LocalDateTime) = LocalDateTimePreferencesDelegate(key, defValue)
