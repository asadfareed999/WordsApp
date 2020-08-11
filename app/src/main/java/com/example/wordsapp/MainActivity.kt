package com.example.wordsapp

import android.R.string
import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils.split
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.wordsapp.prefrences.WordsAppPreferences
import com.google.android.material.textfield.TextInputLayout
import org.greenrobot.eventbus.EventBus


class MainActivity : AppCompatActivity() {

    private lateinit var wordsAppPreferences: WordsAppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)*/
        setContentView(R.layout.activity_main)
        wordsAppPreferences= WordsAppPreferences(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        // return true so that the menu pop up is opened
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.enterURL) {
            val dialog = Dialog(this,R.style.Theme_Dialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setContentView(R.layout.dialog_enter_url)
            val textFieldURL = dialog.findViewById(R.id.tf_url) as TextInputLayout
            val textFieldSheetNo = dialog.findViewById(R.id.tf_sheet_no) as TextInputLayout
            val buttonSearch = dialog.findViewById(R.id.btn_search) as Button
            val url=wordsAppPreferences.url
            val sheetNo=wordsAppPreferences.sheetNO
            if (url.isNotEmpty() && sheetNo.isNotEmpty()){
                textFieldURL.editText!!.setText(url)
                textFieldSheetNo.editText!!.setText(sheetNo)
            }
            buttonSearch.setOnClickListener {
                val url=textFieldURL.editText!!.text.toString()
                val sheetNo=textFieldSheetNo.editText!!.text.toString()
                if (url.isEmpty()){
                    textFieldURL.error="Text Field Can not be empty"
                }else if (sheetNo.isEmpty()){
                    textFieldSheetNo.error="Text Field Can not be empty"
                    textFieldURL.isErrorEnabled=false
                }else{
                    textFieldURL.isErrorEnabled=false
                    textFieldSheetNo.isErrorEnabled=false
                    val id:String=extractID(url)
                    if (id.isEmpty()){
                        textFieldURL.error="Invalid URL"
                    }else{
                        textFieldURL.editText!!.setText(id)
                        val prefix="https://sheets.googleapis.com/v4/spreadsheets/"
                        val suffix="/values/"
                        val sheetNO=sheetNo
                        val key="?key=AIzaSyAfIcH-UbiuxC6jkj4BboVZISmNQtSO5co"
                        val newUrl=prefix.plus(id).plus(suffix).plus(sheetNO).plus(key)
                        wordsAppPreferences.url= url
                        wordsAppPreferences.sheetNO= sheetNo
                        EventBus.getDefault().post(UrlEvent(newUrl))
                        dialog.dismiss()
                    }

                    //Toast.makeText(this,"All Good",Toast.LENGTH_LONG).show()
                }

            }
            dialog.show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun extractID(url: String): String {
        /*val answer =
            url.substring(url.indexOf("/d/") + 1, url.indexOf("/edit"))*/

        val parts: List<String> = url.split("spreadsheets/d/")
        if (parts.size==2) {
            val result: String
            result = if (parts[1].contains("/")) {
                val parts2 = parts[1].split("/").toTypedArray()
                parts2[0]
            } else {
                parts[1]
            }
            return result
        }else{
            return ""
        }
    }


}
