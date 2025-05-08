package com.police.patrol.ui.client

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.cardview.widget.CardView
import com.police.patrol.R
import com.police.patrol.ui.activities.MapCasesActivity

class HomeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = LayoutInflater.from(this)
        val view: View = inflater.inflate(R.layout.activity_home, contentFrame, false)
        contentFrame.addView(view)

        val cardActiveCases = view.findViewById<CardView>(R.id.card_active_cases)
        val cardCompleteCase = view.findViewById<CardView>(R.id.card_complete_case)
        val cardMapCases = view.findViewById<CardView>(R.id.card_map_cases)
        val cardRegisterPatrol = view.findViewById<CardView>(R.id.card_register_patrol)

        cardActiveCases.setOnClickListener {
            startActivity(Intent(this, ActiveCasesActivity::class.java))
        }

        cardCompleteCase.setOnClickListener {
            startActivity(Intent(this, CompleteCaseActivity::class.java))
        }

        cardMapCases.setOnClickListener {
            startActivity(Intent(this, MapCasesActivity::class.java))
        }

        cardRegisterPatrol.setOnClickListener {
            startActivity(Intent(this, RegisterPatrolActivity::class.java))
        }
    }
}
