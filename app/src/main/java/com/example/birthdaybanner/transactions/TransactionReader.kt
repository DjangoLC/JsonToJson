package com.example.birthdaybanner.transactions

import android.app.people.PeopleManager
import android.content.Context
import com.example.birthdaybanner.R
import com.example.birthdaybanner.transactions.csvmodel.TransactionCsvItem
import com.example.birthdaybanner.transactions.requetmodel.Description
import com.example.birthdaybanner.transactions.requetmodel.TransactionRequestItem
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.PrintWriter

class TransactionReader(private val context: Context) {

    // we are going to save key and values that were not found in request

    private fun readJsonFromRaw(context: Context, resourceId: Int): String {
        val inputStream = context.resources.openRawResource(resourceId)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        return bufferedReader.use { it.readText() }
    }

    private fun parseJsonCsv(): List<TransactionCsvItem> {
        val activityList = mutableListOf<TransactionCsvItem>()

        // Create a JSONArray from the input string
        val jsonArray = JSONArray(readJsonFromRaw(context, R.raw.csv))

        // Iterate over each JSON object in the array
        try {
            for (i in 0 until jsonArray.length()) {
                val jsonObject: JSONObject = jsonArray.getJSONObject(i)

                // Parse individual fields
                val activity = TransactionCsvItem(
                    activityCode = jsonObject.getString("activityCode"),
                    flight = jsonObject.optString(
                        "flight",
                        ""
                    ),  // Use optString to handle possible nulls
                    flightClass = jsonObject.optString("flightClass", ""),
                    route = jsonObject.optString("Route", ""),
                    activityDate = jsonObject.getString("activityDate"),
                    activityType = jsonObject.getString("activityType"),
                    pnrFlight = jsonObject.optString("pnrFlight", ""),
                    contentEng = jsonObject.getString("contentEng"),
                    affiliateName = jsonObject.getString("affiliateName"),
                    pmaType = jsonObject.getString("pmaType"),
                    recordLocator = jsonObject.optString("recordLocator", ""),
                    amPoints = jsonObject.getInt("amPoints"),
                    ticketNumberTrans = jsonObject.getString("ticketNumberTrans"),
                    qualificablePoints = jsonObject.getInt("qualificablePoints"),
                    redemptionDate = jsonObject.getString("redemptionDate"),
                    content = jsonObject.getString("content")
                )

                // Add the activity object to the list
                activityList.add(activity)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return activityList
    }

    private fun parseJsonRequest(): List<TransactionRequestItem> {
        val transactionList = mutableListOf<TransactionRequestItem>()

        val jsonArray = JSONArray(readJsonFromRaw(context, R.raw.endpoint))

        for (i in 0 until jsonArray.length()) {
            val jsonObject: JSONObject = jsonArray.getJSONObject(i)

            // Parse nested Description object
            val descriptionObject: JSONObject = jsonObject.getJSONObject("description")
            val description = Description(
                content = descriptionObject.optString("content", ""),
                contentEng = descriptionObject.optString("contentEng", ""),
                flight = descriptionObject.optString("flight", ""),
                flightClass = descriptionObject.optString("flightClass", ""),
                route = descriptionObject.optString("route", "")
            )

            // Parse TransactionRequestItem object
            val transactionItem = TransactionRequestItem(
                activityCode = jsonObject.optString("activityCode", ""),
                activityDate = jsonObject.optString("activityDate", ""),
                activityType = jsonObject.optString("activityType", ""),
                affiliateName = jsonObject.optString("affiliateName", ""),
                amPoints = jsonObject.optInt("amPoints", 0),
                description = description,
                pmaType = jsonObject.optString("pmaType", ""),
                pnrFlight = jsonObject.optString("pnrFlight", ""),
                qualificablePoints = jsonObject.optInt("qualificablePoints", 0),
                recordLocator = jsonObject.optString("recordLocator", ""),
                redemptionDate = jsonObject.optString("redemptionDate", ""),
                segmentsEarned = jsonObject.optInt("segmentsEarned", 0),
                ticketNumberTrans = jsonObject.optString("ticketNumberTrans", "")
            )

            transactionList.add(transactionItem)
        }

        return transactionList
    }

    fun parseInfo() {
        val csvFile = File(context.filesDir, "csv.txt")  // Internal storage file path
        val requestFile = File(context.filesDir, "request.txt")  // Internal storage file path
        val csvItems = parseJsonCsv().sortedWith(
            compareBy(
                { it.activityDate },
                { it.redemptionDate }
            )
        )
        val requestItems = parseJsonRequest().sortedWith(
            compareBy(
                { it.activityDate },
                { it.redemptionDate }
            )
        )
        PrintWriter(FileOutputStream(csvFile), true).use { writer ->
            writer.println(jsonCsvToString(csvItems))
        }
        PrintWriter(FileOutputStream(requestFile), true).use { writer ->
            writer.println(requestToJson(requestItems))
        }
    }

    fun jsonCsvToString(csvItems: List<TransactionCsvItem>): String {
        val jsonArray = StringBuilder("[")

        csvItems.forEachIndexed { index, item ->
            // Create the JSON object for each item
            val jsonObject = """
        {
            "activityCode": "${item.activityCode?.trim() ?: ""}",
            "activityDate": "${item.activityDate?.trim() ?: ""}",
            "activityType": "${item.activityType?.trim() ?: ""}",
            "affiliateName": "${item.affiliateName?.trim() ?: ""}",
            "amPoints": ${item.amPoints ?: 0},
            "content": "${item.content?.trim() ?: ""}",
            "contentEng": "${item.contentEng?.trim() ?: ""}",
            "flight": "${item.flight?.trim() ?: ""}",
            "flightClass": "${item.flightClass?.trim() ?: ""}",
            "pmaType": "${item.pmaType?.trim() ?: ""}",
            "pnrFlight": "${item.pnrFlight?.trim() ?: ""}",
            "qualificablePoints": ${item.qualificablePoints ?: 0},
            "recordLocator": "${item.recordLocator?.trim() ?: ""}",
            "redemptionDate": "${item.redemptionDate?.trim() ?: ""}",
            "route": "${item.route?.trim() ?: ""}",
            "ticketNumberTrans": "${item.ticketNumberTrans?.trim() ?: ""}"
        }
    """.trimIndent()

            // Add comma between objects except for the last one
            jsonArray.append(jsonObject)
            if (index != csvItems.size - 1) {
                jsonArray.append(",")
            }
        }

        jsonArray.append("]")
        return jsonArray.toString()
    }

    fun requestToJson(requestItems: List<TransactionRequestItem>): String {
// Convertir la lista a un JSON array manualmente
        val jsonArray = StringBuilder("[")

        requestItems.forEachIndexed { index, item ->
            // Crear el objeto JSON para cada item
            val jsonObject = """
        {
            "activityCode": "${item.activityCode?.trim() ?: ""}",
            "activityDate": "${item.activityDate?.trim() ?: ""}",
            "activityType": "${item.activityType?.trim() ?: ""}",
            "affiliateName": "${item.affiliateName?.trim() ?: ""}",
            "amPoints": ${item.amPoints ?: 0},
            "content": "${item.description?.content?.trim() ?: ""}",
            "contentEng": "${item.description?.contentEng?.trim() ?: ""}",
            "flight": "${item.description?.flight?.trim() ?: ""}",
            "flightClass": "${item.description?.flightClass?.trim() ?: ""}",
            "pmaType": "${item.pmaType?.trim() ?: ""}",
            "pnrFlight": "${item.pnrFlight?.trim() ?: ""}",
            "qualificablePoints": ${item.qualificablePoints ?: 0},
            "recordLocator": "${item.recordLocator?.trim() ?: ""}",
            "redemptionDate": "${item.redemptionDate?.trim() ?: ""}",
            "route": "${item.description?.route?.trim() ?: ""}",
            "ticketNumberTrans": "${item.ticketNumberTrans?.trim() ?: ""}"
        }
    """.trimIndent()

            // Agregar coma entre objetos excepto el último
            jsonArray.append(jsonObject)
            if (index != requestItems.size - 1) {
                jsonArray.append(",")
            }
        }

        jsonArray.append("]")

// Imprimir o usar el JSON resultante
        println(jsonArray.toString())
        return jsonArray.toString()
    }
}


fun main() {
    /*val bus = Bus(12)
    bus.addPerson("Enrique")
    bus.printSeatNames()
    bus.hasAvailableSeat()*/
}

class Bus(val seats: Int) {
    private val people = arrayOfNulls<Person>(seats)
    private var index = 11

    fun addPerson(name: String) {
        people[index] = Person(name)
    }

    fun addPerson(person: Person) {
        people[index] = person
    }

    fun printSeatNames() {
        for (person in people) {
            println(person!!.name)
        }
    }

    fun hasAvailableSeat(): Boolean {
        index = seats * 2
        var hasAvailable = false
        for (i in 0..index) {
            if (people[i] == null) {
                hasAvailable = true
            }
        }
        return hasAvailable
    }
}

class InvalidIndexException() : Exception()
class Person(val name: String = "")
