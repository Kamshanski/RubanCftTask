package edu.kamshanski.rubancfttask.ui.main.convert

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import edu.kamshanski.rubancfttask.ui.main.CurrencyViewModel
import edu.kamshanski.rubancfttask.utils.toBigDecimalOfZero
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.math.BigDecimal
import java.text.DecimalFormat
import kotlin.time.ExperimentalTime

/** ViewModel for ConvertFragment. Stores selected foreign currency, input in ruble and in foreign
 * currency. Performs calculations on change of any parameter
 *
 */
@ExperimentalCoroutinesApi
class ConvertViewModel(application: Application) : CurrencyViewModel(application) {

    /** Set [amountStr] in rubles and calculate foreign equivalent */
    fun changeRuble(amountStr: String) {
        _rubleAmount.value = amountStr
        getExchangeRecord().value.value?.let { record ->
            record.rates[_foreignCurrency.value]?.let { rate ->
                _foreignAmount.value = formatMultiply(amountStr) {amount -> amount / rate.rubToForeignCoef}
            }
        }
    }

    /** Set [amountStr] in foreign currency and calculate ruble equivalent */
    fun changeForeign(amountStr: String) {
        _foreignAmount.value = amountStr
        getExchangeRecord().value.value?.let { record ->
            record.rates[_foreignCurrency.value]?.let { rate ->
                _rubleAmount.value = formatMultiply(amountStr) {amount -> amount * rate.rubToForeignCoef}
            }
        }
    }

    /** Set selected [currency] and calculate foreign equivalent of inserted rubles amount */
    fun changeCurrency(currency: String) {
        _foreignCurrency.value = currency
        getExchangeRecord().value.value?.let { record ->
            record.rates[currency]?.let { rate ->
                _rubleAmount.value?.let { rubles ->
                    _foreignAmount.value = formatMultiply(rubles) {amount -> amount / rate.rubToForeignCoef}
                }
            }
        }
    }

    /**
     * Format and transform inserted string to [BigDecimal], perform calculations and return
     * formatted amount
     *
     * @param amountStr - input string amount
     * @param transform - transformations on input amount, that gives output amount
     * @return calculated amount in string representation
     */
    fun formatMultiply(amountStr: String, transform: (BigDecimal) -> BigDecimal) : String {
        val amount = inputToNumber(amountStr)
        val convertedAmount = transform(amount)
        val formattedValue = numberToInput(convertedAmount)
        return formattedValue
    }

    private fun inputToNumber(input: String) : BigDecimal {
        val formattedAmountStr = input.replace(',', '.')
        return formattedAmountStr.toBigDecimalOfZero()
    }

    private fun numberToInput(number: BigDecimal) : String {
        val df = DecimalFormat("#.000")
        return df.format(number.toDouble())
            .replace(".", ",")
            .trimEnd('0')
            .let { if (it.first() == ',') "0" + it  else it }
            .let { if (it.last() == ',') it + "0" else it }
    }

    /** Selected currency */
    private val _foreignCurrency = MutableLiveData(repository.latestExchangeSelection)
    val foreignCurrency : LiveData<String> = _foreignCurrency

    /** Inserted ruble amount */
    private val _rubleAmount = MutableLiveData("")
    val rubleAmount : LiveData<String> = _rubleAmount
    val rubleAmountNumber: BigDecimal
        get() = inputToNumber(rubleAmount.value ?: "0")

    /** Inserted foreign currency amount  */
    private val _foreignAmount = MutableLiveData("")
    val foreignAmount : LiveData<String> = _foreignAmount
    val foreignAmountNumber: BigDecimal
        get() = inputToNumber(foreignAmount.value ?: "0")

}