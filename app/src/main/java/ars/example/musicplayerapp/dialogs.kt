package ars.example.musicplayerapp

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable


class dialogs(private val context: Context) {

    private val dialog  = Dialog(context).apply {
        setContentView(R.layout.dialog_layout)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(false)
    }
    var isActive = false

    fun showDialog(){
        dialog.show()
        isActive = true
    }
    fun stopDialog(){
        dialog.dismiss()
        isActive= false
    }


}