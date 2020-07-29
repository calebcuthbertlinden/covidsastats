package com.linden.covidsastats.view

sealed class CovidStatsEvent {
    data class OnFetchStatsEvent(var country: String) : CovidStatsEvent()
    object OnFetchCountriesEvent : CovidStatsEvent()
}