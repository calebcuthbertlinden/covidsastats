package com.linden.covidsastats.model.covid_service

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import toothpick.ProvidesSingletonInScope
import toothpick.config.Module
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

object CovidServiceApiModule : Module() {
    init {
        bind(CovidService::class.java).toProvider(CovidServiceProvider::class.java)
    }
}

@Singleton
@ProvidesSingletonInScope
class CovidServiceProvider @Inject constructor() : Provider<CovidService> {
    override fun get(): CovidService = retrofit.create(CovidService::class.java)

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://covidapi.info/")
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create()
            )
        ).build()
}