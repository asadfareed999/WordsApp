package com.example.wordsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wordsapp.R
import com.example.wordsapp.networking.responsemodels.Word
import com.orm.SugarRecord
import com.orm.SugarRecord.findById


class WordsAdapter(values: List<List<String>>) :
    RecyclerView.Adapter<WordsAdapter.ViewHolder>() {

    private val wordsList=values

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_list_word
                , parent, false
            )
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var index=position
        index++
        holder.bindItems(wordsList.get(index))
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        var size=wordsList.size
        size--
        return size
    }

    //the class is holding the list view
    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val textViewWord:TextView=itemView.findViewById(R.id.tv_list_word)
        private val checkBoxWord=itemView.findViewById<CheckBox>(R.id.cb_list)
        private lateinit var _word: List<String>

        fun bindItems(word: List<String>) {
                _word=word
                textViewWord.text= word[0]
                checkBoxWord.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
           // if (!checkBoxWord.isChecked){
                val word=Word(_word[0],_word[1])
                word.save()
            /*}else{
                *//*val word: Word = SugarRecord.findById(Word::class.java, 1)
                word.delete()*//*
            }*/
        }
    }
}