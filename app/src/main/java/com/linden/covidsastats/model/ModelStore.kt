package com.linden.covidsastats.model

import com.jakewharton.rxrelay2.PublishRelay
import com.linden.covidsastats.intent.Intent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observables.ConnectableObservable

open class ModelStore<S>(startingState: S) : Model<S> {

    private val intents = PublishRelay.create<Intent<S>>()

    private val store = intents
        .observeOn(AndroidSchedulers.mainThread())
        .scan(startingState) { oldState, intent -> intent.reduce(oldState) }
        .replay(1)
        .apply { connect() }

    override fun process(intent: Intent<S>) = intents.accept(intent)

    override fun modelState(): ConnectableObservable<S> = store

}