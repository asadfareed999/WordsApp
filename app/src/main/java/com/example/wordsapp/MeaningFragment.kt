package com.example.wordsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.wordsapp.networking.responsemodels.AllWords
import com.example.wordsapp.networking.responsemodels.Word
import com.example.wordsapp.prefrences.WordsAppPreferences
import com.orm.SugarRecord
import java.util.*


class MeaningFragment() : Fragment() {


    private lateinit var sequenceWordsList: ArrayList<Word>
    private lateinit var textViewWord:TextView
    private lateinit var textViewMeaning:TextView
    private lateinit var buttonPrevious: Button
    private lateinit var buttonHome: Button
    private lateinit var buttonNext: Button
    private lateinit var wordsAppPreferences: WordsAppPreferences
    private var index=0
    private var lastIndex=0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_meaning, container, false)
        initViews(view)
        clickListeners(view)
        val args: MeaningFragmentArgs by navArgs()
        val array=args.wordArray
        val word1=array[0]
        val meaning1=array[1]
        if (word1.isNotEmpty() && meaning1.isNotEmpty()){
            textViewWord.text = word1
            textViewMeaning.text = meaning1
            buttonNext.visibility = View.INVISIBLE
            buttonPrevious.visibility = View.INVISIBLE
            textViewWord.visibility = View.VISIBLE
            textViewMeaning.visibility = View.VISIBLE
        }else {
            val sortingType = wordsAppPreferences.getSortingType()
            if (sortingType.equals("Show Only Marked")) {
                fetchList()
                setUpUI("No selected Words Found")
            } else {
                fetchAllList()
                setUpUI("No Words Found")
            }
        }
        return view
    }

    private fun setUpUI(s: String) {
        if (sequenceWordsList.size > 0) {
            lastIndex = sequenceWordsList.size
            textViewWord.text = sequenceWordsList.get(index).word
            textViewMeaning.text = sequenceWordsList.get(index).meaning
            buttonNext.visibility = View.VISIBLE
            buttonPrevious.visibility = View.VISIBLE
            textViewWord.visibility = View.VISIBLE
            textViewMeaning.visibility = View.VISIBLE
        } else {
            Toast.makeText(requireActivity(), s, Toast.LENGTH_LONG).show()
            buttonNext.visibility = View.INVISIBLE
            buttonPrevious.visibility = View.INVISIBLE
            textViewWord.visibility = View.INVISIBLE
            textViewMeaning.visibility = View.INVISIBLE
        }
    }

    private fun fetchAllList() {
        val _words: List<AllWords> = SugarRecord.listAll(AllWords::class.java)
        val wordsList: ArrayList<Word> = ArrayList(_words.size)
        for (x in 0 until _words.size) {
            val word = _words.get(x).word.toString()
            val meaning = _words.get(x).meaning.toString()
            val selected: Boolean = _words.get(x).selected!!
            val singleWord: Word = Word(word, meaning, selected)
            wordsList.add(singleWord)
        }
        //wordsList.removeAt(0)
        sequenceWordsList = wordsList
    }

    private fun fetchList() {
        val _words: List<AllWords> = SugarRecord.listAll(AllWords::class.java)
        val wordsList: ArrayList<Word> = ArrayList(_words.size)
        val selectedWordsList = getSelectedWordsList(SugarRecord.listAll(Word::class.java))
        val size=selectedWordsList.size
        for (x in 0 until _words.size) {
            val word = _words.get(x).word.toString()
            val meaning = _words.get(x).meaning.toString()
            var seleted: Boolean?
            if (selectedWordsList.contains(word)) {
                seleted = true
                val singleWord: Word = Word(word, meaning, seleted)
                wordsList.add(singleWord)
            }
        }
        //wordsList.removeAt(0)
        sequenceWordsList = wordsList
    }

    private fun getSelectedWordsList(words: List<Word>):ArrayList<String> {
        val wordsList: ArrayList<String> = ArrayList(words.size)
        for (x in 0 until words.size) {
            val word = words.get(x).word.toString()
            wordsList.add(word)
        }
        return wordsList
    }


    private fun clickListeners(view: View) {

        buttonNext.setOnClickListener{
            index++
            if (index<lastIndex){
                textViewWord.text=sequenceWordsList.get(index).word
                textViewMeaning.text=sequenceWordsList.get(index).meaning
            }else{
                index=0
                textViewWord.text=sequenceWordsList.get(index).word
                textViewMeaning.text=sequenceWordsList.get(index).meaning
            }
        }
        buttonPrevious.setOnClickListener {
            index--
            if (index>-1){
                textViewWord.text=sequenceWordsList.get(index).word
                textViewMeaning.text=sequenceWordsList.get(index).meaning
            }else{
                index=lastIndex-1
                textViewWord.text=sequenceWordsList.get(index).word
                textViewMeaning.text=sequenceWordsList.get(index).meaning
            }
        }
        buttonHome.setOnClickListener {
            val action = MeaningFragmentDirections.actionMeaningFragmentToHomeFragment()
            view.findNavController().navigate(action)
        }
    }

    private fun initViews(view: View) {
        textViewWord = view.findViewById(R.id.tv_word)
        textViewMeaning = view.findViewById(R.id.tv_meaning)
        buttonPrevious=view.findViewById(R.id.btn_previous)
        buttonHome=view.findViewById(R.id.btn_home)
        buttonNext=view.findViewById(R.id.btn_next)
        wordsAppPreferences=WordsAppPreferences(view.context)
    }
}


