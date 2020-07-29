package com.linden.covidsastats.model

sealed class CovidStatsState {

    data class ViewCovidStatsState(
        val covidStats: CovidStatViewModel?,
        val success: Boolean,
        val shouldFetch: Boolean) :
        CovidStatsState()

    data class Loading(var isLoading: Boolean) : CovidStatsState()
}


