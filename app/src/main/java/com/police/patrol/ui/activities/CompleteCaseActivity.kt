package com.police.patrol.ui.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.police.patrol.R

class CompleteCaseActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = LayoutInflater.from(this)
        val view: View = inflater.inflate(R.layout.activity_complete_case, contentFrame, false)
        contentFrame.addView(view)

        // Заполнение завершенных дел
    }
}
