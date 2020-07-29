package com.linden.covidsastats.model.covid_service

import com.google.gson.annotations.SerializedName

class Country {
    @SerializedName("Country")
    var country: String? = null
    @SerializedName("Slug")
    var slug: String? = null
}