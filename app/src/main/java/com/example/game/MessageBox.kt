package com.example.game

import android.app.AlertDialog
import android.content.Context

class MessageBox {
    fun show(activity: Context, title: String?, message: String?, endCall: () -> Unit?) {
        dialog = AlertDialog.Builder(activity) // Pass a reference to your main activity here
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", { dialogInterface, i ->
                    if (endCall!= null)
                        endCall()
                    this.dialog!!.cancel()})
                .show()
    }
    fun showYesNo(activity: Context, title: String?, message: String?, endYesCall: () -> Unit?, endNoCall:()->Unit?) {
        dialog = AlertDialog.Builder(activity) // Pass a reference to your main activity here
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(R.string.msgbox_notagree,
                        {  dialogInterface, i->
                            if (endNoCall!= null)
                                endNoCall()
                           this.dialog!!.cancel()
                        })
                .setPositiveButton(R.string.msgbox_yes, { dialogInterface, i ->
                    if (endYesCall!= null)
                        endYesCall()
                    this.dialog!!.cancel()})
                .show()
    }
    fun showModal(activity: Context, title: String?, message: String?) {
        dialog = AlertDialog.Builder(activity) // Pass a reference to your main activity here
                .setTitle(title)
                .setMessage(message)
                .show()
    }
    fun cancel() {
        dialog?.cancel()
    }
    public var dialog: AlertDialog? = null
}
