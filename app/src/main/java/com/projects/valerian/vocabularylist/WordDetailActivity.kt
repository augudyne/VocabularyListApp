package com.projects.valerian.vocabularylist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.projects.valerian.vocabularylist.models.Word
import kotlinx.android.synthetic.main.activity_word_detail.*


class WordDetailActivity : AppCompatActivity() {

    private lateinit var word: Word

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_detail)
        setSupportActionBar(toolbar)
        word = intent.getParcelableExtra(EXTRA_KEY_WORD) as Word
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = word.id
        }
    }

    override fun onStart() {
        super.onStart()
        lbl_title.text = word.id
        lbl_description.text = word.getDescription()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val EXTRA_KEY_WORD = "extra_key_word"

        fun create(word: Word, context: Context): Intent = Intent(context, WordDetailActivity::class.java).apply {
            putExtra(EXTRA_KEY_WORD, word)
        }

    }
}
