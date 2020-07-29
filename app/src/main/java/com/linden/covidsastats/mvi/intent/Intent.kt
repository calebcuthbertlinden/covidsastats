package com.linden.covidsastats.mvi.intent

interface Intent<T> {
    fun reduce(oldState: T): T
}

fun <T> intent(block: T.() -> T): Intent<T> = object : Intent<T> {
    override fun reduce(oldState: T): T = block(oldState)
}
