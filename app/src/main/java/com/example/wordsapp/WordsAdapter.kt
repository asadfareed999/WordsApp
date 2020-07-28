package com.example.wordsapp

import android.provider.ContactsContract.CommonDataKinds.Note
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wordsapp.networking.responsemodels.Word
import com.orm.SugarRecord
import com.orm.SugarRecord.find


class WordsAdapter(values: ArrayList<Word>) :
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
        holder.bindItems(wordsList.get(position))
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return wordsList.size
    }

    //the class is holding the list view
    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val textViewWord:TextView=itemView.findViewById(R.id.tv_list_word)
        private val checkBoxWord=itemView.findViewById<CheckBox>(R.id.cb_list)
        private lateinit var _word: Word

        fun bindItems(word: Word) {
                _word=word
                textViewWord.text= word.word
                if (word.selected!!){
                    checkBoxWord.isChecked=true
                    checkBoxWord.isEnabled=false
                }
                checkBoxWord.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
           if (!_word.selected!!) {
               val word = Word(_word.word, _word.meaning, true)
               word.save()
               v!!.tag=word.id.toString()
               checkBoxWord.isEnabled=false
           }
           /* }else{
                val id=
                val word: Word = SugarRecord.findWithQuery(Word::class.java, "Delete from Word where word = ?", _word.word)
                word.delete()
            }*/
        }
    }
}