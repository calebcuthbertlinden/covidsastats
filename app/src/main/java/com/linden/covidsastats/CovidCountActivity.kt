package com.linden.covidsastats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CovidCountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.covid19api.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(CovidService::class.java)
        val call = service.getTodayCovidStats("2020-07-01T00:00:00Z", "2020-07-26T00:00:00Z")
        call.enqueue(object : Callback<List<CovidStat>> {
            override fun onResponse(call: Call<List<CovidStat>>, response: Response<List<CovidStat>>) {
                if (response.code() == 200) {

                    response.body()?.forEach {
                        it.toString();
                    }
                }
            }
            override fun onFailure(call: Call<List<CovidStat>>, t: Throwable) {
            }
        })

    }
}
