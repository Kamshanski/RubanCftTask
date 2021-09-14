package edu.kamshanski.tpuclassschedule.activities._abstract


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.launch
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * Base for any Fragment class. onAttach and onViewCreated is replaced with several functions that
 * can be overridden. They are invoked in the following order:
 * 1. onAttach:
 *      1.1 initFragment
 * 2. onViewCreated:
 *      2.1 initViews
 *      2.2 initViewModel
 *      2.3 initListeners
 *
 * This class takes care of ViewBinding. It uses reflection to inject ViewBinding object to
 * corresponding field.Thus binding lateinit var property must be declared.
 *
 * Also it has coroutines util.
 */
abstract class BaseFragment : Fragment() {
    // Methods to implement
    /** Implement for non-ui purposes. */
    protected open fun initFragment() {}
    /** Implement to configure Views' appearance */
    protected open fun initViews() {}
    /** Implement to init viewModel, subscribe to [LiveData], [Flow], set default settings, etc.*/
    protected open fun initViewModel() {}
    /** Implement to set listeners to set Views' behaviour*/
    protected open fun initListeners() {}

    override fun onAttach(context: Context) {
        super.onAttach(context)
        initFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)  {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initViewModel()
        initListeners()
    }

    // Internal field for ViewBinding injection
    private lateinit var inflate: Method
    private lateinit var bindingField: Field
//    private lateinit var viewModelField: Field

    init {
        var clazz: Class<*> = this.javaClass
        outer@ while (clazz != Any::class.java) {
            for (declaredField in clazz.declaredFields) {
                if (ViewBinding::class.java.isAssignableFrom(declaredField.type)) {
                    bindingField = declaredField
                    bindingField.isAccessible = true
                    for (method in bindingField.type.methods) {
                        if (method.parameterTypes.size == 3) {
                            inflate = method
                            break@outer
                        }
                    }
                }
            }
            clazz = clazz.superclass
        }
    }



// ViewModel by reflection
//    @SuppressWarnings("unchecked")
//    private fun <T : ViewModel> initViewModel() {
//        try {
//            val vm = ViewModelProvider(this).get(viewModelField.type as Class<T>);
//            viewModelField.set(this, vm);
//        } catch (e: IllegalAccessException) {
//            e.printStackTrace()
//        }
//    }
    /** Replaces with unimplemented methods above. Also inflates view using ViewBinding */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) : View? {
        try {
            var binding = bindingField[this] as ViewBinding?
            if (binding == null) {
                binding = inflate.invoke(null, inflater, container, false) as ViewBinding
                bindingField[this] = binding
            }
            return binding.root
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        try {
            bindingField[this] = null
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        super.onDestroyView()
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

