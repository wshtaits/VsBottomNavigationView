package com.example.vsbottomnavigationview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_sample.*
import kotlinx.android.synthetic.main.item_sample_specific.*

class SampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)
        vs_bottom_navigation_view.setItemAnimator(SampleItemAnimator(this))
        vs_bottom_navigation_view.setItemAnimatorForId(R.id.action_battery, SpecificSampleItemAnimator(this))
        charge_tv.text = "82%"
    }
}
