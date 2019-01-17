package com.projects.valerian.vocabularylist

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.projects.valerian.vocabularylist.apis.AccountApi
import com.projects.valerian.vocabularylist.singletons.UserStore
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register.*
import javax.inject.Inject

/**
 * A login screen that offers login via email/password.
 */
class RegisterActivity : AppCompatActivity() {

    @Inject
    internal lateinit var accountApi: AccountApi
    @Inject
    internal lateinit var userStore: UserStore

    private var registerDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_register)
        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptRegister()
                return@OnEditorActionListener true
            }
            false
        })

        btn_register.setOnClickListener { attemptRegister() }
    }

    private fun attemptRegister() {
        registerDisposable?.dispose()

        // Reset errors.
        username.error = null
        email.error = null
        password_confirm.error = null
        password.error = null

        // Store values at the time of the login attempt.
        val usernameStr = username.text.toString()
        val passwordStr = password.text.toString()
        val confirmPasswordStr = password_confirm.text.toString()
        val emailStr = email.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
            password.error = getString(R.string.error_invalid_password)
            focusView = password
            cancel = true
        }

        if (!TextUtils.isEmpty(confirmPasswordStr) && !isPasswordValid(confirmPasswordStr)) {
            password_confirm.error = getString(R.string.error_field_required)
            focusView = password_confirm
            cancel = true
        }

        // Check that passwords match
        if (passwordStr != confirmPasswordStr) {
            password_confirm.error = getString(R.string.error_passwords_match)
            focusView = password_confirm
            cancel = true
        }

        // Check for a username
        if (TextUtils.isEmpty(usernameStr)) {
            username.error = getString(R.string.error_field_required)
            focusView = username
            cancel = true
        } else if (!isUsernameValid(usernameStr)) {
            username.error = getString(R.string.error_invalid_username)
            focusView = username
            cancel = true
        }

        // Check for an email
        if (TextUtils.isEmpty(emailStr)) {
            email.error = getString(R.string.error_field_required)
            focusView = email
            cancel = true
        } else if (!isEmailValid(emailStr)) {
            email.error = getString(R.string.error_invalid_email)
            focusView = email
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            (this.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(currentFocus.windowToken, 0)
            showProgress(true)
            registerDisposable = accountApi
                .register(AccountApi.RegisterData("", emailStr, usernameStr, passwordStr))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    showProgress(false)
                    Toast.makeText(this, R.string.msg_register_successful, Snackbar.LENGTH_LONG).show()
                    registerDisposable = null
                    finish()
                }, { throwable ->
                    showProgress(false)
                    Toast.makeText(this, R.string.error_register_failed, Snackbar.LENGTH_LONG).show()
                    registerDisposable = null
                    finish()
                })
        }
    }

    private fun isEmailValid(email: String) = email.contains("@")

    private fun isUsernameValid(username: String): Boolean = !username.contains("!/.'`~+#$%^&&*()")

    private fun isPasswordValid(password: String): Boolean = password.length > 5

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            register_form.visibility = if (show) View.GONE else View.VISIBLE
            register_form.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 0 else 1).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        register_form.visibility = if (show) View.GONE else View.VISIBLE
                    }
                })

            register_progress.visibility = if (show) View.VISIBLE else View.GONE
            register_progress.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 1 else 0).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        register_progress.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            register_progress.visibility = if (show) View.VISIBLE else View.GONE
            register_form.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    companion object {
        fun createIntent(context: Context): Intent = Intent(context, RegisterActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
    }
}
