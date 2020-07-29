package com.linden.covidsastats.mvi.model

import com.linden.covidsastats.mvi.intent.Intent
import io.reactivex.observables.ConnectableObservable

interface Model<S> {
    fun process(intent: Intent<S>)
    fun modelState(): ConnectableObservable<S>
}