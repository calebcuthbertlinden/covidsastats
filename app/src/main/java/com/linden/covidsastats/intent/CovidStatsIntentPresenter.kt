package com.linden.covidsastats.intent

import com.linden.covidsastats.model.CovidStatsModelRepository
import com.linden.covidsastats.model.CovidStatsState
import com.linden.covidsastats.model.covid_service.CovidService
import com.linden.covidsastats.model.covid_service.LatestStatByCountryResponse
import com.linden.covidsastats.model.view_model.CovidCountryViewModel
import com.linden.covidsastats.model.view_model.CovidStatViewModel
import com.linden.covidsastats.view.CovidStatsEvent
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

@Singleton
class CovidStatsIntentPresenter @Inject constructor(
    private val covidRestApi: CovidService,
    private val covidModelRepository: CovidStatsModelRepository
) {

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

            fun retrofitSuccess(loadedStats: LatestStatByCountryResponse) = chainedIntent {
                CovidStatsState.ViewCovidStatsState(
                    covidStats = CovidStatViewModel(
                        confirmed = loadedStats.result!!.entries.last().value.confirmed,
                        recovered = loadedStats.result!!.entries.last().value.recovered,
                        deaths = loadedStats.result!!.entries.last().value.deaths
                    ),
                    success = true,
                    shouldFetch = false,
                    errorMessage = null
                )
            }

            fun retrofitError(throwable: Throwable) = chainedIntent {
                // TODO return error message
                CovidStatsState.ViewCovidStatsState(
                    covidStats = null,
                    success = false,
                    shouldFetch = false,
                    errorMessage = throwable.message
                )
            }

            covidRestApi.getCurrentCovidStatsByCountryObservable(viewEvent.country)
                .subscribeOn(Schedulers.io())
                .subscribe(::retrofitSuccess, ::retrofitError)

            CovidStatsState.Loading(isLoading = true)
        }
    }

    private fun buildCountriesIntent(): Intent<CovidStatsState> {
        val countries: ArrayList<CovidCountryViewModel> = ArrayList()
        Locale.getAvailableLocales().forEach {
            try {
                if (it.isO3Country.isNotEmpty()) {
                    countries.add(CovidCountryViewModel(countryCode = it.isO3Country, countryName = it.displayCountry))
                }
            } catch (exception: MissingResourceException) {
                // do nothing
            }
        }

        return intent {
            CovidStatsState.ViewCountriesViewState(
                countries = countries)
        }
    }
}