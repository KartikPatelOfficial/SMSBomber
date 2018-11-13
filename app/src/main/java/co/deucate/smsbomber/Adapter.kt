package co.deucate.smsbomber

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import java.util.ArrayList

class Adapter(private var histories: ArrayList<Data>) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Adapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_text, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: Adapter.ViewHolder, position: Int) {

        val history = histories[position]

        val title = "${history.name} (${history.number})"

        holder.indexTV.text = (histories.indexOf(history) + 1).toString()
        holder.nameTv.text = title
        holder.detailTV.text = history.time

    }

    override fun getItemCount(): Int {
        return histories.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val indexTV = itemView.findViewById<TextView>(R.id.recyclerIndex)!!
        val nameTv = itemView.findViewById<TextView>(R.id.recyclerName)!!
        val detailTV = itemView.findViewById<TextView>(R.id.recyclerDetail)!!
    }
}
