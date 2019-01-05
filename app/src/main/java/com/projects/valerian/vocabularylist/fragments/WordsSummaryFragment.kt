package com.projects.valerian.vocabularylist.fragments

import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchUIUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.projects.valerian.vocabularylist.R
import com.projects.valerian.vocabularylist.WordDetailActivity
import com.projects.valerian.vocabularylist.dagger.ViewModelFactory
import com.projects.valerian.vocabularylist.models.Word
import com.projects.valerian.vocabularylist.setVisible
import com.projects.valerian.vocabularylist.singletons.UserStore
import com.projects.valerian.vocabularylist.viewmodel.WordsViewModel
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_summary_word.view.*
import kotlinx.android.synthetic.main.words_summary_fragment.*
import retrofit2.HttpException
import javax.inject.Inject

class WordsSummaryFragment : Fragment() {

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    @Inject
    internal lateinit var userStore: UserStore

    private lateinit var viewModel: WordsViewModel
    private var disposables: CompositeDisposable = CompositeDisposable()
    private val adapter = WordAdapter()

    private fun updateWords() {
        val itemTouchHelper = ItemTouchHelper(WordItemCallback())
        itemTouchHelper.attachToRecyclerView(rv_words)
        rv_words.run {
            (viewModel.getWordsForUser(context, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d(TAG, "Received new data ${it.joinToString()}")
                    Log.d(TAG, "new data size: ${viewModel.getWordCountForUser()}")
                    if (it.isEmpty()) {
                        lyt_empty_view.setVisible(true)
                    } else {
                        this@WordsSummaryFragment.adapter.notifyDataSetChanged()
                    }
                }, {
                    if (it is HttpException && it.code() == 401) {
                        userStore.clearUser(this@WordsSummaryFragment.context!!)
                    }
                })).also {
                disposables.add(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.words_summary_fragment, container, false)
    }

    inner class WordItemCallback: ItemTouchHelper.Callback() {
        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) =
            makeMovementFlags(0, ItemTouchHelper.LEFT)

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = (viewHolder as WordViewHolder).onDeleteHandler()


        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) =
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                getDefaultUIUtil().onDraw(c, recyclerView, viewHolder.itemView.lyt_overlay, dX, dY, actionState, isCurrentlyActive)
            } else {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
    }

    inner class WordViewHolder(itemView: View, private val onDeleteHandler: (String) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private var word: Word? = null
        private fun reset() = itemView.run {
            lyt_overlay.translationX = 0f
            txt_wordId.text = context.getString(R.string.default_word_id)
            txt_wordSummary.text = context.getString(R.string.default_word_summary)
        }

        fun setWord(wordMaybe: Word?) {
            reset()
            this.word = wordMaybe
            wordMaybe?.let { word ->
                itemView.txt_wordId.text = word.id
                itemView.txt_wordSummary.text = word.getSummary()
                itemView.setOnClickListener {
                    startActivity(WordDetailActivity.create(word, context!!))
                }
            }
        }

        fun onDeleteHandler() {
            this.word?.let {
                onDeleteHandler(it.id)
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
                )) { wordId: String ->
                    viewModel.deleteWord(wordId, this@WordsSummaryFragment.context!!)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            Snackbar.make(if (it.isEmpty()) lyt_empty_view else rv_words, R.string.msg_deleted, Snackbar.LENGTH_LONG).show()
                        }, {
                            Log.d(TAG, it.localizedMessage)
                        })
                }


        override fun getItemCount(): Int {
            Log.d(TAG, "ItemCount: ${viewModel.getWordCountForUser()}")
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
        updateWords()
        disposables.add(viewModel
            .wordsSubject
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { words ->
                    lyt_empty_view.setVisible(words.isEmpty())
                    adapter.notifyDataSetChanged()
                },
                { error -> Log.d(TAG, error.localizedMessage) })
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
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
