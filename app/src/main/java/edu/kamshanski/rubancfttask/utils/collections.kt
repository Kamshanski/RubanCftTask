package edu.kamshanski.rubancfttask.utils

import java.util.*

/*
Collection utils
 */

// just as in kotlin _Collections.kt
public fun <K,V> Map<K, V>.joinToString(separator: CharSequence = ", ", prefix: CharSequence = "", postfix: CharSequence = "", limit: Int = -1, truncated: CharSequence = "...", transform: ((K,V) -> CharSequence)? = null) : String {
    val buffer = StringBuilder()
    buffer.append(prefix)
    var count = 0
    for (element in this) {
        if (++count > 1) buffer.append(separator)
        if (limit < 0 || count <= limit) {
            buffer.append(transform?.invoke(element.key, element.value))
        } else break
    }
    if (limit >= 0 && count > limit) buffer.append(truncated)
    buffer.append(postfix)
    return buffer.toString()
}

public fun <T, K, V, M: MutableMap<K, V>> List<T>.toMap(destination: M, transform: (T) -> Pair<K, V>) : M {
    for (item in this) {
        val pair = transform(item)
        destination[pair.first] = pair.second
    }
    return destination
}

public fun <T, K, V> List<T>.toSortedMap(transform: (T) -> Pair<K, V>) : TreeMap<K, V> = toMap(TreeMap(), transform)