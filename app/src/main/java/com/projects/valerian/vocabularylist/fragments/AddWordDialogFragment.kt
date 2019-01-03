package com.projects.valerian.vocabularylist.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.projects.valerian.vocabularylist.R
import com.projects.valerian.vocabularylist.dagger.ViewModelFactory
import com.projects.valerian.vocabularylist.viewmodel.WordsViewModel
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_add_word_dialog.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AddWordDialogFragment.OnAddWordInteractionListener] interface
 * to handle interaction events.
 * Use the [AddWordDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class AddWordDialogFragment : DialogFragment() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: WordsViewModel

    private var listener: OnAddWordInteractionListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txt_word.requestFocus()
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        btn_positive.setOnClickListener { btnView ->
            if (txt_word.text.isBlank()) {
                txt_word.error = getString(R.string.error_word_required)
                txt_word.requestFocus()
                return@setOnClickListener
            }
            val searchId = txt_word.text.toString()
            viewModel.addWord(searchId, btnView.context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    listener?.onFragmentInteraction("$searchId was added.", RESULT_OK)
                    dismiss()
                }, { error ->
                    Log.e(TAG, error.message, error)
                    listener?.onFragmentInteraction(error.localizedMessage, RESULT_ERROR)
                    dismiss()
                })
        }

        btn_negative.setOnClickListener { btnView ->
            listener?.onFragmentInteraction("Canceled", RESULT_CANCELED)
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(WordsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_word_dialog, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnAddWordInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnAddWordInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    interface OnAddWordInteractionListener {
        fun onFragmentInteraction(msg: String, result: Int)
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddWordDialogFragment()

        const val RESULT_OK = 0
        const val RESULT_ERROR = 1
        const val RESULT_CANCELED = 2

        private const val TAG = "AddWordDialogFragment"
    }
}
