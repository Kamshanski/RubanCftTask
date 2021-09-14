package edu.kamshanski.rubancfttask.model.local

import edu.kamshanski.rubancfttask.model.entities.Rate

/** Interface of local exchange rate storage, that holds the latest sync data */
interface LocalCbrApi {
    /** Exchange rates */
    var rates: List<Rate>
}