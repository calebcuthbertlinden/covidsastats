package com.linden.covidsastats.view

sealed class CovidStatsEvent {
    object OnFetchStatsEvent : CovidStatsEvent()
}