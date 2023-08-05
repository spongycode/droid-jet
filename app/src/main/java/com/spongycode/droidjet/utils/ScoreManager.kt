package com.spongycode.droidjet.utils

import android.content.Context
import android.content.SharedPreferences

class ScoreManager(context: Context) {
    companion object {
        private const val PREFS_NAME = "MyPrefs"
        private const val KEY_SCORE = "score"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var score: Int
        get() = sharedPreferences.getInt(KEY_SCORE, 0)
        set(score) {
            val editor = sharedPreferences.edit()
            editor.putInt(KEY_SCORE, score)
            editor.apply()
        }
}
