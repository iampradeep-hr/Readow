package com.pradeephr.readow.adapter

import android.app.Dialog
import android.content.Context
import com.pradeephr.readow.R

class CustomPbar(context: Context) {
    private var dialog:Dialog = Dialog(context)

    init {
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
    fun showPbar(){
        dialog.setContentView(R.layout.custom_pbar)
        dialog.show()
    }
    fun hidePbar(){
        dialog.dismiss()
    }

}