package com.example.goodtimestamp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.goodtimestamp.data.entity.DiaryEntry
import com.example.goodtimestamp.databinding.ItemDiaryBinding
import java.text.SimpleDateFormat
import java.util.*

class DiaryListAdapter(
    private val onItemClick: (DiaryEntry) -> Unit
) : ListAdapter<DiaryEntry, DiaryListAdapter.DiaryViewHolder>(DIFF_CALLBACK) {

    inner class DiaryViewHolder(private val binding: ItemDiaryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: DiaryEntry) {
            binding.apply {
                textTitle.text = entry.title
                textPreview.text = entry.content
                textDate.text = formatDate(entry.createDate)
                root.setOnClickListener { onItemClick(entry) }
            }
        }

        private fun formatDate(timestamp: Long): String {
            return SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                .format(Date(timestamp))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val binding = ItemDiaryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DiaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DiaryEntry>() {
            override fun areItemsTheSame(oldItem: DiaryEntry, newItem: DiaryEntry): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DiaryEntry, newItem: DiaryEntry): Boolean {
                return oldItem == newItem
            }
        }
    }
}