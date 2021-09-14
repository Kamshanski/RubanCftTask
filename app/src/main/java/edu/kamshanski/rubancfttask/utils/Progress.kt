package edu.kamshanski.rubancfttask.utils
/*
 Progress marker wrapper
 */

sealed class Progress<T>(val value: T?, val error: Throwable?) {
    class Idle<T>() : Progress<T>(null, null)
    class Success<T>(value: T) : Progress<T>(value, null)
    class Loading<T> : Progress<T>(null, null)
    class Fail<T>(error: Throwable?) : Progress<T>(null, error)
}
