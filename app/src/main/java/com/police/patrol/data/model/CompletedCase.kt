package com.police.patrol.data.model

data class CompletedCase(
    val completedCaseId: Int,
    val actionsTaken: String,
    val offenderName: String,
    val verdict: String
)
