package edu.kamshanski.rubancfttask.model.web

import edu.kamshanski.rubancfttask.model.entities.ExchangeRecord
import retrofit2.http.GET

/** Api interface for https://www.cbr-xml-daily.ru/daily_json.js */
interface CbrApi {
    /**
     * Load exchange rate and corresponding metadata
     * @return response from cbr api
     */
    @GET("daily_json.js")
    suspend fun getExchangeRate() : ExchangeRecord
}