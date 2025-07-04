package com.bodakesatish.ktor

import kotlinx.serialization.Serializable

@Serializable
data class MFScheme(
    val schemeCode: Int,
    val schemeName: String
)