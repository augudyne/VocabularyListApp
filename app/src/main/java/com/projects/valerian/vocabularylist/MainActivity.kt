package com.projects.valerian.vocabularylist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.projects.valerian.vocabularylist.dagger.ViewModelFactory
import com.projects.valerian.vocabularylist.fragments.AddWordDialogFragment
import com.projects.valerian.vocabularylist.singletons.UserStore
import com.projects.valerian.vocabularylist.viewmodel.WordsViewModel
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, AddWordDialogFragment.OnAddWordInteractionListener {
    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    @Inject
    internal lateinit var userStore: UserStore

    private lateinit var viewModel: WordsViewModel

    private var fetchWordsDisposable: Disposable? = null
    private var userSignOutDisposable: Disposable? = null

    private fun handleAddWord(view: View) {
        if (!userStore.isLoggedIn()) {
            Snackbar.make(view, "Not logged in. Try restarting the application.", Snackbar.LENGTH_LONG).show()
        } else {
            val fragmentManager = supportFragmentManager
            AddWordDialogFragment.newInstance().show(fragmentManager, ID_FRAGMENT_ADD_WORD)
        }
    }

    private fun promptLoginIfNoUser() {
        if (!userStore.isLoggedIn() && userStore.getUser(this) == null) {
            startActivityForResult(LoginActivity.createIntent(this), REQUEST_CODE_LOGIN)
        }
    }

    private fun showSnackbar(msg: String) = Snackbar.make(container, msg, Snackbar.LENGTH_LONG).show()

    override fun onFragmentInteraction(msg: String, result: Int) = when (result) {
        AddWordDialogFragment.RESULT_OK -> showSnackbar(msg)
        AddWordDialogFragment.RESULT_ERROR -> showSnackbar(msg)
        else -> Unit
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view -> handleAddWord(view) }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(WordsViewModel::class.java)

        promptLoginIfNoUser()

        userSignOutDisposable = userStore.userSignedOut.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it) promptLoginIfNoUser()
            }, {
                Log.e(TAG, it.localizedMessage)
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) = when (requestCode) {
        REQUEST_CODE_LOGIN -> { }
        else -> super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                userStore.clearUser(this)
                promptLoginIfNoUser()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    companion object {
        const val REQUEST_CODE_LOGIN = 1000

        private const val TAG = "MainActivity"
        private const val ID_FRAGMENT_ADD_WORD = "fragment_add_word"
    }
}
