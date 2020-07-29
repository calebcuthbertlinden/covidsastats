package com.linden.covidsastats.mvi.model

sealed class CovidStatsState {

    data class ViewCovidStatsState(
        val covidStats: CovidStatViewModel?,
        val success: Boolean,
        val cancel: Unit,
        val shouldFetch: Boolean) :
        CovidStatsState()

    data class Loading(var isLoading: Boolean) : CovidStatsState()
}


