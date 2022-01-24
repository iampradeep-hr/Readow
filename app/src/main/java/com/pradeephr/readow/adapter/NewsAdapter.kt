package com.pradeephr.readow.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pradeephr.readow.R
import com.pradeephr.readow.model.RssItem


class NewsAdapter(val context: Context, val items: List<RssItem>) :
    RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var newsTitle = view.findViewById<TextView>(R.id.newsTitle)
        val newsDateandTime=view.findViewById<TextView>(R.id.newsTimeandDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.news_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.newsTitle.text = item.title.trim()
        holder.newsDateandTime.text=item.pubDate.trim()
    }


    override fun getItemCount(): Int {
        return items.size
    }

}
