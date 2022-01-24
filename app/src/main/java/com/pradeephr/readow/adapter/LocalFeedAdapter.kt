package com.pradeephr.readow.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.pradeephr.readow.NewsActivity
import com.pradeephr.readow.R
import com.pradeephr.readow.model.DbModelSql

// adapter for the recycler view on Home page
class LocalFeedAdapter(val context: Context,val data:List<DbModelSql>): RecyclerView.Adapter<LocalFeedAdapter.LocalFeedViewHolder>() {

    inner class LocalFeedViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val tvName=itemView.findViewById<TextView>(R.id.tvAgencyName)
        val tvCategory=itemView.findViewById<TextView>(R.id.tvAgencyCategory)
        val tvLink=itemView.findViewById<TextView>(R.id.tvAgencyLink)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalFeedViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.local_feed_rvcard,parent,false)
        return LocalFeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocalFeedViewHolder, position: Int) {
        val item=data[position]
        holder.tvName.text=item.agencyName
        holder.tvCategory.text=item.agencyCategory
        holder.tvLink.text=item.agencyLink

        holder.itemView.setOnClickListener {
            val intent= Intent(context,NewsActivity::class.java)
            intent.putExtra("Link", holder.tvLink.text)
            startActivity(context,intent,null)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

}