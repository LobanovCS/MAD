package com.police.patrol.ui.client

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.police.patrol.R
import com.police.patrol.ui.activities.MapCasesActivity

abstract class BaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    protected lateinit var contentFrame: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawer = findViewById<androidx.drawerlayout.widget.DrawerLayout>(R.id.drawerLayout)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navView = findViewById<NavigationView>(R.id.navView)
        navView.setNavigationItemSelectedListener(this)

        contentFrame = findViewById(R.id.content_frame)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                if (this !is HomeActivity) {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
            }
            R.id.nav_register_patrol -> {
                if (this !is RegisterPatrolActivity) {
                    startActivity(Intent(this, RegisterPatrolActivity::class.java))
                    finish()
                }
            }
            R.id.nav_map_cases -> {
                if (this !is MapCasesActivity) {
                    startActivity(Intent(this, MapCasesActivity::class.java))
                    finish()
                }
            }
            R.id.nav_active_cases -> {
                if (this !is ActiveCasesActivity) {
                    startActivity(Intent(this, ActiveCasesActivity::class.java))
                    finish()
                }
            }
            R.id.nav_complete_case -> {
                if (this !is CompleteCaseActivity) {
                    startActivity(Intent(this, CompleteCaseActivity::class.java))
                    finish()
                }
            }
        }
        findViewById<androidx.drawerlayout.widget.DrawerLayout>(R.id.drawerLayout)
            .closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        val drawer = findViewById<androidx.drawerlayout.widget.DrawerLayout>(R.id.drawerLayout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
