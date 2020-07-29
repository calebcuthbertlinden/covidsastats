package com.linden.covidsastats.mvi.model

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CovidStatsModelStore @Inject constructor() :
    ModelStore<CovidStatsState>(
        CovidStatsState.ViewCovidStatsState(null, true, Unit, true)
    )