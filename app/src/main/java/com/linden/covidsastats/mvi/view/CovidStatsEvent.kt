package com.linden.covidsastats.mvi.view

sealed class CovidStatsEvent {
    object OnFetchStatsEvent : CovidStatsEvent()
}