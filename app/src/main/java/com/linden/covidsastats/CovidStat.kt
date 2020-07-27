package com.linden.covidsastats

import com.google.gson.annotations.SerializedName

class CovidStat {

    @SerializedName("Country")
    var country: String? = null

    @SerializedName("Province")
    var province: String? = null

    @SerializedName("CountryCode")
    var countryCode: String? = null

    @SerializedName("City")
    var city: String? = null

    @SerializedName("CityCode")
    var cityCode: String? = null

    @SerializedName("Cases")
    var cases: Int? = 0

}