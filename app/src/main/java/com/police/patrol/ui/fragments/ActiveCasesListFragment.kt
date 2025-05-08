package com.police.patrol.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.police.patrol.R
import com.police.patrol.data.model.ActiveCase
import androidx.recyclerview.widget.LinearLayoutManager
import com.police.patrol.ui.activities.MapCasesActivity
import com.police.patrol.ui.adapters.ActiveCasesAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActiveCasesListFragment : Fragment() {

    private lateinit var adapter: ActiveCasesAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnRefresh: Button
    private var activeCases: List<ActiveCase> = listOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_active_cases_list, container, false)
        recyclerView = view.findViewById(R.id.rv_active_cases)
        btnRefresh = view.findViewById(R.id.btnRefresh)

        adapter = ActiveCasesAdapter(activeCases) { selectedCase ->
            val intent = Intent(requireContext(), MapCasesActivity::class.java)
            intent.putExtra("case_id", selectedCase.case_id)
            intent.putExtra("latitude", selectedCase.latitude)
            intent.putExtra("longitude", selectedCase.longitude)
            intent.putExtra("description", selectedCase.description)
            intent.putExtra("call_time", selectedCase.call_time)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        btnRefresh.setOnClickListener {
            fetchActiveCases()
        }

        fetchActiveCases()

        return view
    }

    private fun fetchActiveCases() {
        RetrofitClient.apiService.getActiveCases().enqueue(object : Callback<List<ActiveCase>> {
            override fun onResponse(call: Call<List<ActiveCase>>, response: Response<List<ActiveCase>>) {
                if (response.isSuccessful && response.body() != null) {
                    adapter.updateData(response.body()!!)
                }
            }

            override fun onFailure(call: Call<List<ActiveCase>>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
