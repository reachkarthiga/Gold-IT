package com.example.goldit.home

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.goldit.R
import com.example.goldit.databinding.AlertRecyclerviewItemBinding
import com.example.goldit.room.Alert

class AlertAdapter(val clickListener: ActiveAlertsListener) : ListAdapter<Alert, RecyclerView.ViewHolder>(alertsDiffUtilCallBack()) {

    class ViewHolder(val binding: AlertRecyclerviewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflate(parent: ViewGroup) :ViewHolder {
                return ViewHolder(AlertRecyclerviewItemBinding.inflate(LayoutInflater.from(parent.context)))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  ViewHolder.inflate(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is AlertAdapter.ViewHolder) {
            holder.binding.alertRate = getItem(position)
            holder.binding.clickListener = clickListener
            holder.binding.executePendingBindings()
        }

    }

}

class ActiveAlertsListener(val clickListener: (alertId: Long) -> Unit) {
    fun onClick(alert: Alert) = clickListener(alert.id)
}

class alertsDiffUtilCallBack() : DiffUtil.ItemCallback<Alert>() {
    override fun areItemsTheSame(oldItem: Alert, newItem: Alert): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Alert, newItem: Alert): Boolean {
        return oldItem == newItem
    }

}
