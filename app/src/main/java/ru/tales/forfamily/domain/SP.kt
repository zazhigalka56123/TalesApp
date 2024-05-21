package ru.tales.forfamily.domain

import android.content.Context
import androidx.core.content.edit

class SP(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    var addShowed: Int
        set(value) {
            prefs.edit(commit = true) { putInt(PREF_FREE_TALES, value).commit() }
        }
        get() = prefs.getInt(PREF_FREE_TALES, 0)


    var countPopUpShowed: Int
        set(value) = prefs.edit(commit = true) { putInt(PREF_IS_POP_UP_SHOW, value).commit() }
        get() = prefs.getInt(PREF_IS_POP_UP_SHOW, 1)

    var showPopUp: Boolean
        set(value) = prefs.edit(commit = true) { putBoolean(PREF_POP_UP, value).commit() }
        get() = prefs.getBoolean(PREF_POP_UP, false)

    var countAdd: Int
        set(value) = prefs.edit(commit = true) { putInt(PREF_COUNT_ADD, value).commit() }
        get() = prefs.getInt(PREF_COUNT_ADD, 20)

    companion object {
        const val PREFS = "tales_app_v1_sp_private"
        const val PREF_FREE_TALES = "pref_free_tales"
        const val PREF_POP_UP = "pref_pop_up"
        const val PREF_IS_POP_UP_SHOW = "pref_count_pop_up_show"
        const val PREF_COUNT_ADD = "pref_count_add"
    }
}