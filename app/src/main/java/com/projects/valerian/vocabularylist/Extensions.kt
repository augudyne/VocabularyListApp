package com.projects.valerian.vocabularylist

import android.view.View

fun View.setVisible(isVisible: Boolean) = if (isVisible) {
    this.visibility = View.VISIBLE
} else {
    this.visibility = View.GONE
}