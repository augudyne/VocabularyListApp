package com.projects.valerian.vocabularylist.fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.projects.valerian.vocabularylist.R
import com.projects.valerian.vocabularylist.dagger.ViewModelFactory
import com.projects.valerian.vocabularylist.viewmodel.WordsViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.words_summary_fragment.*
import javax.inject.Inject

class WordsSummaryFragment : Fragment() {

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: WordsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.words_summary_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    companion object {
        fun newInstance() = WordsSummaryFragment()
    }

}
