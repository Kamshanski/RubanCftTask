package edu.kamshanski.rubancfttask.ui.works

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import edu.kamshanski.rubancfttask.model.preferences.CurrencyPrefsImpl
import edu.kamshanski.rubancfttask.model.Repository
import edu.kamshanski.rubancfttask.model.RetrofitClient
import edu.kamshanski.rubancfttask.model.local.LocalCbrApiImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import kotlin.time.ExperimentalTime

/** Work for periodic exchange rate update */
@ExperimentalCoroutinesApi
class UpdateExchangeRateWork(context: Context, workerParams: WorkerParameters)
    : Worker(context, workerParams) {

    override fun doWork(): Result {
        val repository = Repository(
            RetrofitClient.getCbrApi(applicationContext),
            CurrencyPrefsImpl(applicationContext),
            LocalCbrApiImpl()
        )
        return runBlocking {
            try {
                repository.getExchangeRate(true)
                return@runBlocking Result.success()
            } catch (ex: Exception) {
                return@runBlocking Result.failure()
            }
        }
    }
}