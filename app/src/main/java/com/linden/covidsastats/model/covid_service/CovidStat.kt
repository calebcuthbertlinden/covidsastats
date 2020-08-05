package com.linden.covidsastats.model.covid_service

import com.google.gson.annotations.SerializedName

class CovidStat {
    @SerializedName("confirmed")
    var confirmed: Int? = 0
    @SerializedName("deaths")
    var deaths: Int? = 0
    @SerializedName("recovered")
    var recovered: Int? = 0
}