package com.gagapps.medadh.loadingClass

import android.app.Activity
import android.app.AlertDialog
import com.gagapps.medadh.R

class LoadingDialog(val mActivity: Activity) {
    private lateinit var isDialog: AlertDialog

    fun startLoading() {
        //set view
        val inflater = mActivity.layoutInflater
        val dialogView = inflater.inflate(R.layout.loading_bar, null)

        //set dialog
        val builder = AlertDialog.Builder(mActivity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isDialog = builder.create()
        isDialog.show()
    }
    fun isDismiss() {
        isDialog.dismiss()
    }
}