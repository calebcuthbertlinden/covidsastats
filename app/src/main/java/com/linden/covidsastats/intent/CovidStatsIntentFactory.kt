package com.linden.covidsastats.intent

import com.linden.covidsastats.model.CovidCountryViewModel
import com.linden.covidsastats.model.CovidStatViewModel
import com.linden.covidsastats.model.CovidStatsModelRepository
import com.linden.covidsastats.model.CovidStatsState
import com.linden.covidsastats.model.covid_service.Country
import com.linden.covidsastats.model.covid_service.CovidService
import com.linden.covidsastats.model.covid_service.CovidStat
import com.linden.covidsastats.view.CovidStatsEvent
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CovidStatsIntentFactory @Inject constructor(
    private val covidRestApi: CovidService,
    private val covidModelRepository: CovidStatsModelRepository) {

    private val confirmed: String = "confirmed"
    private val recovered: String = "recovered"
    private val deaths: String = "deaths"

    fun process(event: CovidStatsEvent) {
        covidModelRepository.process(mapToIntent(event))
    }

    private fun mapToIntent(viewEvent: CovidStatsEvent): Intent<CovidStatsState> {
        return when (viewEvent) {
            is CovidStatsEvent.OnFetchStatsEvent -> buildFetchStatsIntent(viewEvent)
            is CovidStatsEvent.OnFetchCountriesEvent -> buildCountriesIntent()
        }
    }

    private fun chainedIntent(block: CovidStatsState.() -> CovidStatsState) =
        covidModelRepository.process(intent(block))

    private fun buildFetchStatsIntent(viewEvent: CovidStatsEvent.OnFetchStatsEvent): Intent<CovidStatsState> {
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
                viewEvent.country, confirmed, "2020-07-27T00:00:00Z", "2020-07-28T00:00:00Z")
                .subscribeOn(Schedulers.io())
                .subscribe(::retrofitSuccess, ::retrofitError)

            CovidStatsState.Loading(isLoading = true)
        }
    }

    private fun buildCountriesIntent(): Intent<CovidStatsState> {
        return intent {

            fun retrofitSuccess(loadedCountries:List<Country>) = chainedIntent {
                CovidStatsState.ViewCountriesViewState(countries = loadedCountries.map { country -> CovidCountryViewModel(countryName = country.slug)})
            }

            fun retrofitError(throwable:Throwable) = chainedIntent {
                // TODO return error message
                CovidStatsState.ViewCountriesViewState(countries = listOf(CovidCountryViewModel(countryName = "south-africa")))
            }

            covidRestApi.getCountries()
                .subscribeOn(Schedulers.io())
                .subscribe(::retrofitSuccess, ::retrofitError)

            CovidStatsState.ViewCountriesViewState(countries = listOf(CovidCountryViewModel(countryName = "south-africa")))

        }
    }

}