package com.example.goldit.notifiedAlerts

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.goldit.databinding.NotifiedAlertItemBinding
import com.example.goldit.databinding.NotifiedAlertItemHighlightedBinding
import com.example.goldit.room.Alert
import java.text.SimpleDateFormat

private const val ALERT_VIEW = 1
private const val HIGHLIGHTED_ALERT_VIEW = 2

private val sdf = SimpleDateFormat("dd-MM-yyyy")

class NotificationAdapter(val clickListener: NotifiedAlertListener) : ListAdapter<Alert, RecyclerView.ViewHolder>(achievedAlertsDiffUtil()) {

    class alertView(val binding: NotifiedAlertItemBinding) : RecyclerView.ViewHolder(binding.root) {}

    class alertHighlightedView(val binding: NotifiedAlertItemHighlightedBinding) : RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == HIGHLIGHTED_ALERT_VIEW) {
            alertHighlightedView(NotifiedAlertItemHighlightedBinding.inflate(LayoutInflater.from(parent.context)))
        } else {
            alertView(NotifiedAlertItemBinding.inflate(LayoutInflater.from(parent.context)))
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is alertView) {
            holder.binding.notificationItem = getItem(position)
            holder.binding.clickListener = clickListener
            holder.binding.executePendingBindings()
        }

        if (holder is alertHighlightedView) {
            holder.binding.notificationItem = getItem(position)
            holder.binding.clickListener = clickListener
            holder.binding.executePendingBindings()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (sdf.format(getItem(position).achievedDateTime) == sdf.format(System.currentTimeMillis())) {
            HIGHLIGHTED_ALERT_VIEW
        } else {
            ALERT_VIEW
        }
    }

}

class achievedAlertsDiffUtil() : DiffUtil.ItemCallback<Alert>() {

    override fun areItemsTheSame(oldItem: Alert, newItem: Alert): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Alert, newItem: Alert): Boolean {
        return oldItem == newItem
    }

}

class NotifiedAlertListener(val clickListener: (alertId:Long) -> Unit) {
    fun onClick(alert :Alert) = clickListener(alert.id)
}

@BindingAdapter("formattedDateTime")
fun TextView.formatDateTime(time: Long) {
    val sdf = SimpleDateFormat("dd-MM-yyyy")
    text = sdf.format(time)
}