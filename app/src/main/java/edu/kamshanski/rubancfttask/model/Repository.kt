package edu.kamshanski.rubancfttask.model

import edu.kamshanski.rubancfttask.model.entities.ExchangeRecord
import edu.kamshanski.rubancfttask.model.local.LocalCbrApi
import edu.kamshanski.rubancfttask.model.preferences.CurrencyPrefsImpl
import edu.kamshanski.rubancfttask.model.web.CbrApi
import edu.kamshanski.rubancfttask.utils.hoursBetween
import edu.kamshanski.rubancfttask.utils.toSortedMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.time.ExperimentalTime

/**
 * Currency exchange repository
 * @property cbrApi - web api
 * @property sharedPrefs - currency purposes preferences
 * @property localStorage - local storage api
 */
@ExperimentalCoroutinesApi
class Repository(
    private val cbrApi: CbrApi,
    private val sharedPrefs: CurrencyPrefsImpl,
    private val localStorage: LocalCbrApi
) {

    /**
     * Returns Exchange Record for today.
     * Refresh it if the last sync was [REFRESH_HOURS_PERIOD] hours ago or if local db is empty
     *
     * @param refresh - if true forces to load new data from cbr api
     * @return fresh Exchange Rate record
     */
    suspend fun getExchangeRate(refresh: Boolean) : ExchangeRecord = withContext(Dispatchers.IO) {
        val now = LocalDateTime.now()
        val isOldData = hoursBetween(now,  sharedPrefs.timestamp) > REFRESH_HOURS_PERIOD
        val isNewDay = hoursBetween(now,  sharedPrefs.latestCbrTime) > 0
        if (refresh || isOldData || isNewDay) {
            return@withContext getNewExchangeRate()
        } else {
            val rates = localStorage.rates
            if (rates.isEmpty()) {
                return@withContext getNewExchangeRate()
            } else {
                return@withContext ExchangeRecord(
                    sharedPrefs.latestCbrTime,
                    sharedPrefs.previousCbrTime,
                    sharedPrefs.previousUrl,
                    sharedPrefs.latestSyncTime,
                    rates.toSortedMap {item -> item.charCode to item}
                )
            }
        }
    }

    /**
     * Load new exchange rate from web api and save it to local storage
     * @return loaded exchange rate record
     */
    private suspend fun getNewExchangeRate() : ExchangeRecord {
        val record = cbrApi.getExchangeRate()
        saveRecord(record)
        return record
    }

    /**
     * Save exchange rate [record] to local storage
     * @param record
     */
    private suspend fun saveRecord(record: ExchangeRecord) = withContext(Dispatchers.IO) {
        saveMeta(record)
        saveRates(record)
    }

    /**
     * Save exchange rate [record] metadata (sync time, cbrTime...) to preferences
     * @param record
     */
    private fun saveMeta(record: ExchangeRecord) {
        sharedPrefs.apply {
            latestSyncTime = LocalDateTime.now()

            latestCbrTime = record.date
            timestamp = record.timestamp
            previousUrl = record.previousUrl
            previousCbrTime = record.previousDate
        }
    }

    /**
     * Save exchange rate of each currency to local DB
     * @param record
     */
    private fun saveRates(record: ExchangeRecord) {
        localStorage.rates = record.rates.values.toList()
    }

    /** Quick access to last selected foreign currency for exchange */
    var latestExchangeSelection: String
        get() = sharedPrefs.latestExchangeSelection
        set(value) { sharedPrefs.latestExchangeSelection = value }


    companion object {
        /** Duration in hours in which the last sync data is valid */
        const val REFRESH_HOURS_PERIOD = 7
    }

}