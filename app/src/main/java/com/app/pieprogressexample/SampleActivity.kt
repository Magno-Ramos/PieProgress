package com.app.pieprogressexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_sample.*

class SampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        btn_plus.setOnClickListener { increase() }
        btn_minus.setOnClickListener { decrease() }
    }

    private fun increase() = updatePercentage(10f)

    private fun decrease() = updatePercentage(-10f)

    private fun updatePercentage(percentage: Float) {
        progress1.setPercentage(progress1.currentPercentage + percentage)
        progress2.setPercentage(progress2.currentPercentage + percentage)
        progress3.setPercentage(progress3.currentPercentage + percentage)
    }
}