package com.police.patrol.data.model

data class ActiveCase(
    val case_id: Int,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val call_time: String,
    val patrol_id: Int?
)
