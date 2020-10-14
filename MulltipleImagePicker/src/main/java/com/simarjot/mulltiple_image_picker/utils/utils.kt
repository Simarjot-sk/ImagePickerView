package com.simarjot.mulltiple_image_picker.utils

import android.content.Context
import kotlin.math.roundToInt

fun Context.dpToPx(dp: Int): Int {
    val density = resources.displayMetrics.density
    return (dp.toFloat() * density).roundToInt()
}
