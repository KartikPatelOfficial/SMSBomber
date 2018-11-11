package co.deucate.smsbomber

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import java.util.ArrayList

class LogAdapter(private var logs: ArrayList<String>) : RecyclerView.Adapter<LogAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_text, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogAdapter.ViewHolder, position: Int) {
        var log = logs[position]
        val firstThree = log[0].toString()+log[1].toString()+log[2].toString()
        val finalString: String

        when (firstThree) {
            "Err" -> {
                log = log.replace("Err","")
                log = log.replace("orcode","Errorcode")
                finalString = "> $log"
                holder.mTextView.setTextColor(Color.RED)
            }
            "???" -> {
                log = log.replace("???","")
                finalString = "> $log"
                holder.mTextView.setTextColor(Color.YELLOW)
            }
            else -> finalString = log
        }

        holder.mTextView.text = finalString
    }

    override fun getItemCount(): Int {
        return logs.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var mTextView: TextView = itemView.findViewById(R.id.recyclerTV)

    }
}
