package edu.kamshanski.rubancfttask.model.local

import android.content.Context
import edu.kamshanski.rubancfttask.model.entities.MyObjectBox
import io.objectbox.BoxStore
import io.objectbox.exception.DbException

/** ObjectBox Database convenient access and managing object */
object ObjectBox {
    private var isInitialized = false

    /** Store for currency purposes */
    private lateinit var _currencyStore: BoxStore

    /** Safe store access */
    val currencyStore: BoxStore
        get() = if (this::_currencyStore.isInitialized) _currencyStore
                else throw UninitializedPropertyAccessException(
                        "${this::_currencyStore.name} is not initialized")

    /** Must be invoked once when application is started */
    fun init(context: Context) {
        if (!isInitialized) {
            try {
                _currencyStore = MyObjectBox.builder()
                    .name("currencyStore")
                    .androidContext(context.applicationContext)
                    .build()
                isInitialized = true
            } catch (e: DbException) {
                throw e
            }
        }
    }
}