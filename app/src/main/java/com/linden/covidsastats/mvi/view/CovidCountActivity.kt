package com.linden.covidsastats.mvi.view

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.linden.covidsastats.R
import com.linden.covidsastats.mvi.intent.CovidStatsIntentFactory
import com.linden.covidsastats.mvi.model.CovidStatsModelStore
import com.linden.covidsastats.mvi.model.CovidStatsState
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

class CovidCountActivity : AppCompatActivity(), StateSubscriber<CovidStatsState> {

    @Inject lateinit var covidStatsModelStore: CovidStatsModelStore
    @Inject lateinit var covidStatsIntentFactory: CovidStatsIntentFactory

    private val disposables = CompositeDisposable()
    private val amountFormat: String = "###,###,###"

    @BindView(R.id.casesAmount) lateinit var casesAmount: TextView
    @BindView(R.id.recoveredCasesAmount) lateinit var recoveredCasesAmount: TextView
    @BindView(R.id.deathCasesAmount) lateinit var deathCasesAmount: TextView
    @BindView(R.id.swipeToRefresh) lateinit var swipeToRefresh: SwipeRefreshLayout
    @BindView(R.id.loadingBar) lateinit var loadingBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
    }

    override fun onResume() {
        super.onResume()
        disposables.add(covidStatsModelStore.modelState().subscribeToState())

//        swipeToRefresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.colorSecondary))
//        swipeToRefresh.setColorSchemeColors(Color.WHITE)
//        swipeToRefresh.setOnRefreshListener {
//            fetchCases()
//            swipeToRefresh.isRefreshing = false
//        }
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
    }

    override fun Observable<CovidStatsState>.subscribeToState(): Disposable {
        return CompositeDisposable().also { innerDisposables ->
            innerDisposables.add(subscribe {
                when(it) {
                    is CovidStatsState.Loading -> {
                        if (it.isLoading) {
                            presentLoading()
                        }
                    }
                    is CovidStatsState.ViewCovidStatsState -> {
                        when {
                            it.shouldFetch -> {
                                covidStatsIntentFactory.process(CovidStatsEvent.OnFetchStatsEvent)
                            }

                            it.covidStats != null -> {
                                presentCovidStats(
                                    it.covidStats.activeCases,
                                    it.covidStats.recoveredCases,
                                    it.covidStats.deaths)
                            }

                            !it.success -> {
                                // Show error state
                            }
                        }
                    }
                }
            })
        }
    }

    fun presentLoading() {
        loadingBar.visibility = View.VISIBLE
    }

    fun presentCovidStats(activeCases: Int?, recoveredCases: Int?, deaths: Int?) {
        loadingBar.visibility = View.GONE
        if (activeCases != null) {
            casesAmount.text = getReadableAmount(activeCases)
        }
        if (recoveredCases != null) {
            recoveredCasesAmount.text = getReadableAmount(recoveredCases)
        }
        if (deaths != null) {
            deathCasesAmount.text = getReadableAmount(deaths)
        }
    }

    fun getReadableAmount(amount: Int): String {
        val nf = NumberFormat.getNumberInstance(Locale.FRANCE)
        val df = nf as DecimalFormat
        df.applyPattern(amountFormat)
        return df.format(amount)
    }
}
