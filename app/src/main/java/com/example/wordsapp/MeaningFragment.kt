package com.example.wordsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.wordsapp.networking.responsemodels.Word
import com.orm.SugarRecord
import com.orm.SugarRecord.listAll


class MeaningFragment() : Fragment() {


    private lateinit var textViewWord:TextView
    private lateinit var textViewMeaning:TextView
    private lateinit var textViewNext:TextView
    private lateinit var textViewHome:TextView
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

        textViewNext.setOnClickListener{
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
        textViewHome.setOnClickListener {
            view.findNavController().navigate(R.id.homeFragment)
        }
    }

    private fun initViews(view: View) {
        textViewWord = view.findViewById(R.id.tv_word)
        textViewMeaning = view.findViewById(R.id.tv_meaning)
        textViewNext = view.findViewById(R.id.tv_next)
        textViewHome = view.findViewById(R.id.tv_home)
    }
}


