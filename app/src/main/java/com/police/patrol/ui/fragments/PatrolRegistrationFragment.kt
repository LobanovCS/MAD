package com.police.patrol.ui.fragments

import RegisterRequest
import ToggleRequest
import ToggleResponse
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.police.patrol.R
import com.police.patrol.data.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PatrolRegistrationFragment : Fragment() {

    private lateinit var etPatrolName: EditText
    private lateinit var etCarNumber: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnToggleStatus: Button

    private var isActive = false
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val sharedPrefs by lazy {
        requireContext().getSharedPreferences("patrol_prefs", Context.MODE_PRIVATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_patrol_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        etPatrolName = view.findViewById(R.id.et_patrol_name)
        etCarNumber = view.findViewById(R.id.et_car_number)
        btnRegister = view.findViewById(R.id.btn_register_patrol)
        btnToggleStatus = view.findViewById(R.id.btn_toggle_status)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        btnRegister.setOnClickListener {
            val name = etPatrolName.text.toString().trim()
            val car = etCarNumber.text.toString().trim().takeIf { it.isNotEmpty() }

            if (name.isEmpty()) {
                Toast.makeText(context, "Введите ФИО", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = RegisterRequest(name, car)
            RetrofitClient.apiService.registerPatrol(request).enqueue(object : Callback<Map<String, Any>> {
                override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                    Toast.makeText(context, "Наряд зарегистрирован", Toast.LENGTH_SHORT).show()
                    Log.d("SERVER", "Response: ${response.body()}")
                    requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
                        .edit()
                        .putInt("patrol_id", id)
                        .apply()
                }

                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    Toast.makeText(context, "Ошибка: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                    Log.e("SERVER", "Request failed: ${t.localizedMessage}")
                }
            })
        }

        btnToggleStatus.setOnClickListener {
            val officerName = etPatrolName.text.toString().trim()

            if (officerName.isEmpty()) {
                Toast.makeText(context, "Введите ФИО для смены статуса", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            getLocationAndToggleStatus(officerName)
        }
    }

    private fun getLocationAndToggleStatus(officerName: String) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        val toggleRequest = ToggleRequest(officerName)
        RetrofitClient.apiService.toggleStatus(toggleRequest).enqueue(object : Callback<ToggleResponse> {
            override fun onResponse(call: Call<ToggleResponse>, response: Response<ToggleResponse>) {
                val result = response.body()
                if (result != null && result.success) {
                    isActive = result.new_status == 1
                    Toast.makeText(context, "Статус: ${if (isActive) "Активен" else "Неактивен"}", Toast.LENGTH_SHORT).show()

                    if (isActive) {
                        result.patrol_id?.let {
                            savePatrolIdToPrefs(it)
                            Log.d("PATROL_ID", "Сохранён ID отряда: $it")
                        }
                        startLocationUpdates(officerName)
                    } else {
                        stopLocationUpdates()
                    }
                }
            }

            override fun onFailure(call: Call<ToggleResponse>, t: Throwable) {
                Toast.makeText(context, "Ошибка: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun startLocationUpdates(officerName: String) {
        handler.post(object : Runnable {
            override fun run() {
                if (isActive) {
                    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                        return
                    }

                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            val request = ToggleRequest(
                                officer_names = officerName,
                                latitude = location.latitude,
                                longitude = location.longitude
                            )

                            RetrofitClient.apiService.updateLocation(request).enqueue(object : Callback<Map<String, Any>> {
                                override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                                    Log.d("SERVER", "Location updated: ${response.body()}")
                                }

                                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                                    Log.e("SERVER", "Error updating location: ${t.localizedMessage}")
                                }
                            })
                        }
                    }
                    handler.postDelayed(this, 30000)
                }
            }
        })
    }

    private fun stopLocationUpdates() {
        handler.removeCallbacksAndMessages(null)
    }

    private fun savePatrolIdToPrefs(id: Int) {
        sharedPrefs.edit().putInt("patrol_id", id).apply()
    }

    fun getSavedPatrolId(): Int? {
        val id = sharedPrefs.getInt("patrol_id", -1)
        return if (id != -1) id else null
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val officerName = etPatrolName.text.toString().trim()
            if (officerName.isNotEmpty()) {
                getLocationAndToggleStatus(officerName)
            }
        }
    }
}
