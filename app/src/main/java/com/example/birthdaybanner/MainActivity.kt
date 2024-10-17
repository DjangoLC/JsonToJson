package com.example.birthdaybanner

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.birthdaybanner.transactions.TransactionReader

const val LIMIT_BEFORE_ADJUST = 20

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val reader = TransactionReader(this)
        reader.parseInfo()
    }
}

