package com.linden.covidsastats

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CovidService {

    @GET("country/south-africa/status/{status}?")
    fun getCurrentCovidStatsByStatus(@Path("status") status: String, @Query("from") from: String,
                           @Query("to") to: String): Call<List<CovidStat>>

}