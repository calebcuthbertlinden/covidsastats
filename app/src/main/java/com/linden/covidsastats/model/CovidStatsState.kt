package com.linden.covidsastats.model

import com.linden.covidsastats.model.view_model.CovidCountryViewModel
import com.linden.covidsastats.model.view_model.CovidStatViewModel

sealed class CovidStatsState {

    data class ViewCovidStatsState(
        val covidStats: CovidStatViewModel?,
        val success: Boolean,
        val shouldFetch: Boolean,
        val errorMessage: String?) :
        CovidStatsState()

    data class Loading(var isLoading: Boolean) : CovidStatsState()

    data class ViewCountriesViewState(var countries: List<CovidCountryViewModel?>) : CovidStatsState()
}


