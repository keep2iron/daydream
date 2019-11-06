package io.github.keep2iron.daydream.util

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import io.github.keep2iron.daydream.R
import java.io.Serializable

object PageTransition {

    fun setArgsForIntent(intent: Intent, vararg args: Pair<String, Any>) {
        for (arg in args) {
            when (val value = arg.second) {
                is String -> {
                    intent.putExtra(arg.first, value)
                }
                is Int -> {
                    intent.putExtra(arg.first, value)
                }
                is Double -> {
                    intent.putExtra(arg.first, value)
                }
                is Float -> {
                    intent.putExtra(arg.first, value)
                }
                is Byte -> {
                    intent.putExtra(arg.first, value)
                }
                is Boolean -> {
                    intent.putExtra(arg.first, value)
                }
                is Bundle -> {
                    intent.putExtra(arg.first, value)
                }
                is Long -> {
                    intent.putExtra(arg.first, value)
                }
                is Char -> {
                    intent.putExtra(arg.first, value)
                }
                is Short -> {
                    intent.putExtra(arg.first, value)
                }
                is Parcelable -> {
                    intent.putExtra(arg.first, value)
                }
                is IntArray -> {
                    intent.putExtra(arg.first, value)
                }
                is ByteArray -> {
                    intent.putExtra(arg.first, value)
                }
                is FloatArray -> {
                    intent.putExtra(arg.first, value)
                }
                is DoubleArray -> {
                    intent.putExtra(arg.first, value)
                }
                is BooleanArray -> {
                    intent.putExtra(arg.first, value)
                }
                is Serializable -> {
                    intent.putExtra(arg.first, value)
                }
                is LongArray -> {
                    intent.putExtra(arg.first, value)
                }
                is CharSequence -> {
                    intent.putExtra(arg.first, value)
                }
            }
        }
    }

    inline fun <reified T : Activity> start(activity: Activity, vararg args: Pair<String, Any>) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val intent = Intent(activity, T::class.java)
            setArgsForIntent(intent, *args)
            activity.startActivity(
                intent,
                ActivityOptions.makeSceneTransitionAnimation(activity).toBundle()
            )
        } else {
            val intent = Intent(activity, T::class.java)
            setArgsForIntent(intent, *args)
            activity.startActivity(intent)
            activity.overridePendingTransition(
                R.anim.anim_page_in,
                R.anim.anim_page_out
            )
        }
    }
}