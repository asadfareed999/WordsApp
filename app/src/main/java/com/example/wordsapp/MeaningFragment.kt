package com.example.wordsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.wordsapp.networking.responsemodels.Word
import com.orm.SugarRecord
import com.orm.SugarRecord.listAll


class MeaningFragment() : Fragment() {


    private lateinit var textViewWord:TextView
    private lateinit var textViewMeaning:TextView
    private lateinit var buttonPrevious: Button
    private lateinit var buttonHome: Button
    private lateinit var buttonNext: Button
    private lateinit var words:List<Word>
    private var index=0
    private var lastIndex=0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_meaning, container, false)
        initViews(view)
        clickListeners(view)
        words = SugarRecord.listAll(Word::class.java)
        lastIndex=words.size
        textViewWord.text=words.get(index).word
        textViewMeaning.text=words.get(index).meaning
        return view
    }


    private fun clickListeners(view: View) {

        buttonNext.setOnClickListener{
            index++
            if (index<lastIndex){
                textViewWord.text=words.get(index).word
                textViewMeaning.text=words.get(index).meaning
            }else{
                index=0
                textViewWord.text=words.get(index).word
                textViewMeaning.text=words.get(index).meaning
            }
        }
        buttonPrevious.setOnClickListener {
            index--
            if (index>-1){
                textViewWord.text=words.get(index).word
                textViewMeaning.text=words.get(index).meaning
            }else{
                index=lastIndex-1
                textViewWord.text=words.get(index).word
                textViewMeaning.text=words.get(index).meaning
            }
        }
        buttonHome.setOnClickListener {
            view.findNavController().navigate(R.id.homeFragment)
        }
    }

    private fun initViews(view: View) {
        textViewWord = view.findViewById(R.id.tv_word)
        textViewMeaning = view.findViewById(R.id.tv_meaning)
        buttonPrevious=view.findViewById(R.id.btn_previous)
        buttonHome=view.findViewById(R.id.btn_home)
        buttonNext=view.findViewById(R.id.btn_next)
    }
}


