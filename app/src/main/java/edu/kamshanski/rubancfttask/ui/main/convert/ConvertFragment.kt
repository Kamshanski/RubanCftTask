package edu.kamshanski.rubancfttask.ui.main.convert

import android.text.Editable
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.viewModels
import edu.kamshanski.rubancfttask.R
import edu.kamshanski.rubancfttask.databinding.ConvertionFragmentBinding
import edu.kamshanski.rubancfttask.model.entities.Rate
import edu.kamshanski.rubancfttask.ui.utils.AfterTextChangedListener
import edu.kamshanski.rubancfttask.ui.utils.OnlyOnItemSelectedListener
import edu.kamshanski.rubancfttask.ui.utils.getFormattedString
import edu.kamshanski.rubancfttask.ui.utils.setTextWithoutListener
import edu.kamshanski.rubancfttask.utils.Progress.*
import edu.kamshanski.tpuclassschedule.activities._abstract.BaseFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.math.BigDecimal
import kotlin.time.ExperimentalTime

/** Currency exchange fragment */
@ExperimentalCoroutinesApi
class ConvertFragment : BaseFragment() {
    private lateinit var binding: ConvertionFragmentBinding
    private val vm: ConvertViewModel by viewModels()
    /** last loaded currency list */
    private var currencyList: List<String> = emptyList()

    /** Listener for ruble [EditText] field */
    private val rubleListener = AfterTextChangedListener { s: Editable? ->
            vm.changeRuble(s.toString())
    }
    /** Listener for foreign currency [EditText] field */
    private val foreignListener = AfterTextChangedListener { s: Editable? ->
        vm.changeForeign(s.toString())
    }
    /** Listener for currency selection in [Spinner] */
    private val currencyListener = OnlyOnItemSelectedListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            val currency = currencyList[position]
            vm.changeCurrency(currency)
    }

    override fun initListeners() = with(binding) {
        spForeignCurrency.onItemSelectedListener = currencyListener

        edtRuble.addTextChangedListener(rubleListener)
        edtForeign.addTextChangedListener(foreignListener)


        // clear zero value input
        edtRuble.setOnFocusChangeListener { v, hasFocus ->
            val num = vm.rubleAmountNumber
            if (hasFocus && num.compareTo(BigDecimal.ZERO) == 0) {
                (v as EditText).setTextKeepState("")
            }
        }
        edtForeign.setOnFocusChangeListener { v, hasFocus ->
            val num = vm.foreignAmountNumber
            if (hasFocus && num.compareTo(BigDecimal.ZERO) == 0) {
                (v as EditText).setTextKeepState("")
            }
        }
    }

    override fun initViewModel() {
        // Init exchange rate loading
        repeatOnStarted {
            vm.getExchangeRecord().collect {
                when (it) {
                    is Loading -> disableConvert(getString(R.string.wait))
                    is Fail -> disableConvert(getFormattedString(R.string.loadingFailed, it.error?.message))
                    is Success -> enableConvert(it.value!!.rates)
                    else -> disableConvert(getString(R.string.unpredictedBehaviour))
                }
            }
        }

        // apply VM changes to views
        vm.foreignCurrency.observe(this) { currency ->
            binding.spForeignCurrency.setSelection(currencyList.indexOf(currency))
        }
        vm.foreignAmount.observe(this) { amount ->
            binding.edtForeign.setTextWithoutListener(amount, foreignListener)
        }
        vm.rubleAmount.observe(this) { amount ->
            binding.edtRuble.setTextWithoutListener(amount, rubleListener)
        }
    }

    /** Set not-ready or error UI state */
    private fun disableConvert(msg: String) = with(binding) {
        // show placeholder with message
        txPlaceholder.text = msg
        txPlaceholder.visibility = View.VISIBLE

        // disable input fields and spinner
        currencyList = emptyList()
        spForeignCurrency.isEnabled = false
        spForeignCurrency.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, emptyList<String>())

        edtForeign.isEnabled = false
        edtForeign.setTextKeepState("")
        edtRuble.isEnabled = false
        edtRuble.setTextKeepState("")
    }

    /** Set ready UI state */
    private fun enableConvert(rates: Map<String, Rate>) = with(binding) {
        // no error - hide placeholder
        txPlaceholder.visibility = View.GONE

        // fill spinner with currencies
        currencyList = rates.keys.toList()
        spForeignCurrency.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, currencyList)
        val initialSelection = vm.foreignCurrency
        val initialPosition = currencyList
            .indexOf(initialSelection.value)
            .coerceAtLeast(0)  // previous selection or first in list if not found
        spForeignCurrency.setSelection(initialPosition)
        spForeignCurrency.isEnabled = true

        // enable input fields
        edtForeign.isEnabled = true
        edtForeign.setText(vm.foreignAmount.value)
        edtRuble.isEnabled = true
        edtRuble.setText(vm.rubleAmount.value)
    }

}