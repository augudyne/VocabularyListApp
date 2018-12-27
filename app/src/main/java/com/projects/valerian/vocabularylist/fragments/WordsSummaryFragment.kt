package com.projects.valerian.vocabularylist.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.projects.valerian.vocabularylist.R
import com.projects.valerian.vocabularylist.dagger.ViewModelFactory
import com.projects.valerian.vocabularylist.models.Word
import com.projects.valerian.vocabularylist.viewmodel.WordsViewModel
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_summary_word.view.*
import kotlinx.android.synthetic.main.words_summary_fragment.*
import javax.inject.Inject

class WordsSummaryFragment : Fragment() {

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: WordsViewModel
    private var disposables: CompositeDisposable = CompositeDisposable()
    private val adapter = WordAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.words_summary_fragment, container, false)
    }

    class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private fun reset() = itemView.run {
            txt_wordId.text = context.getString(R.string.default_word_id)
            txt_wordSummary.text = context.getString(R.string.default_word_summary)
        }

        fun setWord(word: Word?) {
            reset()
            word?.let {
                itemView.txt_wordId.text = it.id
                itemView.txt_wordSummary.text = it.getSummary()
            }
        }
    }

    inner class WordAdapter : RecyclerView.Adapter<WordViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder =
            WordViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_summary_word,
                    parent,
                    false
                )
            )

        override fun getItemCount(): Int {
            Log.d(TAG, "ItemCount: ${viewModel.getWordsForUser(context!!)}")
            return viewModel.getWordCountForUser()
        }

        override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
            viewModel.getWordAtPosition(position)?.let {
                holder.setWord(it)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        rv_words.run {
            (viewModel.getWordsForUser(context, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d(TAG, "Received new data ${it.joinToString()}")
                    Log.d(TAG, "new data size: ${viewModel.getWordCountForUser()}")
                    this@WordsSummaryFragment.adapter.notifyDataSetChanged()
                }, {
                    Log.e(this::class.java.canonicalName, it.message)
                })).also {
                disposables.add(it)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        AndroidSupportInjection.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(WordsViewModel::class.java)
        rv_words.layoutManager = LinearLayoutManager(this.context)
        rv_words.adapter = adapter
    }

    companion object {
        fun newInstance() = WordsSummaryFragment()
        private const val TAG = "WordsSummaryFragment"
    }
}
