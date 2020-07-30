package com.linden.covidsastats.model.view_model

import java.util.*

data class CovidStatViewModel(
    val id: String = UUID.randomUUID().toString(),
    var activeCases: Int?,
    var recoveredCases: Int?,
    var deaths: Int?,
    var completed: Boolean = false
)
