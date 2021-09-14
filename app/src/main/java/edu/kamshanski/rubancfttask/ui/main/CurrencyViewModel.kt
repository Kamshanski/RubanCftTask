package edu.kamshanski.rubancfttask.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.kamshanski.rubancfttask.model.preferences.CurrencyPrefsImpl
import edu.kamshanski.rubancfttask.model.Repository
import edu.kamshanski.rubancfttask.model.RetrofitClient
import edu.kamshanski.rubancfttask.model.entities.ExchangeRecord
import edu.kamshanski.rubancfttask.model.local.LocalCbrApiImpl
import edu.kamshanski.rubancfttask.utils.Progress
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

/** Base ViewModel for currency purposes. Comprises repository and data save/load operations */
@ExperimentalCoroutinesApi
open class CurrencyViewModel(application: Application) : AndroidViewModel(application) {
    protected val repository = Repository(
        RetrofitClient.getCbrApi(application),
        CurrencyPrefsImpl(application),
        LocalCbrApiImpl()
    )

    private val exchangeRecordFlow = MutableStateFlow<Progress<ExchangeRecord>>(Progress.Idle())

    init {
        loadExchangeRate()
    }

    /**
     * Starts loading exchange rate, that is wrapped in progress indicator
     * @param refresh true if data must be loaded directly from web api
     */
    private fun loadExchangeRate(refresh: Boolean = false) {
        viewModelScope.launch {
            exchangeRecordFlow.emit(Progress.Loading())
            try {
                val record = repository.getExchangeRate(refresh)
                exchangeRecordFlow.emit(Progress.Success(record))
            } catch (ex: Throwable) {
                ex.printStackTrace()
                exchangeRecordFlow.emit(Progress.Fail(ex))
            }
        }
    }

    /** Safe access of exchange rate flow */
    fun getExchangeRecord() : StateFlow<Progress<ExchangeRecord>> {
         if (exchangeRecordFlow.value is Progress.Idle) {
            loadExchangeRate()
        }
        return exchangeRecordFlow
    }

    /** Force data refreshment and safe exchange rate access */
    fun getNewExchangeRate() : StateFlow<Progress<ExchangeRecord>> {
        loadExchangeRate(true)
        return getExchangeRecord()
    }


}