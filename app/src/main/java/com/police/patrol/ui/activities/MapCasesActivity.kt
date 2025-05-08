package com.police.patrol.ui.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.police.patrol.R
import com.police.patrol.data.model.*
import com.police.patrol.ui.client.BaseActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapCasesActivity : BaseActivity(), com.google.android.gms.maps.OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var currentPatrolId: Int = -1
    private var hasActiveCase: Boolean = false
    private var incomingCase: ActiveCase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_cases)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map)
        if (mapFragment is SupportMapFragment) {
            mapFragment.getMapAsync(this)
        } else {
            Toast.makeText(this, "Карта не найдена", Toast.LENGTH_SHORT).show()
        }

        // Сохраняем данные кейса, если они пришли через интент
        if (intent.hasExtra("case_id")) {
            incomingCase = ActiveCase(
                intent.getIntExtra("case_id", -1),
                intent.getDoubleExtra("latitude", 0.0),
                intent.getDoubleExtra("longitude", 0.0),
                intent.getStringExtra("description") ?: "",
                intent.getStringExtra("call_time") ?: "",
                null
            )
        }

        currentPatrolId = getSharedPreferences("prefs", MODE_PRIVATE).getInt("patrol_id", -1)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(55.751244, 37.618423), 10f))

        loadPatrolUnits()
        loadActiveCases()

        incomingCase?.let { case ->
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(case.latitude, case.longitude), 15f))
            showCaseDialog(case)
        }
    }

    private fun loadPatrolUnits() {
        RetrofitClient.apiService.getAllPatrols().enqueue(object : Callback<List<Patrol>> {
            override fun onResponse(call: Call<List<Patrol>>, response: Response<List<Patrol>>) {
                val patrols = response.body() ?: return
                for (unit in patrols) {
                    if (unit.latitude != null && unit.longitude != null && unit.isActive) {
                        val marker = MarkerOptions()
                            .position(LatLng(unit.latitude, unit.longitude))
                            .title("Патруль: ${unit.name}")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        mMap.addMarker(marker)
                    }
                    if (unit.id == currentPatrolId) {
                        hasActiveCase = true
                    }
                }
            }

            override fun onFailure(call: Call<List<Patrol>>, t: Throwable) {
                Toast.makeText(this@MapCasesActivity, "Ошибка загрузки отрядов", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadActiveCases() {
        RetrofitClient.apiService.getActiveCases().enqueue(object : Callback<List<ActiveCase>> {
            override fun onResponse(call: Call<List<ActiveCase>>, response: Response<List<ActiveCase>>) {
                val cases = response.body() ?: return
                for (c in cases) {
                    val marker = mMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(c.latitude, c.longitude))
                            .title("Дело №${c.case_id}")
                            .snippet(c.description)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    )
                    marker?.tag = c
                }

                mMap.setOnInfoWindowClickListener { marker ->
                    val activeCase = marker.tag as? ActiveCase ?: return@setOnInfoWindowClickListener
                    showCaseDialog(activeCase)
                }
            }

            override fun onFailure(call: Call<List<ActiveCase>>, t: Throwable) {
                Toast.makeText(this@MapCasesActivity, "Ошибка загрузки дел", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showCaseDialog(activeCase: ActiveCase) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_case_actions, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        dialogView.findViewById<TextView>(R.id.tvCaseInfo).text =
            "Дело №${activeCase.case_id}\n${activeCase.description}\nВремя: ${activeCase.call_time}"

        dialogView.findViewById<Button>(R.id.btnTakeCase).setOnClickListener {
            if (hasActiveCase) {
                Toast.makeText(this, "У вас уже есть активные дела", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                return@setOnClickListener
            }
            assignCaseToPatrol(activeCase.case_id)
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun assignCaseToPatrol(caseId: Int) {
        if (currentPatrolId == -1) {
            Toast.makeText(this, "Наряд не найден", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.apiService.assignCase(caseId, currentPatrolId).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MapCasesActivity, "Дело назначено", Toast.LENGTH_SHORT).show()
                    hasActiveCase = true
                } else {
                    Toast.makeText(this@MapCasesActivity, "Ошибка назначения", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                Toast.makeText(this@MapCasesActivity, "Сервер не отвечает", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
