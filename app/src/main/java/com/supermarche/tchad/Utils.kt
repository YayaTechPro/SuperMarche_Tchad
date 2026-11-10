package com.supermarche.tchad

import android.content.Context
import java.text.NumberFormat

// Small helper functions used by several screens
object Utils {

    // Formats an amount like "5,000 FCFA" (or "5 000 FCFA" in French)
    fun formatPrice(context: Context, amount: Double): String {
        val locale = context.resources.configuration.locales[0]
        val number = NumberFormat.getIntegerInstance(locale).format(Math.round(amount))
        return context.getString(R.string.price_format, number)
    }

    // Converts a stored category key ("Food") into the translated label ("Alimentation")
    fun categoryLabel(context: Context, key: String): String {
        val keys = context.resources.getStringArray(R.array.category_keys)
        val labels = context.resources.getStringArray(R.array.category_labels)
        val index = keys.indexOf(key)
        return if (index >= 0) labels[index] else key
    }
}
