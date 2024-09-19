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

    var countPostSend: Int
        set(value) = prefs.edit(commit = true) { putInt(PREF_COUNT_POST_SEND, value).commit() }
        get() = prefs.getInt(PREF_COUNT_POST_SEND, 1)

    var showPopUp: Boolean
        set(value) = prefs.edit(commit = true) { putBoolean(PREF_POP_UP, value).commit() }
        get() = prefs.getBoolean(PREF_POP_UP, true)

    var countAdd: Int
        set(value) = prefs.edit(commit = true) { putInt(PREF_COUNT_ADD, value).commit() }
        get() = prefs.getInt(PREF_COUNT_ADD, 20)

    var addShowedUserId: Int
        set(value) = prefs.edit(commit = true) { putInt(PREF_ADD_SHOWED_USER_ID, value).commit() }
        get() = prefs.getInt(PREF_ADD_SHOWED_USER_ID, 0)

    var isTackingUserId: Boolean
        set(value) = prefs.edit(commit = true) { putBoolean(PREF_IS_TRACKING_USER_ID, value).commit() }
        get() = prefs.getBoolean(PREF_IS_TRACKING_USER_ID, false)


    var trackingTypeUserId: Int
        set(value) = prefs.edit(commit = true) { putInt(PREF_TRACKING_TYPE, value).commit() }
        get() = prefs.getInt(PREF_TRACKING_TYPE, 1)


    var userId: String
        set(value) = prefs.edit(commit = true) { putString(PREF_USER_ID, value).commit() }
        get() = prefs.getString(PREF_USER_ID, "").toString()

    companion object {
        const val PREFS = "tales_app_v1_sp_private"
        const val PREF_FREE_TALES = "pref_free_tales"
        const val PREF_POP_UP = "pref_pop_up"
        const val PREF_IS_POP_UP_SHOW = "pref_count_pop_up_show"
        const val PREF_COUNT_ADD = "pref_count_add"
        const val PREF_ADD_SHOWED_USER_ID = "pref_add_showed_user_id"
        const val PREF_IS_TRACKING_USER_ID = "pref_is_tracking_user_id"
        const val PREF_TRACKING_TYPE = "pref_tracking_type"
        const val PREF_USER_ID = "pref_user_id"
        const val PREF_COUNT_POST_SEND = "pref_count_post_send"
    }
}