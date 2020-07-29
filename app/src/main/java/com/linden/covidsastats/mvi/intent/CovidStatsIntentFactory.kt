package com.linden.covidsastats.mvi.intent

import com.linden.covidsastats.mvi.model.covid_service.CovidStat
import com.linden.covidsastats.mvi.model.covid_service.CovidService
import com.linden.covidsastats.mvi.model.CovidStatViewModel
import com.linden.covidsastats.mvi.model.CovidStatsModelStore
import com.linden.covidsastats.mvi.model.CovidStatsState
import com.linden.covidsastats.mvi.view.CovidStatsEvent
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CovidStatsIntentFactory @Inject constructor(
    private val covidRestApi: CovidService,
    private val covidModelStore: CovidStatsModelStore
) {

    private val confirmed: String = "confirmed"
    private val recovered: String = "recovered"
    private val deaths: String = "deaths"
    private val defaultCountry: String = "south-africa"

    fun process(event: CovidStatsEvent) {
        covidModelStore.process(mapToIntent(event))
    }

    private fun mapToIntent(viewEvent: CovidStatsEvent): Intent<CovidStatsState> {
        return when (viewEvent) {
            CovidStatsEvent.OnFetchStatsEvent -> buildFetchStatsIntent()
        }
    }

    private fun chainedIntent(block: CovidStatsState.() -> CovidStatsState) =
        covidModelStore.process(intent(block))

    private fun buildFetchStatsIntent(): Intent<CovidStatsState> {
        return intent {

            fun retrofitSuccess(loadedStats:List<CovidStat>) = chainedIntent {
                CovidStatsState.ViewCovidStatsState(
                    covidStats = CovidStatViewModel(activeCases = loadedStats[0].cases!!, recoveredCases = null, deaths = null),
                    success = true, cancel = Unit,
                    shouldFetch = false)
            }

            fun retrofitError(throwable:Throwable) = chainedIntent {
                // Do something with throwable
                CovidStatsState.ViewCovidStatsState(covidStats = null, success = false, cancel = Unit, shouldFetch = false)
            }

            covidRestApi.getCurrentCovidStatsByStatusAndCountryObservable(
                defaultCountry, confirmed, "2020-06-01T00:00:00Z", "2020-06-03T00:00:00Z")
                .subscribeOn(Schedulers.io())
                .subscribe(::retrofitSuccess, ::retrofitError)

            CovidStatsState.Loading(isLoading = true)
        }
    }

}