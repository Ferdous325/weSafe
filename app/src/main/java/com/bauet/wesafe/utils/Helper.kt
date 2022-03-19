package com.bauet.wesafe.utils

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.bauet.wesafe.R
import com.google.android.material.snackbar.Snackbar
import java.util.regex.Matcher
import java.util.regex.Pattern

fun Context.toast(msg: String?, time: Int = Toast.LENGTH_SHORT) {
    if (!msg.isNullOrEmpty()) {
        val toast = Toast.makeText(this, msg, time)
        val view: View? = toast.view
        view?.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black_100))
        val textView: TextView? = view?.findViewById(android.R.id.message)
        textView?.setTextColor(ContextCompat.getColor(this, R.color.white))
        toast.show()
    }
}

fun View.snackbar(message: String, length: Int = Snackbar.LENGTH_INDEFINITE) {
    Snackbar.make(this, message, length).also { snackbar ->
        snackbar.setAction("ঠিক আছে") {
            snackbar.dismiss()
        }
    }.show()
}

fun View.snackbar(
    message: String,
    length: Int = Snackbar.LENGTH_INDEFINITE,
    actionName: String,
    onClick: ((view: View) -> Unit)? = null,
): Snackbar {
    return Snackbar.make(this, message, length).also { snackbar ->
        snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).maxLines =
            5
        snackbar.setActionTextColor(Color.YELLOW)
        snackbar.setAction(actionName) {
            onClick?.invoke(it)
            snackbar.dismiss()
        }
    }
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

object Validator {

    val VALID_MOBILE_NUMBER_REGEX: Pattern = Pattern.compile(
        "^(01|০১)?[0-9০১২৩৪৫৬৭৮৯]{9}$",
        Pattern.CASE_INSENSITIVE
    )

    fun isValidMobileNumber(mobileNumber: String?): Boolean {
        val matcher: Matcher =
            VALID_MOBILE_NUMBER_REGEX.matcher(mobileNumber)
        return matcher.find()
    }
}

object AppConstant {
    const val GPS_REQUEST = 1001
}

internal object SharedPreferenceUtil {

    const val KEY_FOREGROUND_ENABLED = "tracking_foreground_location"

    fun getLocationTrackingPref(context: Context): Boolean =
        context.getSharedPreferences(
            context.getString(R.string.app_name), Context.MODE_PRIVATE)
            .getBoolean(KEY_FOREGROUND_ENABLED, false)

    fun saveLocationTrackingPref(context: Context, requestingLocationUpdates: Boolean) =
        context.getSharedPreferences(
            context.getString(R.string.app_name),
            Context.MODE_PRIVATE).edit {
            putBoolean(KEY_FOREGROUND_ENABLED, requestingLocationUpdates)
        }
}

