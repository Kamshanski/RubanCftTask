package edu.kamshanski.rubancfttask.ui.main

import androidx.core.view.forEach
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.kamshanski.rubancfttask.R
import edu.kamshanski.rubancfttask.databinding.ActivityMainBinding
import edu.kamshanski.rubancfttask.model.local.ObjectBox
import edu.kamshanski.rubancfttask.ui.works.UpdateExchangeRateWork
import edu.kamshanski.tpuclassschedule.activities._abstract.BaseAppCompatActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime

/**
 * The only activity of this single activity application. Manage fragments transactions,
 * non-ui operations and periodical data update
 */
@ExperimentalCoroutinesApi
class MainActivity : BaseAppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun initActivity(): Boolean {
        // Init db
        ObjectBox.init(this)

        // Enqueue periodic exchange rate update
        val request = PeriodicWorkRequestBuilder<UpdateExchangeRateWork>(WORK_INTERVAL, TimeUnit.MINUTES).build()
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "CbrExchangeRateUpdateWork",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )

        return false
    }

    override fun initViews() {
        // Set up navigation components
        binding.navigationBar.selectedItemId = R.id.currencyListFragment
        bindNavigationElements(binding.navigationBar, findNavController(R.id.navFragment))
    }

    /** binding nav components. Based on NavigationUI.kt */
    private fun bindNavigationElements(bottomView: BottomNavigationView, navController: NavController) {
        bottomView.setOnItemSelectedListener {
            val navDestination = it.itemId
            val navigatedSuccess = navController.popBackStack(navDestination, true)
            if (!navigatedSuccess) {
                navController.navigate(navDestination)
            }
            true
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomView.menu.forEach {
                if (it.itemId == destination.id) {
                    it.isChecked = true
                }
            }
        }
    }

    companion object {
        /** Time interval in minutes for exchange rate update work */
        const val WORK_INTERVAL = 15L
    }
}