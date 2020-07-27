package com.linden.covidsastats

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CovidCountActivity : AppCompatActivity() {

    private val confirmed: String = "confirmed"
    private val recovered: String = "recovered"
    private val deaths: String = "deaths"

    private var confirmedAmount: Int = 0
    private var recoveredAmount: Int = 0
    private var deathsAmount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()

        CovidServiceImplementation.newInstance().getCurrentCases(confirmed)
            .enqueue(object : Callback<List<CovidStat>> {
                override fun onResponse(call: Call<List<CovidStat>>, response: Response<List<CovidStat>>) {
                    if (response.code() == 200) {

                        response.body()?.forEach {
                            confirmedAmount = it.cases!!
                        }
                    }
                }

                override fun onFailure(call: Call<List<CovidStat>>, t: Throwable) {
                }
            })

        CovidServiceImplementation.newInstance().getCurrentCases(recovered)
            .enqueue(object : Callback<List<CovidStat>> {
                override fun onResponse(call: Call<List<CovidStat>>, response: Response<List<CovidStat>>) {
                    if (response.code() == 200) {

                        response.body()?.forEach {
                            recoveredAmount = it.cases!!
                        }
                    }
                }

                override fun onFailure(call: Call<List<CovidStat>>, t: Throwable) {
                }
            })

        CovidServiceImplementation.newInstance().getCurrentCases(deaths)
            .enqueue(object : Callback<List<CovidStat>> {
                override fun onResponse(call: Call<List<CovidStat>>, response: Response<List<CovidStat>>) {
                    if (response.code() == 200) {

                        response.body()?.forEach {
                            deathsAmount = it.cases!!
                        }
                    }
                }

                override fun onFailure(call: Call<List<CovidStat>>, t: Throwable) {
                }
            })

    }
}
