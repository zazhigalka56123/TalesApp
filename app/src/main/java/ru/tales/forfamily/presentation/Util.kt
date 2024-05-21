package ru.tales.forfamily.presentation

import android.app.Activity
import android.content.Context
import com.google.android.material.snackbar.Snackbar
import ru.tales.forfamily.R

fun Context.snackBar(message: String) {
    val decorView = (this as Activity).window.decorView
    val view = decorView.findViewById(android.R.id.content) ?: decorView.rootView
    Snackbar
        .make(view, message, Snackbar.LENGTH_LONG)
        .setBackgroundTint(getColor(R.color.main))
        .show()
}

enum class Mode{
    DEFAULT_MODE,
    NO_INTERNET_MODE
}