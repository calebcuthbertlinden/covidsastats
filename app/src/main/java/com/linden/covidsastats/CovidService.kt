package com.linden.covidsastats

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CovidService {

    @GET("country/south-africa/status/confirmed?")
    fun getTodayCovidStats(@Query("from") from: String, @Query("to") to: String): Call<List<CovidStat>>

    // 2020-07-01T00:00:00Z

}