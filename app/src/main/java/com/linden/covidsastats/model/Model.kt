package com.linden.covidsastats.model

import com.linden.covidsastats.intent.Intent
import io.reactivex.observables.ConnectableObservable

interface Model<S> {
    fun process(intent: Intent<S>)
    fun modelState(): ConnectableObservable<S>
}