package com.example.birthdaybanner.transactions.requetmodel




data class TransactionRequestItem(
    val activityCode: String?,
    val activityDate: String?,
    val activityType: String?,
    val affiliateName: String?,
    val amPoints: Int?,
    val description: Description?,
    val pmaType: String?,
    val pnrFlight: String?,
    val qualificablePoints: Int?,
    val recordLocator: String?,
    val redemptionDate: String?,
    val segmentsEarned: Int?,
    val ticketNumberTrans: String?
)