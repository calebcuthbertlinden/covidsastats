package com.linden.covidsastats.model.covid_service

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CovidService {

    @GET("country/{country}/status/{status}?")
    fun getCurrentCovidStatsByStatusAndCountryObservable(
        @Path("country") country: String,
        @Path("status") status: String,
        @Query("from") from: String,
        @Query("to") to: String
    ): Observable<List<CovidStat>>

}