package com.linden.covidsastats.intent

import com.linden.covidsastats.model.covid_service.CovidStat
import com.linden.covidsastats.model.covid_service.CovidService
import com.linden.covidsastats.model.CovidStatViewModel
import com.linden.covidsastats.model.CovidStatsModelStore
import com.linden.covidsastats.model.CovidStatsState
import com.linden.covidsastats.view.CovidStatsEvent
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
                    success = true,
                    shouldFetch = false)
            }

            fun retrofitError(throwable:Throwable) = chainedIntent {
                // TODO return error message
                CovidStatsState.ViewCovidStatsState(covidStats = null, success = false, shouldFetch = false)
            }

            // TODO zip function to return recovered and death stats as well
            covidRestApi.getCurrentCovidStatsByStatusAndCountryObservable(
                defaultCountry, confirmed, "2020-07-27T00:00:00Z", "2020-07-28T00:00:00Z")
                .subscribeOn(Schedulers.io())
                .subscribe(::retrofitSuccess, ::retrofitError)

            CovidStatsState.Loading(isLoading = true)
        }
    }

}