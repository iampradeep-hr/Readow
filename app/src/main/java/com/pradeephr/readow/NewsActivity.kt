package com.pradeephr.readow

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pradeephr.readow.adapter.CustomPbar
import com.pradeephr.readow.adapter.NewsAdapter
import com.pradeephr.readow.api.DatabaseHelper
import com.pradeephr.readow.api.RssService
import com.pradeephr.readow.api.SavedArticles
import com.pradeephr.readow.databinding.ActivityNewsBinding
import com.pradeephr.readow.model.ReadLaterModel
import com.pradeephr.readow.model.RssFeed
import com.pradeephr.readow.model.RssItem
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding
    private lateinit var pbar: CustomPbar
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var link: String
    private var articles = mutableListOf<RssItem>()
    private lateinit var dataAdapter: NewsAdapter
    private lateinit var rv: RecyclerView
    private lateinit var dialog:Dialog
    private var pos=0
    private lateinit var savedArticles: SavedArticles


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_newsLytTheme)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializer()
        recyclerViewInit()

        link = intent.getStringExtra("Link").toString()
        pbar.showPbar()
        CoroutineScope(Main).launch {
            withContext(CoroutineScope(IO).coroutineContext) {
                fetchNews()
            }
        }
    }

    private fun recyclerViewInit() {
        rv.layoutManager = LinearLayoutManager(this)
        dataAdapter = NewsAdapter(this@NewsActivity, articles)
        rv.adapter = dataAdapter
        val viewManager=LinearLayoutManager(this@NewsActivity,LinearLayoutManager.VERTICAL,false)
        rv.apply {
            setHasFixedSize(true)
            layoutManager=viewManager
            val dividerItemDecoration=DividerItemDecoration(context,DividerItemDecoration.VERTICAL)
            ContextCompat.getDrawable(this@NewsActivity,R.drawable.divider)
                ?.let { dividerItemDecoration.setDrawable(it) }
            addItemDecoration(dividerItemDecoration)
        }

        val simpleCallback=object: ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        pos=viewHolder.adapterPosition
                        addToReadLater()
                    }
                    ItemTouchHelper.RIGHT -> {
                        pos=viewHolder.adapterPosition
                        val intent= Intent(this@NewsActivity,WebActivity::class.java)
                        intent.putExtra("loadUrl", articles[pos].link)
                        dataAdapter.notifyDataSetChanged() //to revert back the swiped item
                        startActivity(intent)
                    }
                }


            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(this@NewsActivity, R.color.white))
                    .addSwipeLeftActionIcon(R.drawable.ic_archive2)
                    .addSwipeRightActionIcon(R.drawable.ic_open6)
                    .addSwipeRightLabel("Read Full Article")
                    .addSwipeLeftLabel("Read Later")
                    .create()
                    .decorate()
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        val itemTouchHelper= ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(rv)

    }

    private fun fetchNews() {
        val rssService=RssService.retrofit.create(RssService::class.java)
        val call=rssService.getFeed(link)
        call.enqueue(object:Callback<RssFeed>{
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<RssFeed>, response: Response<RssFeed>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()!!
                    articles.addAll(apiResponse.channel.item)
                    dataAdapter.notifyDataSetChanged()
                }
                pbar.hidePbar()
            }
            override fun onFailure(call: Call<RssFeed>, t: Throwable) {
                pbar.hidePbar()
                failureDialog()
            }
        })
    }


    private fun initializer() {
        pbar = CustomPbar(this)
        dialog=Dialog(this)
        databaseHelper = DatabaseHelper(this)
        rv = binding.newsRecyclerView
        savedArticles= SavedArticles(this)
    }

    @SuppressLint("SetTextI18n")
    fun failureDialog() {
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.success_dialog)
        dialog.window?.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        val btnOk = dialog.findViewById<Button>(R.id.buttonOk)
        val btnHome = dialog.findViewById<Button>(R.id.buttonHome)
        val textView = dialog.findViewById<TextView>(R.id.tvDialog)
        val imageView = dialog.findViewById<ImageView>(R.id.ivDialog)
        imageView.setImageResource(R.drawable.ic_error404)
        btnOk.isVisible=false
        textView.text = "Network Error"
        btnHome.text="Close"
        dialog.show()
        btnHome.setOnClickListener { dialog.dismiss();finish() }
    }

    @SuppressLint("SetTextI18n")
    fun addToReadLater() {
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.success_dialog)
        dialog.window?.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        val btnOk = dialog.findViewById<Button>(R.id.buttonOk)
        val btnHome = dialog.findViewById<Button>(R.id.buttonHome)
        val textView = dialog.findViewById<TextView>(R.id.tvDialog)
        val imageView = dialog.findViewById<ImageView>(R.id.ivDialog)
        imageView.setImageResource(R.drawable.ic_archive2)
        textView.text = "Do you want to add this to Read Later...?"
        btnHome.text="No"
        btnOk.text="Yes"
        dialog.show()
        btnHome.setOnClickListener { dialog.dismiss();dataAdapter.notifyDataSetChanged() }
        btnOk.setOnClickListener {
            CoroutineScope(Main).launch{
                var flag=false
                val readLaterModel = ReadLaterModel()
                CoroutineScope(IO).async {
                    val savedArticles = SavedArticles(this@NewsActivity)
                    readLaterModel.articleTitle = articles[pos].title
                    readLaterModel.articleLink = articles[pos].link
                    readLaterModel.articlePubDate = articles[pos].pubDate
                    flag=savedArticles.checkifExists(readLaterModel)
                }.await()
                if (!flag){
                    savedArticles.addOne(readLaterModel)
                    dataAdapter.notifyDataSetChanged()
                }else{
                    dataAdapter.notifyDataSetChanged()
                }
                dialog.dismiss()
            }

        }
    }

}





