package com.police.patrol.data.model

data class Patrol(
    val id: Int,
    val name: String,
    val carNumber: String?,
    val latitude: Double?,
    val longitude: Double?,
    val isActive: Boolean,
    val caseId: Int? = null
)
