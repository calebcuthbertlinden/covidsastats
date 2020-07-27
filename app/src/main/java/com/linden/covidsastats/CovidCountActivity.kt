package com.linden.covidsastats

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.BindView
import butterknife.ButterKnife
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class CovidCountActivity : AppCompatActivity() {

    private val confirmed: String = "confirmed"
    private val recovered: String = "recovered"
    private val deaths: String = "deaths"

    private val AMOUNT_FORMAT = "###,###,###"

    private var confirmedAmount: Int = 0
    private var recoveredAmount: Int = 0
    private var deathsAmount: Int = 0

    @BindView(R.id.casesAmount) lateinit var casesAmount: TextView
    @BindView(R.id.recoveredCasesAmount) lateinit var recoveredCasesAmount: TextView
    @BindView(R.id.deathCasesAmount) lateinit var deathCasesAmount: TextView
    @BindView(R.id.swipeToRefresh) lateinit var swipeToRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        fetchCases()

        swipeToRefresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.colorSecondary))
        swipeToRefresh.setColorSchemeColors(Color.WHITE)
        swipeToRefresh.setOnRefreshListener {
            fetchCases()
            swipeToRefresh.isRefreshing = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchCases() {
        CovidServiceImplementation.newInstance().getCurrentCases(confirmed)
            .enqueue(object : Callback<List<CovidStat>> {
                override fun onResponse(call: Call<List<CovidStat>>, response: Response<List<CovidStat>>) {
                    if (response.code() == 200) {

                        response.body()?.forEach {
                            confirmedAmount = it.cases!!
                            casesAmount.text = getReadableAmount(confirmedAmount)
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
                            recoveredCasesAmount.text = getReadableAmount(recoveredAmount)
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
                            deathCasesAmount.text = getReadableAmount(deathsAmount)
                        }
                    }
                }

                override fun onFailure(call: Call<List<CovidStat>>, t: Throwable) {
                }
            })
    }

    fun getReadableAmount(amount: Int): String {
        val nf = NumberFormat.getNumberInstance(Locale.FRANCE)
        val df = nf as DecimalFormat
        df.applyPattern(AMOUNT_FORMAT)
        return df.format(amount)
    }
}
