package com.police.patrol.ui.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.police.patrol.R

class RegisterPatrolActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = LayoutInflater.from(this)
        val view: View = inflater.inflate(R.layout.activity_register_patrol, contentFrame, false)
        contentFrame.addView(view)

        // Работа с полями регистрации через findViewById
    }
}
