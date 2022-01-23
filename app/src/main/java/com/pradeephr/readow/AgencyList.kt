package com.pradeephr.readow

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.pradeephr.readow.adapter.CustomPbar
import com.pradeephr.readow.api.DatabaseHelper
import com.pradeephr.readow.api.ServiceForAgency
import com.pradeephr.readow.databinding.ActivityAgencyListBinding
import com.pradeephr.readow.model.DbModelSql
import com.pradeephr.readow.model.RssAgency
import com.pradeephr.readow.model.RssNamesandLinks
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AgencyList : AppCompatActivity() {
    private lateinit var binding: ActivityAgencyListBinding
    private lateinit var spinner1: Spinner
    private lateinit var spinner2: Spinner
    private lateinit var data1: MutableList<String>
    private lateinit var data2: MutableList<String>
    private lateinit var data3: MutableList<String>
    private lateinit var adapter1: ArrayAdapter<String>
    private lateinit var adapter2: ArrayAdapter<String>
    private lateinit var btnSave: Button
    private var selectedAgency: String = ""
    private lateinit var dbModelSql: DbModelSql
    private lateinit var pbar: CustomPbar
    private lateinit var dialog:Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgencyListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializer() //initialize and set views



        pbar.showPbar()
        CoroutineScope(Main).launch {
            adapter1 = ArrayAdapter<String>(this@AgencyList, android.R.layout.simple_list_item_activated_1, data1)
            adapter2 = ArrayAdapter<String>(this@AgencyList, android.R.layout.simple_list_item_activated_1, data2)
            spinner1.adapter = adapter1
            spinner2.adapter = adapter2
            withContext(CoroutineScope(IO).coroutineContext) {
                fetchAgencyList()
            }
        } //launches to fetch data for sp1

        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedAgency = data1[position]
                dbModelSql.agencyName = data1[position]
                if (selectedAgency!="--Choose here--"){
                    pbar.showPbar()
                    CoroutineScope(Main).launch {
                        withContext(CoroutineScope(IO).coroutineContext) {
                            fetchSelectedAgencyLinks()
                        }
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        spinner2.onItemSelectedListener=object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (data3[position]!="--Choose here"){
                    dbModelSql.agencyCategory=data2[position]
                    dbModelSql.agencyLink=data3[position]
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        btnSave.setOnClickListener {
            if ((dbModelSql.agencyName!="--Choose here--")&&(dbModelSql.agencyCategory!="--Choose here--")){
                var b=false
                var i=false
                CoroutineScope(Main).launch{
                    CoroutineScope(IO).async {
                        val databaseHelper=DatabaseHelper(this@AgencyList)
                        if (!databaseHelper.checkifExists(dbModelSql)){
                            b=databaseHelper.addOne(dbModelSql)
                        }else{
                            i=true
                        }
                    } .await()
                    if (b){
                        successDialog()
                    }else if(i){
                        alreadyExistDialog()
                    }else{
                        alreadyExistDialog()
                    }
                }
            }else{
                warningDialog()
            }
        }

    }







    private fun initializer() {
        btnSave = binding.btnSave
        spinner1 = binding.sp1
        spinner2 = binding.sp2
        pbar = CustomPbar(this)
        data1 = mutableListOf()
        data2 = mutableListOf()
        data3 = mutableListOf()
        dbModelSql = DbModelSql()
        dialog=Dialog(this)
    }


    private fun fetchAgencyList() {
        val res = ServiceForAgency.retrofitInstance.getAgencyList()
        res.enqueue(object : Callback<RssAgency> {
            override fun onResponse(call: Call<RssAgency>, response: Response<RssAgency>) {
                if (response.isSuccessful) {
                    data1.add("--Choose here--")
                    data1.addAll(response.body()!!.names)
                    adapter1.notifyDataSetChanged()
                    pbar.hidePbar()
                }
            }
            override fun onFailure(call: Call<RssAgency>, t: Throwable) {
                pbar.hidePbar()
                failureDialog()
            }
        })
    }

    private fun fetchSelectedAgencyLinks() {
        val res =ServiceForAgency.retrofitInstance.getSelectedAgencyNamesandLinks(selectedAgency)
        res.enqueue(object : Callback<RssNamesandLinks> {
            override fun onResponse(
                call: Call<RssNamesandLinks>,
                response: Response<RssNamesandLinks>
            ) {
                if (response.isSuccessful) {
                    data2.clear()
                    data3.clear()
                    data2.add("--Choose here--")
                    data3.add("--Choose here--")
                    data2.addAll(response.body()?.names!!)
                    data3.addAll(response.body()?.links!!)
                    adapter2.notifyDataSetChanged()
                }
                pbar.hidePbar()
            }
            override fun onFailure(call: Call<RssNamesandLinks>, t: Throwable) {
                pbar.hidePbar()
                failureDialog()
            }
        })
    }






    private fun successDialog() {
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.success_dialog)
        dialog.window?.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        val btnOk = dialog.findViewById<Button>(R.id.buttonOk)
        val btnHome = dialog.findViewById<Button>(R.id.buttonHome)
        dialog.show()
        btnOk.setOnClickListener { dialog.dismiss() }
        btnHome.setOnClickListener { dialog.dismiss();finish() }
    }

    @SuppressLint("SetTextI18n")
    fun failureDialog() {
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.success_dialog)
        dialog.window?.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        val btnOk = dialog.findViewById<Button>(R.id.buttonOk)
        val btnHome = dialog.findViewById<Button>(R.id.buttonHome)
        val textView = dialog.findViewById<TextView>(R.id.tvDialog)
        val imageView = dialog.findViewById<ImageView>(R.id.ivDialog)
        imageView.setImageResource(R.drawable.ic_error404)
        textView.text = "Network Error"
        dialog.show()
        btnOk.setOnClickListener {
            dialog.dismiss();finish() }
        btnHome.setOnClickListener { dialog.dismiss();finish() }
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
        btnHome.isVisible=false
        btnOk.setText("Close")
        imageView.setImageResource(R.drawable.ic_warning)
        textView.text = "Invalid"
        dialog.show()
        btnOk.setOnClickListener { dialog.dismiss() }
    }

    @SuppressLint("SetTextI18n")
    fun alreadyExistDialog(){
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.success_dialog)
        dialog.window?.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        val btnOk = dialog.findViewById<Button>(R.id.buttonOk)
        val btnHome = dialog.findViewById<Button>(R.id.buttonHome)
        val textView = dialog.findViewById<TextView>(R.id.tvDialog)
        val imageView = dialog.findViewById<ImageView>(R.id.ivDialog)
        btnHome.isVisible=false
        btnOk.setText("Ok")
        imageView.setImageResource(R.drawable.ic_database)
        textView.text = "Data already exists"
        dialog.show()
        btnOk.setOnClickListener { dialog.dismiss() }
    }

}