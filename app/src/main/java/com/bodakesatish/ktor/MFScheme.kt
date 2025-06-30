package com.bodakesatish.ktor

import kotlinx.serialization.Serializable

@Serializable
data class MFScheme(
    val schemeCode: Int,
    val schemeName: String,
    val isinGrowth: String? = null, // Making these nullable to match the JSON
    val isinDivReinvestment: String? = null // Making these nullable to match the JSON
)