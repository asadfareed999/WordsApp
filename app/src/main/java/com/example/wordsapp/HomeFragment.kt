package com.example.wordsapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.wordsapp.networking.BaseWebservices
import com.example.wordsapp.networking.OnResponseListener
import com.example.wordsapp.networking.responsemodels.AllWords
import com.example.wordsapp.networking.responsemodels.ResponseWords
import com.example.wordsapp.networking.responsemodels.Word
import com.example.wordsapp.prefrences.WordsAppPreferences
import com.google.android.material.snackbar.Snackbar
import com.orm.SugarRecord
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


class HomeFragment() : Fragment() {

    private lateinit var wordsList1: ArrayList<Word>
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WordsAdapter
    private val apiEndpointClient = BaseWebservices.getApiEndpointClient()
    private lateinit var getDataApiListener: OnResponseListener<ResponseWords>
    private lateinit var wordsAppPreferences: WordsAppPreferences
    private lateinit var textViewNoData:TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var allWordsList: ArrayList<Word>
    private lateinit var buttonUpdate:Button
    private lateinit var buttonMeanings:Button
    private lateinit var buttonSort:Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        //wordsAppPreferences = WordsAppPreferences(view.context)
        initViews(view)
        initApiListener(view)
        clickListeners(view)
        swipeRefresh.isRefreshing = true
        val sortingType=wordsAppPreferences.getSortingType()
        if (sortingType.equals("Show Only Marked")){
            val words:List<Word> = SugarRecord.listAll(Word::class.java)
            fetchSelectedWords(words)
            swipeRefresh.isRefreshing = false
            updateAllWordsList()
        }else {
            val words: List<AllWords> = SugarRecord.listAll(AllWords::class.java)
            val size = words.size
            if (words.isEmpty()) {
                exeOrdersApi()
            } else {
                fetchOfflineData(words)
                swipeRefresh.isRefreshing = false
            }
        }
        return view
    }

    private fun updateAllWordsList() {
        val _words: List<AllWords> = SugarRecord.listAll(AllWords::class.java)
        val wordsList: ArrayList<Word> = ArrayList(_words.size)
        val selectedWordsList = getSelectedWordsList(SugarRecord.listAll(Word::class.java))
        val size=selectedWordsList.size
        for (x in 0 until _words.size) {
            val word = _words.get(x).word.toString()
            val meaning = _words.get(x).meaning.toString()
            var seleted = _words.get(x).selected
            if (selectedWordsList.contains(word)) {
                seleted = true
            }
            val singleWord: Word = Word(word, meaning, seleted)
            wordsList.add(singleWord)
        }
        wordsList1=wordsList
        //wordsList.removeAt(0)
        allWordsList = wordsList
    }

    private fun fetchOfflineData(words: List<AllWords>) {
        /*val wordsList: ArrayList<Word> = ArrayList(words.size)
        val selectedWordsList=getSelectedWordsList(SugarRecord.listAll(Word::class.java))
        for (x in 0 until words.size) {
            val word = words.get(x).word.toString()
            val meaning = words.get(x).meaning.toString()
            var seleted=words.get(x).selected
            if (selectedWordsList.contains(word)){
                seleted=true
            }
            val singleWord: Word = Word(word, meaning,seleted)
            wordsList.add(singleWord)
        }
        //wordsList.removeAt(0)
        allWordsList=wordsList*/
        updateAllWordsList()
        if (wordsList1.isEmpty()){
            recyclerView.visibility=View.INVISIBLE
            textViewNoData.visibility=View.VISIBLE
        }else{
            recyclerView.visibility=View.VISIBLE
            textViewNoData.visibility=View.INVISIBLE
        }
        setUpRecyclerview(wordsList1)
    }

    private fun clearAllWordsTable() {
        SugarRecord.deleteAll(AllWords::class.java)
    }

    private fun initApiListener(view: View) {
        getDataApiListener = object : OnResponseListener<ResponseWords> {
            override fun onSuccess(response: ResponseWords?) {
                val wordsResponse: ResponseWords = response!!
                clearAllWordsTable()
                val words=wordsResponse.values
                for (x in 1 until words.size){
                    val word=AllWords(words[x][0], words[x][1],false)
                    word.save()
                }
                val wordsList:List<AllWords> = SugarRecord.listAll(AllWords::class.java)
                fetchOfflineData(wordsList)
                EnableViews()
                    // setUpRecyclerview(words)
            }

            override fun onFailure(t: Throwable) {
                EnableViews()
                Snackbar.make(view, t.message.toString(), Snackbar.LENGTH_LONG).show()
            }

            override fun onCancel() {
                EnableViews()
            }

        }
    }

    private fun setUpRecyclerview(words: ArrayList<Word>) {
        recyclerView.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        adapter = WordsAdapter(words)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun EnableViews() {
        swipeRefresh.isRefreshing = false
        progressBar.visibility = View.GONE
        buttonUpdate.isEnabled = true
        buttonMeanings.isEnabled = true
    }

    private fun exeOrdersApi() {
        progressBar.visibility=View.VISIBLE
        buttonUpdate.isEnabled=false
        buttonMeanings.isEnabled=false
        val call = apiEndpointClient.getData()
        BaseWebservices.executeApi(call, getDataApiListener)
    }

    private fun clickListeners(view: View) {

        swipeRefresh.setOnRefreshListener {
          exeOrdersApi()
        }

        buttonUpdate.setOnClickListener {
            exeOrdersApi()
        }

        buttonMeanings.setOnClickListener {
            val words:List<Word>  = SugarRecord.listAll(Word::class.java)
            if (words.size>0) {
                val action = HomeFragmentDirections.actionHomeFragmentToMeaningFragment()
                view.findNavController().navigate(action)
               // view.findNavController().navigate(R.id.meaningFragment)
            }else{
                Toast.makeText(requireActivity(),"Select Words First ",Toast.LENGTH_LONG).show()
            }
        }

        buttonSort.setOnClickListener {
            showDialog()
        }
    }

    fun showDialog() {
        val sortingTypes = arrayOf( "Alphabetically","Randomly", "Show Only Marked")
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.str_sorting_type)
        builder.setItems(sortingTypes, DialogInterface.OnClickListener {
                dialog, which ->
            // 'which' item of the 'dialog' was selected
            Toast.makeText(requireActivity(), sortingTypes[which],
                Toast.LENGTH_SHORT).show()
            updateAllWordsList()
            val allWords:ArrayList<Word> = allWordsList
            if (which==0){
                recyclerView.visibility=View.VISIBLE
                textViewNoData.visibility=View.INVISIBLE
                sortData(allWords)
                wordsAppPreferences.setSortingType("Alphabetically")
            }else if (which==1){
                recyclerView.visibility=View.VISIBLE
                textViewNoData.visibility=View.INVISIBLE
                shuffleData(allWords)
                wordsAppPreferences.setSortingType("Randomly")
            }else if (which==2){
               /*val words:List<Word> = SugarRecord.listAll(Word::class.java)
                fetchSelectedWords(words)*/
                wordsAppPreferences.setSortingType("Show Only Marked")
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
                if (wordsList.isEmpty()){
                    recyclerView.visibility=View.INVISIBLE
                    textViewNoData.visibility=View.VISIBLE
                }else{
                    recyclerView.visibility=View.VISIBLE
                    textViewNoData.visibility=View.INVISIBLE
                }
                setUpRecyclerview(wordsList)
            }
        })
        builder.show()
    }

    private fun fetchSelectedWords(words: List<Word>) {
        val wordsList: ArrayList<Word> = ArrayList(words.size)
        for (x in 0 until words.size) {
            val word = words.get(x).word.toString()
            val meaning = words.get(x).meaning.toString()
            val seleted=words.get(x).selected
            val singleWord: Word = Word(word, meaning,seleted)
            wordsList.add(singleWord)
        }
        if (wordsList.isEmpty()){
            recyclerView.visibility=View.INVISIBLE
            textViewNoData.visibility=View.VISIBLE
        }else{
            recyclerView.visibility=View.VISIBLE
            textViewNoData.visibility=View.INVISIBLE
        }
        setUpRecyclerview(wordsList)
    }

    private fun getSelectedWordsList(words: List<Word>):ArrayList<String> {
        val wordsList: ArrayList<String> = ArrayList(words.size)
        for (x in 0 until words.size) {
            val word = words.get(x).word.toString()
            wordsList.add(word)
        }
        return wordsList
    }

    private fun shuffleData(list: java.util.ArrayList<Word>) {
        list.shuffle()
        setUpRecyclerview(list)
        clearAllWordsTable()
        for (x in 0 until list.size){
            val word=AllWords(list.get(x).word, list.get(x).meaning,list.get(x).selected)
            word.save()
        }
    }

    private fun sortData(list: ArrayList<Word>) {
        Collections.sort(list, object : Comparator<Word> {
            override fun compare(o1: Word?, o2: Word?): Int {
                val s1 = o1!!.word
                val s2 = o2!!.word
                return s1!!.compareTo(s2!!, ignoreCase = true)            }
        })
        setUpRecyclerview(list)
        clearAllWordsTable()
        for (x in 0 until list.size){
            val word=AllWords(list.get(x).word, list.get(x).meaning,list.get(x).selected)
            word.save()
        }
    }

    private fun initViews(view: View) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        recyclerView = view.findViewById(R.id.fragmentHomeRecyclerView)
        textViewNoData = view.findViewById(R.id.tv_no_data)
        wordsAppPreferences = WordsAppPreferences(view.context)
        progressBar=requireActivity().findViewById(R.id.progress_bar)

        buttonUpdate=view.findViewById(R.id.btn_update)
        buttonMeanings=view.findViewById(R.id.btn_meanings)
        buttonSort=view.findViewById(R.id.btn_sort)


    }

}


