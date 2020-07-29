package com.linden.covidsastats.mvi.model

import com.google.gson.GsonBuilder
import com.linden.covidsastats.mvi.model.covid_service.CovidService
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import toothpick.ProvidesSingletonInScope
import toothpick.config.Module
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

typealias BaseUrl = String

object CovidServiceApiModule : Module() {
    init {
        bind(BaseUrl::class.java).toInstance("https://api.covid19api.com/")
        bind(CovidService::class.java).toProvider(CovidServiceProvider::class.java)
    }
}

@Singleton
@ProvidesSingletonInScope
class CovidServiceProvider @Inject constructor(baseUrl: BaseUrl) : Provider<CovidService> {
    override fun get(): CovidService = retrofit.create(CovidService::class.java)

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create()
            )
        ).build()
}