package com.linden.covidsastats.model

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CovidStatsModelStore @Inject constructor() :
    ModelStore<CovidStatsState>(
        CovidStatsState.ViewCovidStatsState(covidStats = null, success = true, shouldFetch = true)
    )