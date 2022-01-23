package com.pradeephr.readow

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pradeephr.readow.adapter.CustomPbar
import com.pradeephr.readow.adapter.SavedArticlesAdapter
import com.pradeephr.readow.api.SavedArticles
import com.pradeephr.readow.databinding.ActivityReadLaterBinding
import com.pradeephr.readow.model.ReadLaterModel
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.coroutines.*

class ReadLaterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReadLaterBinding
    private lateinit var rv:RecyclerView
    private lateinit var pbar: CustomPbar
    private lateinit var dialog: Dialog
    private lateinit var listToPass:List<ReadLaterModel>
    private var pos=0
    private lateinit var savedArticles: SavedArticles
    private lateinit var myAdapter: SavedArticlesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityReadLaterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initializer()
        recyclerViewInit()
        adapterRv()
    }


    private fun recyclerViewInit() {
        val viewManager= LinearLayoutManager(this@ReadLaterActivity, LinearLayoutManager.VERTICAL,false)
        rv.apply {
            setHasFixedSize(true)
            layoutManager=viewManager
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }

        val simpleCallback=object: ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                pos=viewHolder.adapterPosition
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        pos=viewHolder.adapterPosition
                       warningDialog()
                    }
                    ItemTouchHelper.RIGHT -> {

                        val intent= Intent(this@ReadLaterActivity,WebActivity::class.java)
                        intent.putExtra("loadUrl", listToPass[pos].articleLink)
                        myAdapter.notifyDataSetChanged() //to revert back the swiped item
                        startActivity(intent)
                    }
                }

            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(this@ReadLaterActivity, R.color.white))
                    .addSwipeLeftActionIcon(R.drawable.ic_dustbin)
                    .addSwipeRightActionIcon(R.drawable.ic_open6)
                    .addSwipeRightLabel("Read Full Article")
                    .addSwipeLeftLabel("Delete")
                    .create()
                    .decorate()
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        val itemTouchHelper= ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(rv)
    }



    private fun adapterRv() {
        pbar.showPbar()
        CoroutineScope(Dispatchers.Main).launch {
            withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                listToPass = savedArticles.readAll()
                pbar.hidePbar()
            }
            myAdapter= SavedArticlesAdapter(this@ReadLaterActivity,listToPass)
            rv.adapter=myAdapter
        }
    }


    private fun initializer() {
        rv=binding.readLaterRecyclerView
        pbar= CustomPbar(this@ReadLaterActivity)
        dialog=Dialog(this)
        savedArticles= SavedArticles(this)
    }

    @SuppressLint("SetTextI18n")
    fun warningDialog(){
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.success_dialog)
        dialog.window?.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        val btnOk = dialog.findViewById<Button>(R.id.buttonOk)
        val btnHome = dialog.findViewById<Button>(R.id.buttonHome)
        val textView = dialog.findViewById<TextView>(R.id.tvDialog)
        val imageView = dialog.findViewById<ImageView>(R.id.ivDialog)
        btnOk.text = "Delete"
        imageView.setImageResource(R.drawable.ic_dustbin)
        textView.text = "Are you sure, You want to delete this item ?"
        dialog.show()
        btnOk.setOnClickListener {
            val readLaterModel=ReadLaterModel()
            readLaterModel.dataId=listToPass[pos].dataId
            savedArticles.deleteOne(readLaterModel)
            adapterRv()
            dialog.dismiss() }
        btnHome.text = "Cancel"
        btnHome.setOnClickListener { dialog.dismiss();
        adapterRv() }
    }


}