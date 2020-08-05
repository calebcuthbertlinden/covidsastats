package com.linden.covidsastats.model.covid_service

import com.google.gson.annotations.SerializedName

class LatestStatByCountryResponse {
    @SerializedName("count")
    var count: Int? = 0
    @SerializedName("result")
    var result: Map<String, CovidStat>? = null
}