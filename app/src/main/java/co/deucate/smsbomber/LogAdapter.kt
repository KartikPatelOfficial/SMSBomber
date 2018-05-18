package co.deucate.smsbomber

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import java.util.ArrayList

class LogAdapter(var logs: ArrayList<String>) : RecyclerView.Adapter<LogAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_text, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogAdapter.ViewHolder, position: Int) {
        val log = logs[position]
        holder.mTextView.text = log
    }

    override fun getItemCount(): Int {
        return logs.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var mTextView: TextView = itemView.findViewById(R.id.recyclerTV)

    }
}
