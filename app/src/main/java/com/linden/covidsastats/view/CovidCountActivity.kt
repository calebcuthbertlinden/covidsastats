package com.linden.covidsastats.view

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnItemSelected
import com.google.android.material.textfield.TextInputLayout
import com.linden.covidsastats.R
import com.linden.covidsastats.intent.CovidStatsIntentFactory
import com.linden.covidsastats.model.CovidCountryViewModel
import com.linden.covidsastats.model.CovidStatsModelRepository
import com.linden.covidsastats.model.CovidStatsState
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import java.security.AccessController.getContext
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

class CovidCountActivity : AppCompatActivity(), StateSubscriber<CovidStatsState> {

    @Inject lateinit var covidStatsModelRepository: CovidStatsModelRepository
    @Inject lateinit var covidStatsIntentFactory: CovidStatsIntentFactory

    private val disposables = CompositeDisposable()
    private val amountFormat: String = "###,###,###"
    private val country: String = "south-africa"

    @BindView(R.id.casesAmount) lateinit var casesAmount: TextView
    @BindView(R.id.recoveredCasesAmount) lateinit var recoveredCasesAmount: TextView
    @BindView(R.id.deathCasesAmount) lateinit var deathCasesAmount: TextView
    @BindView(R.id.swipeToRefresh) lateinit var swipeToRefresh: SwipeRefreshLayout
    @BindView(R.id.loadingBar) lateinit var loadingBar: ProgressBar
    @BindView(R.id.filledExposeDropdown) lateinit var dropDownMenu: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        Toothpick.inject(this, openActivityScope(this))
    }

    override fun onResume() {
        super.onResume()
        disposables.add(covidStatsModelRepository.modelState().subscribeToState())

        swipeToRefresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.colorSecondary))
        swipeToRefresh.setColorSchemeColors(Color.WHITE)
        swipeToRefresh.setOnRefreshListener {
            swipeToRefresh.isRefreshing = false
            covidStatsIntentFactory.process(CovidStatsEvent.OnFetchStatsEvent(country))
        }
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        this.apply {
            Toothpick.closeScope(this)
        }
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
                                covidStatsIntentFactory.process(CovidStatsEvent.OnFetchStatsEvent(country))
                                covidStatsIntentFactory.process(CovidStatsEvent.OnFetchCountriesEvent)
                            }
                            it.covidStats != null -> {
                                presentCovidStats(
                                    it.covidStats.activeCases,
                                    it.covidStats.recoveredCases,
                                    it.covidStats.deaths)
                            }
                            !it.success -> {
                                // TODO show error state
                            }
                        }
                    }
                    is CovidStatsState.ViewCountriesViewState -> {
                        if (it.countries.isNotEmpty()) {
                            presentCountriesMenu(it.countries)
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

    fun presentCountriesMenu(countries: List<CovidCountryViewModel>) {
        val countryNames = countries.map { v -> v.countryName }
        dropDownMenu.setAdapter(ArrayAdapter(this, R.layout.dropdown_item, countryNames))
        dropDownMenu.setOnItemClickListener { adapterView, view, position, l ->
            covidStatsIntentFactory.process(CovidStatsEvent.OnFetchStatsEvent(dropDownMenu.text.toString()))}
    }

    fun getReadableAmount(amount: Int): String {
        val nf = NumberFormat.getNumberInstance(Locale.FRANCE)
        val df = nf as DecimalFormat
        df.applyPattern(amountFormat)
        return df.format(amount)
    }

    private fun openActivityScope(activity: Activity): Scope {
        return Toothpick.openScopes(activity.application, activity).apply {
            installModules(SmoothieActivityModule(activity))
        }
    }
}
