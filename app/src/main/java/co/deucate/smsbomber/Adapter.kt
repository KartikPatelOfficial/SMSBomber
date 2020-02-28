package co.deucate.smsbomber

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import co.deucate.smsbomber.model.History

import java.util.ArrayList

class Adapter(private var histories: ArrayList<History>) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    var listener: OnClickCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_text, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = histories[position]

        val title = "${data.name} (${data.number})"

        holder.indexTV.text = (histories.indexOf(data) + 1).toString()
        holder.nameTv.text = title
        holder.detailTV.text = data.time

        holder.cardView.setOnClickListener {
            listener!!.onClickCard(data)
        }

    }

    override fun getItemCount(): Int {
        return histories.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView = itemView.findViewById<LinearLayout>(R.id.recyclerCard)!!
        val indexTV = itemView.findViewById<TextView>(R.id.recyclerIndex)!!
        val nameTv = itemView.findViewById<TextView>(R.id.recyclerName)!!
        val detailTV = itemView.findViewById<TextView>(R.id.recyclerDetail)!!
    }

    interface OnClickCallback {
        fun onClickCard(history: History)
    }

}
