package edu.kamshanski.rubancfttask.model.preferences

import android.content.Context
import edu.kamshanski.tomskpolytechnicuniversityclassschedule.model.preferences.AbstractPreferences
import edu.kamshanski.tomskpolytechnicuniversityclassschedule.model.preferences.localDateTimePref
import edu.kamshanski.tomskpolytechnicuniversityclassschedule.model.preferences.stringPref
import java.time.LocalDateTime

/** Common preferences for currency purposes */
class CurrencyPrefsImpl(context: Context) : AbstractPreferences(context), CurrencyPreferences {
    override val preferencesName: String = "currencyPrefs"

    /**  Time of the latest sync */
    override var latestSyncTime by localDateTimePref("latestSync", LocalDateTime.MIN)

    /** Time of cbr exchange rate publishing, idk */
    override var latestCbrTime by localDateTimePref("latestRateExchangeUpdateTime", LocalDateTime.MIN)

    /** Time of previous exchange rate publishing, idk */
    override var previousCbrTime by localDateTimePref("previousCbrTime", LocalDateTime.MIN)

    /** Url to previous exchange rate record */
    override var previousUrl by stringPref("previousUrl", "https://www.cbr-xml-daily.ru/daily_json.js")

    /** Timestamp. idk */
    override var timestamp by localDateTimePref("timestamp", LocalDateTime.MIN)

    /** Last foreign currency selected in [ConvertFragment] */
    override var latestExchangeSelection by stringPref("latestExchangeSelection", "USD")

}