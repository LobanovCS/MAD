package com.police.patrol.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.police.patrol.R
import com.police.patrol.data.model.ActiveCase

class ActiveCasesAdapter(
    private var cases: List<ActiveCase>,
    private val onItemClick: (ActiveCase) -> Unit) :
    RecyclerView.Adapter<ActiveCasesAdapter.CaseViewHolder>() {

    inner class CaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val description: TextView = itemView.findViewById(R.id.tvDescription)
        val coords: TextView = itemView.findViewById(R.id.tvCoords)
        val callTime: TextView = itemView.findViewById(R.id.tvCallTime)

        init {
            itemView.setOnClickListener {
                onItemClick(cases[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CaseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_active_case, parent, false)
        return CaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CaseViewHolder, position: Int) {
        val item = cases[position]
        holder.description.text = item.description
        holder.coords.text = "Lat: ${item.latitude}, Lon: ${item.longitude}"
        holder.callTime.text = item.call_time
    }

    override fun getItemCount(): Int = cases.size

    fun updateData(newData: List<ActiveCase>) {
        cases = newData
        notifyDataSetChanged()
    }
}
