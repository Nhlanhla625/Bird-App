package com.example.birdzone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.birdzone.Model.Observation
import com.example.birdzone.R

class ObservationAdapter : ListAdapter<Observation, ObservationAdapter.ObservationViewHolder>(ObservationDiffCallback()) { // Add missing colon

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObservationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_saved_observation, parent, false)
        return ObservationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ObservationViewHolder, position: Int) {
        val observation = getItem(position)
        holder.bind(observation)
    }

    inner class ObservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val speciesTextView: TextView = itemView.findViewById(R.id.textViewSpecies)
        private val dateTextView: TextView = itemView.findViewById(R.id.textViewDate)
        private val timeTextView: TextView = itemView.findViewById(R.id.textViewTime)
        private val locationTextView: TextView = itemView.findViewById(R.id.textViewLocation)
        private val notesTextView: TextView = itemView.findViewById(R.id.textViewNotes)

        fun bind(observation: Observation) {
            speciesTextView.text = "Bird Species: ${observation.species}"
            dateTextView.text = "Observation Date: ${observation.date}"
            timeTextView.text = "Observation Time: ${observation.time}"
            locationTextView.text = "Observation Location: ${observation.location}"
            notesTextView.text = "Observation Notes: ${observation.notes}"
        }
    }

    class ObservationDiffCallback : DiffUtil.ItemCallback<Observation>() {
        override fun areItemsTheSame(oldItem: Observation, newItem: Observation): Boolean {
            return oldItem.species == newItem.species
        }

        override fun areContentsTheSame(oldItem: Observation, newItem: Observation): Boolean {
            return oldItem == newItem
        }
    }
}
