package edu.kamshanski.rubancfttask.ui.utils

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment

/*
 Extensions for quick resources access
 */

inline fun Fragment.getDrawable(@DrawableRes id: Int) : Drawable? {
    return ResourcesCompat.getDrawable(resources, id, requireContext().theme)
}
inline fun ImageView.setDrawable(@DrawableRes id: Int) : Drawable? {
    return ResourcesCompat.getDrawable(resources, id, context.theme)
}

inline fun Fragment.getDrawableColor(@ColorRes id: Int) : Drawable {
    return ColorDrawable(getColor(id))
}

@ColorInt
inline fun Fragment.getColor(@ColorRes id: Int) : Int {
    return resources.getColor(id, context?.theme)
}

inline fun Fragment.getString(@StringRes id: Int) : String {
    return resources.getString(id)
}
inline fun Fragment.getFormattedString(@StringRes id: Int, vararg content: Any?) : String {
    return resources.getString(id).format(*content)
}
inline fun Context.getFormattedString(@StringRes id: Int, vararg content: Any?) : String {
    return resources.getString(id).format(*content)
}
