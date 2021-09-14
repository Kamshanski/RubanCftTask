package edu.kamshanski.tpuclassschedule.activities._abstract

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.launch
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * Base for any Activity class. OnCreate is divided into several functions that can be overridden.
 * They are invoked in the following order:
 * 1. initActivity
 * 2. initViews
 * 3. initViewModel
 * 4. initListeners
 *
 * This class takes care of ViewBinding. It uses reflection to inject ViewBinding object to
 * corresponding field.Thus binding lateinit var property must be declared.
 *
 * Also it has coroutines util.
 */
abstract class BaseAppCompatActivity : AppCompatActivity() {
    // Methods to implement
    /** Implement for non-ui purposes.
     * @return true if [onCreate] must be finished. E.g. if user must sign in before entering this activity*/
    protected open fun initActivity() : Boolean { return false }
    /** Implement to configure Views' appearance */
    protected open fun initViews() {}
    /** Implement to init viewModel, subscribe to [LiveData], [Flow], set default settings, etc.*/
    protected open fun initViewModel() {}
    /** Implement to set listeners to set Views' behaviour*/
    protected open fun initListeners() {}

    // Internal field for ViewBinding injection
    private lateinit var inflate: Method
    private lateinit var bindingField: Field

    init {
        var clazz: Class<*> = this.javaClass
        outer@ while (clazz != Any::class.java) {
            for (declaredField in clazz.declaredFields) {
                if (ViewBinding::class.java.isAssignableFrom(declaredField.type)) {
                    bindingField = declaredField
                    bindingField.isAccessible = true
                    for (method in bindingField.type.methods) {
                        val paramTypes = method.parameterTypes
                        if (paramTypes.size == 1 && paramTypes.contains(LayoutInflater::class.java)
                        ) {
                            inflate = method
                            break@outer
                        }
                    }
                }
            }
            clazz = clazz.superclass
        }
    }
    /** Replaces with unimplemented methods above. Also inflates view using ViewBinding */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (initActivity()) {
            return
        }

        try {
            var binding = bindingField[this] as ViewBinding?
            if (binding == null) {
                binding = inflate.invoke(null, layoutInflater) as ViewBinding
                bindingField[this] = binding
            }
            setContentView(binding.root)

            initViews()
            initViewModel()
            initListeners()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }

        return
    }

    override fun onDestroy() {
        try {
            bindingField[this] = null
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    /**
     * Shot form to start [block]
     * @param block - coroutine that starts when Lifecycle is STARTED
     * and finishes when lifecycleis STOP
     */
    inline fun repeatOnStarted(crossinline block: suspend () -> Unit) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                block()
            }
        }
    }
}