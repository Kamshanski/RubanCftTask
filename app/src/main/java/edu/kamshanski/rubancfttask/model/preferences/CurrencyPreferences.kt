package edu.kamshanski.rubancfttask.model.preferences

import edu.kamshanski.tomskpolytechnicuniversityclassschedule.model.preferences.stringPref
import java.time.LocalDateTime

interface CurrencyPreferences {
    /**  Time of the latest sync */
    var latestSyncTime: LocalDateTime

    /** Time of cbr exchange rate publishing */
    var latestCbrTime: LocalDateTime

    /** Time of previous exchange rate publishing */
    var previousCbrTime: LocalDateTime

    /** Url to previous exchange rate record */
    var previousUrl: String

    /** Timestamp. idk */
    var timestamp: LocalDateTime

    /** Last foreign currency selected in [ConvertFragment] */
    var latestExchangeSelection: String
}