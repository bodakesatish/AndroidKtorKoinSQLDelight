package com.bodakesatish.ktor.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bodakesatish.ktor.R
import com.bodakesatish.ktor.domain.model.SchemeModel

class MFSchemeAdapter(private var schemes: List<SchemeModel>) :
    RecyclerView.Adapter<MFSchemeAdapter.MFSchemeViewHolder>() {

    class MFSchemeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val schemeCodeTextView: TextView = view.findViewById(R.id.schemeCodeTextView)
        val schemeNameTextView: TextView = view.findViewById(R.id.schemeNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MFSchemeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mf_scheme, parent, false)
        return MFSchemeViewHolder(view)
    }

    override fun onBindViewHolder(holder: MFSchemeViewHolder, position: Int) {
        val scheme = schemes[position]
        holder.schemeCodeTextView.text = "Code: ${scheme.schemeCode}"
        holder.schemeNameTextView.text = "Name: ${scheme.schemeName}"
    }

    override fun getItemCount(): Int = schemes.size

    fun updateSchemes(newSchemes: List<SchemeModel>) {
        schemes = newSchemes
        notifyDataSetChanged()
    }
}