package edu.kamshanski.rubancfttask.ui.main.currencyList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.kamshanski.rubancfttask.R
import edu.kamshanski.rubancfttask.databinding.CurrencyListFragmentBinding
import edu.kamshanski.rubancfttask.databinding.ItemRageBinding
import edu.kamshanski.rubancfttask.model.entities.Rate
import edu.kamshanski.rubancfttask.model.resources.Flag
import edu.kamshanski.rubancfttask.ui.utils.getFormattedString
import edu.kamshanski.rubancfttask.ui.utils.setDrawable
import edu.kamshanski.rubancfttask.utils.Progress
import edu.kamshanski.tpuclassschedule.activities._abstract.BaseFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.ExperimentalTime

/** Fragment with CBR currency exchange rate */
@ExperimentalCoroutinesApi
class CurrencyListFragment : BaseFragment() {
    lateinit var binding: CurrencyListFragmentBinding
    private val vm: CurrencyListViewModel by viewModels()
    /** RecycleView currency adapter */
    private val adapter = CurrencyListAdapter()

    override fun initViews() {
        binding.rvCurrencyList.adapter = adapter
    }

    override fun initListeners() {
        binding.imbReload.setOnClickListener {
            vm.getNewExchangeRate() // may ignore returned flow as it's already collected
        }
    }

    override fun initViewModel() = repeatOnStarted {
        vm.getExchangeRecord().collect {
            when(it) {
                is Progress.Fail -> showPlaceholder(getFormattedString(R.string.loadingFailed, it.error?.message))
                is Progress.Loading -> showPlaceholder(getString(R.string.wait))
                is Progress.Success -> showExchangeRate(
                    it.value!!.rates.values.toList(),
                    it.value.date
                )
                is Progress.Idle -> vm.getExchangeRecord()
            }
        }
    }

    /** Set ready UI state */
    private fun showExchangeRate(rates: List<Rate>, cbrTime: LocalDateTime) {
        binding.txPlaceholder.visibility = View.GONE
        binding.txTime.apply {
            visibility = View.VISIBLE
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")
            text = getFormattedString(R.string.currencyDateTime, formatter.format(cbrTime))
        }
        binding.imbReload.isEnabled = true
        adapter.rates = rates
    }

    /** Set non-ready or error UI state */
    private fun showPlaceholder(msg: String) {
        binding.txPlaceholder.apply {
            text = msg
            visibility = View.VISIBLE
        }
        binding.txTime.visibility = View.GONE
        binding.imbReload.isEnabled = false
        adapter.rates = emptyList()
    }

    /** Adapter for currency list*/
    class CurrencyListAdapter : RecyclerView.Adapter<CurrencyListAdapter.ViewHolder>() {
        // local instance of exchange rate list
        var rates: List<Rate> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

        class ViewHolder(val binding: ItemRageBinding): RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                ItemRageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.binding.apply {
                val c = txNominal.context
                val item = rates[position]
                txRate.text = c.getFormattedString(R.string.twoDigitsPrecisionDecimal, item.valute)
                txCurrencyCharCode.text = item.charCode
                txCurrencyName.text = item.name
                txNominal.text = if (item.nominal == 1) "" else c.getFormattedString(R.string.nominal, item.nominal.toString())

                imgMovement.setDrawable(
                    if (item.valute - item.previos > 0) R.drawable.ic_rate_up
                    else R.drawable.ic_rate_down
                )

                Glide.with(c)
                    .load(Flag.link(item.charCode))
                    .into(imgFlag)

            }
        }

        override fun getItemCount(): Int = rates.size
    }

}