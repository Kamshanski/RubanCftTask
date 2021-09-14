package edu.kamshanski.rubancfttask.model.entities

import com.google.gson.annotations.SerializedName
import edu.kamshanski.rubancfttask.utils.joinToString
import java.time.LocalDateTime
import java.util.*

/**
 * Exchange rate record from [CbrApi]. Ready for [Gson]
 *
 * @property date
 * @property previousDate
 * @property previousUrl
 * @property timestamp
 * @property rates
 */
class ExchangeRecord(
    @SerializedName("Date")
    val date: LocalDateTime,
    @SerializedName("PreviousDate")
    val previousDate: LocalDateTime,
    @SerializedName("PreviousURL")
    val previousUrl: String,
    @SerializedName("Timestamp")
    val timestamp: LocalDateTime,
    @SerializedName("Valute")
    val rates: TreeMap<String, Rate>

) {
    override fun toString(): String {
        return "ExchangeRecord(date=$date, previousDate=$previousDate, previousUrl='$previousUrl', timestamp=$timestamp, rates=${rates.joinToString { _, v -> v.toString() + "\n" }})"
    }
}