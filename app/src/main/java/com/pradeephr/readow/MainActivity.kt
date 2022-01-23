package com.pradeephr.readow

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pradeephr.readow.adapter.CustomPbar
import com.pradeephr.readow.adapter.LocalFeedAdapter
import com.pradeephr.readow.api.DatabaseHelper
import com.pradeephr.readow.databinding.ActivityMainBinding
import com.pradeephr.readow.model.DbModelSql
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageViewAdd:ImageView
    private lateinit var rv:RecyclerView
    private lateinit var dbModelSql: DbModelSql
    private lateinit var listToPass:List<DbModelSql>
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var pbar: CustomPbar
    private lateinit var myAdapter: LocalFeedAdapter
    private lateinit var dialog: Dialog
    private var pos=0


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_myCustomTheme)
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializer()
        recyclerViewInit()



        imageViewAdd.setOnClickListener {
            val intent = Intent(this, AgencyList::class.java)
            startActivity(intent)
        }


    }

    override fun onResume() {
        super.onResume()
        adapterRv()

    }

    private fun recyclerViewInit() {
        val viewManager=LinearLayoutManager(this@MainActivity,LinearLayoutManager.VERTICAL,false)
        rv.apply {
            setHasFixedSize(true)
            layoutManager=viewManager
            addItemDecoration(DividerItemDecoration(this.context,DividerItemDecoration.VERTICAL))
        }

        val simpleCallback=object:ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
               return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                pos=viewHolder.adapterPosition
                warningDialog()
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                  .addActionIcon(R.drawable.ic_dustbin)
                    .create()
                    .decorate()
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        val itemTouchHelper=ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(rv)
    }



    private fun adapterRv() {
        pbar.showPbar()
        CoroutineScope(Main).launch {
            withContext(CoroutineScope(IO).coroutineContext) {
                listToPass = databaseHelper.readAll()
                pbar.hidePbar()
            }
            myAdapter=LocalFeedAdapter(this@MainActivity,listToPass)
            rv.adapter=myAdapter
        }
    }


    private fun initializer() {
        imageViewAdd=binding.imgAdd
        rv=binding.localFeedRow
        dbModelSql= DbModelSql()
        listToPass= mutableListOf()
        databaseHelper= DatabaseHelper(this@MainActivity)
        pbar= CustomPbar(this@MainActivity)
        dialog=Dialog(this)
    }


    @SuppressLint("SetTextI18n")
    fun warningDialog(){
        dialog.setCancelable(true)
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
            val dbModelSql=DbModelSql()
            dbModelSql.dataId=listToPass[pos].dataId
            databaseHelper.deleteOne(dbModelSql)
            adapterRv()
            dialog.dismiss() }
        btnHome.text = "Cancel"
        btnHome.setOnClickListener { dialog.dismiss();recreate() }
    }



}