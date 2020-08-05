package com.linden.covidsastats.model.covid_service

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface CovidService {

    @GET("/api/v1/country/{country}/latest")
    fun getCurrentCovidStatsByCountryObservable(@Path("country") country: String): Observable<LatestStatByCountryResponse>

}