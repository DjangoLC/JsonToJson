package com.example.birthdaybanner.transactions.csvmodel


data class TransactionCsvItem(
    val activityCode: String?,
    val activityDate: String?,
    val activityType: String?,
    val affiliateName: String?,
    val amPoints: Int?,
    val content: String?,
    val contentEng: String?,
    val flight: String?,
    val flightClass: String?,
    val pmaType: String?,
    val pnrFlight: String?,
    val qualificablePoints: Int?,
    val recordLocator: String?,
    val redemptionDate: String?,
    val route: String?,
    val ticketNumberTrans: String?
)