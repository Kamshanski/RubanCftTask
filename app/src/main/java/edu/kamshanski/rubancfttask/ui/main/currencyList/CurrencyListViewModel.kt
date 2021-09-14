package edu.kamshanski.rubancfttask.ui.main.currencyList

import android.app.Application
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import edu.kamshanski.rubancfttask.databinding.ConvertionFragmentBinding
import edu.kamshanski.rubancfttask.databinding.CurrencyListFragmentBinding
import edu.kamshanski.rubancfttask.ui.main.CurrencyViewModel
import edu.kamshanski.rubancfttask.ui.main.convert.ConvertViewModel
import edu.kamshanski.rubancfttask.utils.Progress
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlin.time.ExperimentalTime
/** Currently no use */
@ExperimentalCoroutinesApi
class CurrencyListViewModel(application: Application) : CurrencyViewModel(application) {
}