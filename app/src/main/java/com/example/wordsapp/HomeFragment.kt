package com.example.wordsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


class HomeFragment() : Fragment() {

    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WordsAdapter
    private val apiEndpointClient = BaseWebservices.getApiEndpointClient()
    private lateinit var getDataApiListener: OnResponseListener<ResponseWords>
    private lateinit var wordsAppPreferences: WordsAppPreferences
    private lateinit var textViewUpdate:TextView
    private lateinit var textViewMeanings:TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        wordsAppPreferences = WordsAppPreferences(view.context)
        initViews(view)
        initApiListener(view)
        clickListeners(view)
        swipeRefresh.isRefreshing = true
        val words:List<AllWords> = SugarRecord.listAll(AllWords::class.java)
        val size=words.size
        if (words.isEmpty()){
            exeOrdersApi()
        }else{
            val wordsList: List<List<String>> = arrayListOf()
            for (x in 0 until words.size){
                val word=words.get(x).word.toString()
                val meaning=words.get(x).meaning.toString()
                val singleWord: List<String> = arrayListOf(word,meaning)
                wordsList.plusElement(singleWord)
            }
            //val word=words.get(0).word
            //val meaning=words.get(1).meaning
           setUpRecyclerview(wordsList)
            swipeRefresh.isRefreshing = false
        }
        return view
    }

    private fun clearDatabase() {
        SugarRecord.deleteAll(AllWords::class.java)
    }

    private fun initApiListener(view: View) {
        getDataApiListener = object : OnResponseListener<ResponseWords> {
            override fun onSuccess(response: ResponseWords?) {
                val wordsResponse: ResponseWords = response!!
                clearDatabase()
                val words=wordsResponse.values
                for (x in 0 until words.size){
                    val word=AllWords(words[x][0], words[x][1])
                    word.save()
                }

                EnableViews()
                setUpRecyclerview(words)
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

    private fun setUpRecyclerview(words: List<List<String>>) {
        recyclerView.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        adapter = WordsAdapter(words)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun EnableViews() {
        swipeRefresh.isRefreshing = false
        progressBar.visibility = View.GONE
        textViewUpdate.isEnabled = true
        textViewMeanings.isEnabled = true
    }

    private fun exeOrdersApi() {
        progressBar.visibility=View.VISIBLE
        textViewUpdate.isEnabled=false
        textViewMeanings.isEnabled=false
        val call = apiEndpointClient.getData()
        BaseWebservices.executeApi(call, getDataApiListener)
    }

    private fun clickListeners(view: View) {

        swipeRefresh.setOnRefreshListener {
            exeOrdersApi()
        }

        textViewUpdate.setOnClickListener {
            exeOrdersApi()
        }

        textViewMeanings.setOnClickListener {
            val words:List<Word>  = SugarRecord.listAll(Word::class.java)
            if (words.size>0) {
                view.findNavController().navigate(R.id.meaningFragment)
            }else{
                Toast.makeText(requireActivity(),"Select Words First ",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initViews(view: View) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        recyclerView = view.findViewById(R.id.fragmentHomeRecyclerView)
        textViewUpdate = view.findViewById(R.id.tv_update)
        textViewMeanings = view.findViewById(R.id.tv_meanings)
        wordsAppPreferences = WordsAppPreferences(view.context)
        progressBar=requireActivity().findViewById(R.id.progress_bar)
    }

}


