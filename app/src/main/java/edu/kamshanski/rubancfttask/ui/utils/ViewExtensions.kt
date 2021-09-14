package edu.kamshanski.rubancfttask.ui.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner

/*
 Utils for better UI programming
 */
public class AfterTextChangedListener(val listener: (s: Editable?) -> Unit) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        listener(s)
    }
}

public class OnlyOnItemSelectedListener(
    val listener: (parent: AdapterView<*>?, view: View?, position: Int, id: Long) -> Unit
) : AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(parent: AdapterView<*>?) {}
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        listener(parent, view, position, id)
    }


}

public fun EditText.setTextWithoutListener(txt: String, listenerToSkip: TextWatcher) {
    removeTextChangedListener(listenerToSkip)
    setTextKeepState(txt)
    addTextChangedListener(listenerToSkip)
}