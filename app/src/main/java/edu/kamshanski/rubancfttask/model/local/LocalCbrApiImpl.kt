package edu.kamshanski.rubancfttask.model.local

import edu.kamshanski.rubancfttask.model.entities.Rate

/**
 * Context independent local exchange rate storage built with ObjectBox DB
 */
class LocalCbrApiImpl : LocalCbrApi {
    /** Exchange rates */
    override var rates: List<Rate>
        get()
            = ObjectBox.currencyStore.boxFor(Rate::class.java).all
        set(value) {
            val box = ObjectBox.currencyStore.boxFor(Rate::class.java)
            // in case if some currencies are deleted on api side
            box.removeAll()
            box.put(value)
        }

}