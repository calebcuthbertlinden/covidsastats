package com.linden.covidsastats

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime

class CovidCountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()

        CovidServiceImplementation.newInstance().getCurrentCases("confirmed")
            .enqueue(object : Callback<List<CovidStat>> {
            override fun onResponse(call: Call<List<CovidStat>>, response: Response<List<CovidStat>>) {
                if (response.code() == 200) {

                    response.body()?.forEach {
                        it.toString()
                    }
                }
            }
            override fun onFailure(call: Call<List<CovidStat>>, t: Throwable) {
            }
        })
    }
}
