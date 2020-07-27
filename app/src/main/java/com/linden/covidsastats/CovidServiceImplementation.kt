package com.linden.covidsastats

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime

class CovidServiceImplementation {

    var retrofit: Retrofit? = null

    companion object {

        val baseUrl = "https://api.covid19api.com/"
        val dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
        var instance: CovidServiceImplementation? = null
        var gson: Gson? = null

        fun newInstance(): CovidServiceImplementation {
            return if (instance != null) {
                instance as CovidServiceImplementation
            } else {
                init()
            }
        }

        fun init(): CovidServiceImplementation {
            instance = CovidServiceImplementation()
            gson = GsonBuilder()
                .setDateFormat(dateFormat)
                .create()
            instance!!.retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            return instance as CovidServiceImplementation
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentCases(status: String): Call<List<CovidStat>> {
        val service = retrofit?.create(CovidService::class.java)
        val current: LocalDateTime = LocalDateTime.now()
        return service!!.getCurrentCovidStatsByStatus(
            status,
            current.minusDays(1).withHour(0).toString(),
            current.withHour(0).toString())
    }

}