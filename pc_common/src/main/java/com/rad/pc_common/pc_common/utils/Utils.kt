package com.rad.pc_common.pc_common.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.content.ClipboardManager
import android.content.Context.ACTIVITY_SERVICE
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.URLSpan
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.util.Patterns
import android.util.TypedValue
import android.view.ViewParent
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import java.io.*
import java.lang.reflect.Type
import java.net.URLEncoder
import java.security.GeneralSecurityException
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import com.google.android.material.snackbar.Snackbar

import android.widget.ImageView

import android.widget.TextView

import android.view.View
import android.view.ViewGroup
import com.rad.pc_common.R
import com.rad.pc_common.pc_common.Constants
import com.rad.pc_common.pc_common.interfaces.CallToAction


class Utils {
    companion object {
        private val TAG = "Utils"
        const val formatDate = "yyyy-MM-dd HH:mm:ss"
        const val formatDateEx = "dd/MM/yyyy HH:mm:ss"
        const val formatDateUI = "dd/MM/yyyy"
        const val formatDateAMPM = "hh:mm a dd/MM/yyyy"
        fun addFragmentNoAnim(
            fragment: Fragment?,
            fragmentManager: FragmentManager?,
            layoutId: Int?,
            tag: String?
        ) {
            if (fragment != null && fragmentManager != null && layoutId != null && tag != null) {
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.add(layoutId, fragment, tag)
                fragmentTransaction.addToBackStack(tag)
                fragmentTransaction.commitAllowingStateLoss()
            }
        }

        fun checkContainNumbers(string: String?): Boolean {
            return string?.contains("0") == true
                    || string?.contains("1") == true
                    || string?.contains("2") == true
                    || string?.contains("3") == true
                    || string?.contains("4") == true
                    || string?.contains("5") == true
                    || string?.contains("6") == true
                    || string?.contains("7") == true
                    || string?.contains("8") == true
                    || string?.contains("9") == true
        }

        fun saveFileToDevice(
            context: Context?,
            inputStream: InputStream?,
            fileName: String
        ): Uri? {
            if (inputStream != null) {
                context?.let {
                    val values = ContentValues()

                    values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
//                    values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                    val folderPath = Environment.DIRECTORY_DOWNLOADS + "/hifpt/"
                    values.put(MediaStore.MediaColumns.RELATIVE_PATH, folderPath)

                    context.contentResolver.insert(
                        MediaStore.Files.getContentUri("external"),
                        values
                    )?.let { uri ->
                        try {
                            val outputStream = context.contentResolver.openOutputStream(uri)

                            val fileReader = ByteArray(4096)
                            var fileSizeDownloaded: Long = 0

                            while (true) {
                                val read = inputStream.read(fileReader)
                                //Nó được sử dụng để trả về một ký tự trong mẫu ASCII. Nó trả về -1 vào cuối tập tin.
                                if (read == -1) {
                                    break
                                }
                                outputStream?.write(fileReader, 0, read)
                                fileSizeDownloaded += read.toLong()
                            }

                            outputStream!!.flush()
//                    outputStream.write("This is menu category data.".byte())

                            outputStream.close()
                            return uri
//                            return "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/${fileName}"
//                            return "external_files/${folderPath}${fileName}"
//                            return "${uri.path}/${fileName}"
//                            val file = File(uri.path)
//                            return file.absolutePath
                        } catch (e: Exception) {
                            return null
                        }
                    }
                }
            }
            return null
        }

        fun addFragmentNoAnimWithoutBackStack(
            fragment: Fragment,
            fragmentManager: FragmentManager,
            layoutId: Int,
            tag: String
        ) {
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.add(layoutId, fragment, tag)
            fragmentTransaction.commitAllowingStateLoss()
        }

        fun addActivityWithAnimRightToLeft(activity: AppCompatActivity, intent: Intent) {
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.enter, R.anim.exit)
        }

        fun returnToLoginActivity(activity: AppCompatActivity, bundle: Bundle?) {
//            startActivityWithNameAndClearTask(
//                    activity,
//                    "com.rad.hifpt.screens_login.activities.LoginActivity",
//                    bundle
//            )
            startActivityWithNameAndClearTask(
                activity,
                Constants.ActivityClass.loginActivityNew,
                bundle
            )
        }

        fun finishActivityWithMessage(activity: AppCompatActivity, message: String?) {
            val intentFinish = Intent()
            var bundleFinish = Bundle()
            bundleFinish.putInt(Constants.ActionMain.callAction, Constants.ActionMain.showMessage)
            bundleFinish.putString(Constants.BundleParam.message, message)
            intentFinish.putExtras(bundleFinish)
            Utils.finishActivityWithAnim(activity, intentFinish)
        }

        fun startActivityWithNameAndClearTask(
            activity: AppCompatActivity,
            activityNameNeedStart: String,
            bundle: Bundle?
        ) {
            try {
                val activityNeedStartClass = Class.forName(activityNameNeedStart)
                val intent = Intent(activity, activityNeedStartClass)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

                bundle?.let {
                    intent.putExtras(it)
                }
                activity.startActivity(intent)
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                activity.finish()
            } catch (ex: ClassNotFoundException) {
                Log.i("Exception", "class not found")
            }
        }

        fun startActivityWithNameAndResultCode(
            activity: AppCompatActivity,
            activityNameNeedStart: String,
            resultCode: Int,
            bundle: Bundle?
        ) {
            try {
                val activityNeedStartClass = Class.forName(activityNameNeedStart)
                val intent = Intent(activity, activityNeedStartClass)
                bundle?.let {
                    intent.putExtras(it)
                }
                activity.startActivityForResult(intent, resultCode)
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            } catch (ex: ClassNotFoundException) {
                Log.i("Exception", "class not found")
            } catch (e: java.lang.Exception) {
//                Log.i("Exception", e.message)

            }
        }

        fun startActivityWithNameAndResultCodeWithoutAnimation(
            activity: AppCompatActivity,
            activityNameNeedStart: String,
            resultCode: Int,
            bundle: Bundle?
        ) {
            try {
                val activityNeedStartClass = Class.forName(activityNameNeedStart)
                val intent = Intent(activity, activityNeedStartClass)
                bundle?.let {
                    intent.putExtras(it)
                }
                activity.startActivityForResult(intent, resultCode)
            } catch (ex: ClassNotFoundException) {
                Log.i("Exception", "class not found")
            } catch (e: java.lang.Exception) {
//                Log.i("Exception", e.message)

            }
        }

        fun startActivityWithNameAndResultCodeInFragment(
            fragment: Fragment,
            activityNameNeedStart: String,
            resultCode: Int,
            bundle: Bundle?
        ) {
            try {
                val activityNeedStartClass = Class.forName(activityNameNeedStart)
                val intent = Intent(fragment.activity, activityNeedStartClass)
                bundle?.let {
                    intent.putExtras(it)
                }
                fragment.startActivityForResult(intent, resultCode)
                fragment.activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            } catch (ex: ClassNotFoundException) {
                Log.i("Exception", "class not found")
            } catch (e: java.lang.Exception) {
//                Log.i("Exception", e.message)

            }
        }

        fun startActivityBottomUpWithNameAndResultCode(
            activity: AppCompatActivity,
            activityNameNeedStart: String,
            resultCode: Int,
            bundle: Bundle?
        ) {
            try {
                val activityNeedStartClass = Class.forName(activityNameNeedStart)
                val intent = Intent(activity, activityNeedStartClass)
                bundle?.let {
                    intent.putExtras(it)
                }
                activity.startActivityForResult(intent, resultCode)
                activity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top)
            } catch (ex: ClassNotFoundException) {
                Log.i("Exception", "class not found")
            } catch (e: java.lang.Exception) {
//                Log.i("Exception", e.message)

            }
        }

        fun hideSoftKeyboard(view: View?, context: Context?) {
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
        }

        fun showSoftKeyboard(view: View, context: Context?) {
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }

        fun hidenKeyboard(activity: Activity) {
            val inputMethodManager = activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            if (activity.currentFocus != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
            }
        }

        fun hideKeyboardAnyWhere(activity: Activity) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val f = activity.currentFocus
            if (null != f && null != f.windowToken && EditText::class.java.isAssignableFrom(f.javaClass))
                imm.hideSoftInputFromWindow(f.windowToken, 0)
            else
                activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        }


        fun isValidEmail(target: CharSequence): Boolean {
            return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches())
        }

        fun removeSpecialCharactersFromPhoneNumber(phoneNumber: String?): String? {
            return phoneNumber?.trim()?.replace("[^0-9]+".toRegex(), "")
        }

        fun isPhoneNumber(phoneNumber: String?): Boolean {
            if (phoneNumber.isNullOrBlank()) {
                return false
            }

            var maxLength =
                getMaxLengthOfPhoneNumber(removeSpecialCharactersFromPhoneNumber(phoneNumber))

            if (!checkPrefixPhoneNumber(phoneNumber)) {
                return false
            }

            if (phoneNumber.startsWith("0") && phoneNumber.length != maxLength) {
                return false
            }

            if (phoneNumber.startsWith("840") && phoneNumber.length != maxLength) {
                return false
            }

            if (phoneNumber.startsWith("84") && phoneNumber.length != maxLength) {
                return false
            }

            return true
        }


        fun checkPrefixPhoneNumber(phoneNumber: String): Boolean {
            return (phoneNumber.startsWith("0") || phoneNumber.startsWith("84")
                    || phoneNumber.startsWith("840") || (phoneNumber.startsWith("8") && phoneNumber.length == 1))
        }

        fun getMaxLengthOfPhoneNumber(phoneNumber: String?): Int {
            phoneNumber?.let {
                if (it.startsWith("0")) {
                    return 10
                }
                if (it.startsWith("840")) {
                    return 12
                }
                if (it.startsWith("84")) {
                    return 11
                }
                if (it.startsWith("8") && it.length == 1) {
                    return 12
                }
            }
            return 12
        }

        @JvmStatic
        fun fromHtml(html: String): Spanned {
            val mHtml = html?.replace("\n", "<br /> ")
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(mHtml, Html.FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(mHtml)
            }
        }

        fun setTextViewHTML(text: TextView?, html: String, callToAction: CallToAction?) {
            if (text == null) return

            var sequence: Spanned? = null

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                sequence = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
            } else {
                sequence = Html.fromHtml(html)
            }
            val strBuilder = SpannableStringBuilder(sequence)
            val urls = strBuilder.getSpans(0, sequence.length, URLSpan::class.java)
            for (span in urls) {
                makeLinkClickable(strBuilder, span, callToAction)
            }
            text.text = strBuilder
            text.movementMethod = LinkMovementMethod.getInstance()
        }

        fun makeLinkClickable(
            strBuilder: SpannableStringBuilder,
            span: URLSpan,
            callToAction: CallToAction?
        ) {
            val start = strBuilder.getSpanStart(span)
            val end = strBuilder.getSpanEnd(span)
            val flags = strBuilder.getSpanFlags(span)
            val clickable = object : ClickableSpan() {
                override fun onClick(view: View) {
                    span.url?.let {
                        val bundle = Bundle()
                        bundle.putInt(Constants.ActionMain.callAction, Constants.ActionMain.openUrl)
                        bundle.putString(Constants.BundleParam.url, it)
                        callToAction?.action(bundle)

                    }
                }
            }
            strBuilder.setSpan(clickable, start, end, flags)
            strBuilder.removeSpan(span)
        }

        fun getListBoldString(
            contentString: String?,
            listBoldOfString: List<String?>,
            textColor: Int,
            context: Context?
        ): SpannableString? {
            var content = SpannableString("" + contentString)

            for (boldString in listBoldOfString) {
                if (contentString != null && boldString != null) {
                    var startIndex = contentString.indexOf(boldString)
                    var endIndex = startIndex + boldString.length

                    var font =
                        Typeface.createFromAsset(context?.assets, "fonts/inter_semi_bold.otf")
                    var customTypeface = CustomTypefaceSpan("", font)

                    if (startIndex > -1 && endIndex <= contentString.length) {
                        content.setSpan(
                            customTypeface,
                            startIndex,
                            endIndex,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        context?.let {
                            content.setSpan(
                                ForegroundColorSpan(textColor),
                                startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                    }
                }
            }

            return content
        }

        fun <T> startActivityWithFinish(
            beginActivity: AppCompatActivity,
            finishActivity: Class<T>,
            bundle: Bundle?
        ) {
            val intentAcitivity = Intent(beginActivity, finishActivity)
            bundle?.let {
                intentAcitivity.putExtras(it)
            }
            beginActivity.startActivity(intentAcitivity)
            beginActivity.finish()
            beginActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }


        fun startActivityWithResultCode(
            beginActivity: AppCompatActivity,
            finishActivity: AppCompatActivity, resultCode: Int, bundle: Bundle?
        ) {
            val intentAcitivity = Intent(beginActivity, finishActivity::class.java)
            bundle?.let {
                intentAcitivity.putExtras(it)
            }
            beginActivity.startActivityForResult(intentAcitivity, resultCode)
            beginActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        fun <T> startActivityWithResultCode(
            beginActivity: AppCompatActivity,
            finishActivity: Class<T>, resultCode: Int, bundle: Bundle?
        ) {
            val intentAcitivity = Intent(beginActivity, finishActivity)
            bundle?.let {
                intentAcitivity.putExtras(it)
            }
            beginActivity.startActivityForResult(intentAcitivity, resultCode)
            beginActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        fun goToBrowser(activity: Activity, link: String?) {
            var url = link
            if (!url.isNullOrBlank()) {
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://$url"
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                activity.startActivity(browserIntent)
            }
        }

        fun setResultWithRequestLoginAndMessage(activity: Activity, message: String?) {
            val intent = Intent()
            val bundle = Bundle()
            bundle.putInt(Constants.ActionMain.callAction, Constants.ActionMain.requestLogin)
            message?.let {
                bundle.putString(Constants.BundleParam.message, it)
            }
            intent.putExtras(bundle)
            activity.setResult(Activity.RESULT_OK, intent)
            activity.finish()
            activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        fun finishActivityWithAnim(activity: Activity) {
            activity.finish()
            activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }


        fun finishActivityWithAnim(activity: Activity, intent: Intent?) {
            activity.setResult(Activity.RESULT_OK, intent)
            activity.finish()
            activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        fun finishActivityWithNoAnim(activity: Activity, intent: Intent?) {
            activity.setResult(Activity.RESULT_OK, intent)
            activity.finish()
        }

        fun finishActivityWithAnimTopBottom(activity: Activity, intent: Intent?) {
            activity.setResult(Activity.RESULT_OK, intent)
            activity.finish()
            activity.overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom)
        }

        fun finishActivityWithAnimData(activity: Activity, bundle: Bundle?) {
            var intent = Intent()
            bundle?.let {
                intent.putExtras(bundle)
            }
            activity.setResult(Activity.RESULT_OK, intent)
            activity.finish()
            activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        fun addFragment(
            activity: AppCompatActivity?,
            fragment: Fragment?,
            layoutId: Int,
            tag: String
        ) {
            if (activity != null && fragment != null) {
                if (fragment.isAdded) {
                    val fm = activity.supportFragmentManager
                    val ft = fm.beginTransaction()

                    ft.add(layoutId, fragment, tag)
                    ft.commitAllowingStateLoss()
                }
            }

        }

        fun removeFragment(activity: AppCompatActivity?, fragment: Fragment?) {
            try {
                if (activity != null && fragment != null) {
                    val fm = activity.supportFragmentManager
                    val ft = fm.beginTransaction()
                    ft.remove(fragment)
                    ft.commit()
                }
            } catch (e: java.lang.Exception) {

            }
        }

        fun removeFragmentWithBackStack(activity: AppCompatActivity?, fragment: Fragment?) {
            try {
                if (activity != null && fragment != null && fragment.isAdded) {
                    val fm = activity.supportFragmentManager
                    val ft = fm.beginTransaction()
                    ft?.setCustomAnimations(
                        R.anim.slide_in_left,
                        R.anim.slide_out_right,
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                    )
                    ft.remove(fragment)
                    ft.commitAllowingStateLoss()
                    if (fm.backStackEntryCount > 0) {
                        fm.popBackStack()
                    }
                }
            } catch (e: java.lang.Exception) {

            }
        }

        fun removeFragmentWithBackStackNoAnim(activity: AppCompatActivity?, fragment: Fragment?) {
            if (activity != null && fragment != null && fragment.isAdded) {
                val fm = activity.supportFragmentManager
                val ft = fm.beginTransaction()

                ft.remove(fragment)
                ft.commitAllowingStateLoss()
                if (fm.backStackEntryCount > 0) {
                    fm.popBackStack()
                }
            }
        }

        fun dpToPx(context: Context?, dp: Int): Int {
            if (context != null){
                val displayMetrics = context.resources.displayMetrics
                return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
            }
            return 0
        }

        fun convertStringToDate(str_date: String): Date? {
            return try {
                val serverFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                serverFormat.parse(str_date.substring(0, 19))

            } catch (e: Exception) {
                null
            }
        }

        @JvmStatic
        fun formatDate(strDate: String): String? {
            return try {
                val serverFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val dateFormat = SimpleDateFormat("HH:mm dd/MM/yyyy")
                dateFormat.format(serverFormat.parse(strDate))

            } catch (e: ParseException) {
                null
            }
        }

        fun formatDate(strDate: String, pattern: String): String? {
            return try {
                val serverFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val dateFormat = SimpleDateFormat(pattern)
                dateFormat.format(serverFormat.parse(strDate))

            } catch (e: ParseException) {
                null
            }
        }

        fun formatDateOption(strDate: String, oldFormat: String, newFormat: String): String? {
            return try {
                val serverFormat = SimpleDateFormat(oldFormat)
                val dateFormat = SimpleDateFormat(newFormat)
                dateFormat.format(serverFormat.parse(strDate))

            } catch (e: ParseException) {
                null
            }
        }

        fun getTimeNow(): String? {
            return try {
                val yourmilliseconds = System.currentTimeMillis()
                var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                var resultdate = Date(yourmilliseconds)
                sdf.format(resultdate)
            } catch (e: Exception) {
                null
            }
        }

        fun getBoldString(
            context: Context?,
            contentString: String?,
            boldText: String?,
            fontColor: Int?
        ): SpannableString? {
            var content = SpannableString("" + contentString)

            if (contentString != null && boldText != null) {
                var startIndex = contentString.indexOf(boldText)
                var endIndex = startIndex + boldText.length

                var font =
                    Typeface.createFromAsset(context?.assets, "fonts/inter_semi_bold.otf")
                var customTypeface = CustomTypefaceSpan("", font)

                if (startIndex > -1 && endIndex <= contentString.length) {
                    content.setSpan(
                        customTypeface,
                        startIndex,
                        endIndex,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    context?.let {
                        content.setSpan(
                            ForegroundColorSpan(fontColor ?: 0),
                            startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }
            }

            return content
        }

        fun convertHourToSecond(hour: String): Long {
            try {
                val sdf = SimpleDateFormat("HH:mm")
                val gmt = TimeZone.getTimeZone("GMT")
                sdf.timeZone = gmt
                val date = sdf.parse(hour)
                return (date?.time ?: 0) / 1000
            } catch (e: java.lang.Exception) {
                return -1
            }
        }

        fun getAppVersion(context: Context): String {
            val manager = context.packageManager
            val info = manager?.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
            info?.versionName?.let {
                return it
            } ?: run {
                return ""
            }
        }

        fun hideFragmentWithTagname(fragmentTransaction: FragmentManager, tag: String) {
            var fragment = fragmentTransaction.findFragmentByTag(tag)
            fragment?.let {

                if (it.isAdded == true) {
                    fragmentTransaction.beginTransaction().hide(fragment).commitAllowingStateLoss()
                }
            }
        }

        fun removeFragmentWithViewId(fragmentTransaction: FragmentManager, layoutId: Int) {
            fragmentTransaction.findFragmentById(layoutId)?.let {
                fragmentTransaction.beginTransaction().remove(it).commitAllowingStateLoss()
            }
        }

        fun removeFragmentByTag(fragmentTransaction: FragmentManager, tagName: String) {
            fragmentTransaction.findFragmentByTag(tagName)?.let {
                if (it.isAdded) {
                    fragmentTransaction.beginTransaction().remove(it).commitAllowingStateLoss()
                }
            }
        }

        fun removeFragmentByTags(fragmentTransaction: FragmentManager, tagsName: List<String>) {
            val trans = fragmentTransaction.beginTransaction()
            for (tag in tagsName) {
                fragmentTransaction.findFragmentByTag(tag)?.let {
                    if (it.isAdded) {
                        trans.remove(it)
                    }
                }
            }
            trans.commitAllowingStateLoss()
        }

        fun removeFragmentWithTagNameAndBackStack(
            fragmentTransaction: FragmentManager,
            tag: String
        ) {
            var fragment = fragmentTransaction.findFragmentByTag(tag)
            fragment?.let {

                if (it.isAdded == true) {
                    fragmentTransaction.beginTransaction().remove(fragment)
                        .commitAllowingStateLoss()
                    fragmentTransaction.popBackStack()
                }
            }
        }

        fun showFragmentWithTagname(fragmentTransaction: FragmentManager, tag: String) {
            var fragment = fragmentTransaction.findFragmentByTag(tag)
            fragment?.let {
                if (it.isVisible == false) {
                    fragmentTransaction.beginTransaction().show(fragment).commitAllowingStateLoss()
                }
            }
        }

        fun addFragmentNoAnim(
            fragmentTransaction: FragmentTransaction,
            container: Int,
            fragment: Fragment,
            tag: String
        ) {
            fragmentTransaction.add(container, fragment, tag)
            fragmentTransaction.addToBackStack(tag)
            fragmentTransaction.commitAllowingStateLoss()
        }

        fun addFragmentNoAnimNotAddToBackstack(
            fragmentTransaction: FragmentTransaction,
            container: Int,
            fragment: Fragment?,
            tag: String?
        ) {
            fragment?.let {
                fragmentTransaction.add(container, it, tag)
                fragmentTransaction.commitAllowingStateLoss()
            }

        }

        fun addFragmentNoAnimNotAddToBackstackAndClear(
            fragmentManager: FragmentManager,
            container: Int,
            fragment: Fragment,
            tag: String?
        ) {
            for (i in 0 until fragmentManager.backStackEntryCount) {
                fragmentManager.popBackStack()
            }
            val transaction = fragmentManager.beginTransaction()
            transaction.add(container, fragment, tag)
            transaction.commitAllowingStateLoss()

        }

        fun addFragmentWithAnimLeft(
            fragmentTransaction: FragmentTransaction,
            container: Int,
            fragment: Fragment,
            tag: String
        ) {
            fragmentTransaction.setCustomAnimations(
                R.anim.slide_in_left,
                R.anim.slide_out_right,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
            fragmentTransaction.add(container, fragment, tag)
            fragmentTransaction.addToBackStack(tag)
            fragmentTransaction.commitAllowingStateLoss()
        }


        fun addFragmentWithAnimRight(
            fragmentTransaction: FragmentTransaction,
            container: Int,
            fragment: Fragment?,
            tag: String
        ) {
            fragment?.let {
                fragmentTransaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                fragmentTransaction.add(container, it, tag)
                fragmentTransaction.addToBackStack(tag)
                fragmentTransaction.commitAllowingStateLoss()
            }

        }

        fun replaceFragmentWithAnimRight(
            fragmentTransaction: FragmentTransaction,
            container: Int,
            fragment: Fragment?,
            tag: String
        ) {
            fragment?.let {
                fragmentTransaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                fragmentTransaction.replace(container, it, tag)
                fragmentTransaction.addToBackStack(tag)
                fragmentTransaction.commitAllowingStateLoss()
            }

        }

        fun addFragmentWithAnimBottom(
            fragmentTransaction: FragmentTransaction,
            container: Int,
            fragment: Fragment,
            tag: String
        ) {
            fragmentTransaction.setCustomAnimations(
                R.anim.slide_in_bottom,
                R.anim.slide_out_top,
                R.anim.slide_in_top,
                R.anim.slide_out_bottom
            )
            fragmentTransaction.add(container, fragment, tag)
            fragmentTransaction.addToBackStack(tag)
            fragmentTransaction.commitAllowingStateLoss()
        }

//        inline fun <reified T> jsonToObject(jsonString: String?): T? {
//            var jObject: T? = null
//
//            try {
//                var gson = GsonBuilder().create()
//                jObject = gson.fromJson(jsonString, T::class.java)
//            } catch (e: Exception) {
//                jObject = null
//            }
//
//            return jObject
//        }
//
//        inline fun <reified T> jsonToList(jsonString: String?): T? {
//            var jObject: T? = null
//
//            try {
//                val type = object : TypeToken<T>() {}.type
//                jObject = parseArray<T>(json = jsonString!!, typeToken = type)
//            } catch (e: Exception) {
//                jObject = null
//            }
//
//            return jObject
//        }
//
//        inline fun <reified T> parseArray(json: String, typeToken: Type): T {
//            val gson = GsonBuilder().create()
//            return gson.fromJson<T>(json, typeToken)
//        }
//
//        fun objectToJSON(jObject: Any?): String? {
//            var jsonString: String? = null
//
//            try {
//                var gson = GsonBuilder().create()
//                jsonString = gson.toJson(jObject)
//            } catch (e: Exception) {
//                jsonString = null
//            }
//
//            return jsonString
//        }

        fun convertObjectToHashMap(obj: Any): Map<String, Any>? {
            val map: MutableMap<String, Any> = HashMap()
            for (field in obj.javaClass.declaredFields) {
                field.isAccessible = true
                try {
                    map[field.name] = field[obj]
                } catch (e: java.lang.Exception) {
                }
            }
            return map
        }

        fun setTextSizeWithSp(textView: TextView?, size: Float) {
            textView?.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                size / (textView.context?.resources?.displayMetrics?.density ?: 1f)
            )

        }

        fun convertDateToAnotherFormat(
            dateNeedToFormat: String?,
            originalFormaType: String,
            targetFormatType: String
        ): String {
            if (dateNeedToFormat.isNullOrBlank()) {
                return ""
            }
            var originalFormat = SimpleDateFormat(originalFormaType, Locale.ENGLISH)
            var targetFormat = SimpleDateFormat(targetFormatType, Locale.ENGLISH)
            var date = originalFormat.parse(dateNeedToFormat)
            return targetFormat.format(date)
        }

        fun checkIfAppIsInstalled(context: Context?, packageName: String?): Boolean {
            if (!packageName.isNullOrBlank() && context != null) {
                try {
                    context.packageManager.getApplicationInfo(packageName, 0)
                    return true
                } catch (e: PackageManager.NameNotFoundException) {
                    return false
                }
            }
            return false
        }


        fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
                permissions.forEach {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            it
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return false
                    }
                }
            }
            return true
        }



        fun getColorAQIByPoint(context: Context, point: Int): Int {
            if (point == -1) {
                return ContextCompat.getColor(context, R.color.aqi_lv_na)
            } else {
                when (point) {
                    in 0..50 -> {
                        return ContextCompat.getColor(context, R.color.aqi_lv_0)
                    }
                    in 51..100 -> {
                        return ContextCompat.getColor(context, R.color.aqi_lv_1)
                    }
                    in 101..150 -> {
                        return ContextCompat.getColor(context, R.color.aqi_lv_2)
                    }
                    in 151..200 -> {
                        return ContextCompat.getColor(context, R.color.aqi_lv_3)
                    }
                    in 201..300 -> {
                        return ContextCompat.getColor(context, R.color.aqi_lv_4)
                    }
                    in 301..500 -> {
                        return ContextCompat.getColor(context, R.color.aqi_lv_5)
                    }
                    else -> {
                        return ContextCompat.getColor(context, R.color.aqi_lv_5)
                    }
                }
            }
        }

        fun getDateFromLongTime(milliSeconds: Long, dateFormat: String?): String {
            //Utils.getDateFromLongTime(1586916000000,"dd/MM/yyyy hh:mm:ss.SSS")
            //Utils.getDateFromLongTime(chartData?.lastvalueaveragehour[1][1]?.toLong() ?: 0,"dd/MM/yyyy")
            dateFormat?.let {
                var formatter = SimpleDateFormat(dateFormat)
                var calendar = Calendar.getInstance()
                calendar.timeInMillis = milliSeconds
                return formatter.format(calendar.time)
            }
            return ""
        }


//        fun makeInformDialog(activity: AppCompatActivity, content: String?) {
//            if (!content.isNullOrBlank()) {
//                var bundle = Bundle()
//                bundle.putString(Constants.BundleParam.title, content)
//
//                InformDialogFragment.createInstance(bundle).show(
//                    activity.supportFragmentManager,
//                    Constants.FragmentName.informDialogFragment
//                )
//            }
//        }

        fun clearAllBackStack(activity: AppCompatActivity) {
            val fm = activity.supportFragmentManager
            try {
                fm.fragments.let {
                    if (it.size > 0)
                        for (fragment in it) {
                            if (fragment?.isAdded == true && fragment is DialogFragment) {
                                fragment.dismissAllowingStateLoss()
                            }
                        }
                }
            } catch (e: Exception) {

            }
            for (i in 0 until fm.backStackEntryCount) {
                fm.popBackStackImmediate()
            }
        }

        fun isRecyclerScrollable(recyclerView: RecyclerView): Boolean {
            return recyclerView.computeHorizontalScrollRange() > recyclerView.width || recyclerView.computeVerticalScrollRange() > recyclerView.height
        }


        fun scrollToView(scrollViewParent: ScrollView, view: View) {
            // Get deepChild Offset
            val childOffset = Point()
            getDeepChildOffset(scrollViewParent, view.parent, view, childOffset)
            // Scroll to child.
            scrollViewParent.smoothScrollTo(0, childOffset.y)
        }

        fun scrollToView(scrollViewParent: NestedScrollView, view: View) {
            // Get deepChild Offset
            val childOffset = Point()
            getDeepChildOffset(scrollViewParent, view.parent, view, childOffset)
            // Scroll to child.
            scrollViewParent.smoothScrollTo(0, childOffset.y)
        }

        /**
         * Used to get deep child offset.
         * <p/>
         * 1. We need to scroll to child in scrollview, but the child may not the direct child to scrollview.
         * 2. So to get correct child position to scroll, we need to iterate through all of its parent views till the main parent.
         *
         * @param mainParent        Main Top parent.
         * @param parent            Parent.
         * @param child             Child.
         * @param accumulatedOffset Accumulated Offset.
         */
        fun getDeepChildOffset(
            mainParent: ViewGroup,
            parent: ViewParent,
            child: View,
            accumulatedOffset: Point
        ) {
            val parentGroup = parent as ViewGroup
            accumulatedOffset.x += child.left
            accumulatedOffset.y += child.top
            if (parentGroup == mainParent) {
                return
            }
            getDeepChildOffset(mainParent, parentGroup.parent, parentGroup, accumulatedOffset)
        }

        fun backToHome(activity: AppCompatActivity, bundle: Bundle? = null) {
            startActivityWithNameAndClearTop(
                activity,
                Constants.ActivityClass.NewHomeActivity,
                bundle
            )
        }

        fun backToActivity(activity: AppCompatActivity, activityClass: String, bundle: Bundle?) {
            startActivityWithNameAndClearTop(activity, activityClass, bundle)
        }

        fun startActivityWithNameAndClearTop(
            activity: AppCompatActivity,
            activityNameNeedStart: String,
            bundle: Bundle?
        ) {
            try {
                val activityNeedStartClass = Class.forName(activityNameNeedStart)
                val intent = Intent(activity, activityNeedStartClass)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                bundle?.let {
                    intent.putExtras(it)
                }
                activity.startActivity(intent)
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                activity.finish()
            } catch (ex: ClassNotFoundException) {
                Log.i("Exception", "class not found")
            }
        }

        fun isDuplicateEmail(currentEmail: String, newEmail: String): Boolean {
            return (currentEmail.equals(newEmail, ignoreCase = true))
        }

        fun base64ToString(base64: String?): String {
            base64?.let {
                Log.e(
                    "decodeBase64",
                    "${String(Base64.decode(base64, Base64.DEFAULT), charset("UTF-8"))}"
                )
                return String(Base64.decode(base64, Base64.DEFAULT), charset("UTF-8"))
            }
            return ""
        }

        fun String.encode(): String {
            return Base64.encodeToString(this.toByteArray(charset("UTF-8")), Base64.DEFAULT)
        }
        fun convertBase64ToBitmap(b64: String): Bitmap? {
            val bitmapImage = BitmapFactory.decodeByteArray(b64.base64ToByteCode(), 0, b64.base64ToByteCode().size)
            return bitmapImage
        }
        fun String.base64ToByteCode() =Base64.decode(this.substring(this.indexOf(",")  + 1),Base64.DEFAULT)
        fun bitmapToBase64(bitmapImage: Bitmap): String {
            var url_encode_val = ""
            bitmapImage.let {
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                val encrypted = Base64.encodeToString(byteArray, Base64.DEFAULT)
                //Log.d("bitmapToBase64", "$encrypted")
                try {
                    url_encode_val = URLEncoder.encode(encrypted, "utf-8")
                    //  Log.e("encryptData_To url::", url_encode_val)
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }

                return url_encode_val
            }
            return ""
        }

        fun getWidthScreen(context: Context): Int? {
            val displayMetrics = DisplayMetrics()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val display = context.display
                display?.getRealMetrics(displayMetrics)
            } else {
                @Suppress("DEPRECATION")
                val display =
                    (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
                @Suppress("DEPRECATION")
                display.getMetrics(displayMetrics)
            }
            return displayMetrics.widthPixels
        }


        fun getHeightScreen(context: Context): Int? {
            val displayMetrics = DisplayMetrics()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val display = context.display
                display?.getRealMetrics(displayMetrics)
            } else {
                @Suppress("DEPRECATION")
                val display =
                    (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
                @Suppress("DEPRECATION")
                display.getMetrics(displayMetrics)
            }
            return displayMetrics.heightPixels
        }

        fun removeAccentsString(input: String): CharSequence {
//            var str = Normalizer.normalize(input, Normalizer.Form.NFD);
            var str = input.replace("à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ".toRegex(), "a")
            str = str.replace("è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ".toRegex(), "e")
            str = str.replace("ì|í|ị|ỉ|ĩ".toRegex(), "i")
            str = str.replace("ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ".toRegex(), "o")
            str = str.replace("ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ".toRegex(), "u")
            str = str.replace("ỳ|ý|ỵ|ỷ|ỹ".toRegex(), "y")
            str = str.replace("đ".toRegex(), "d")

            str = str.replace("À|Á|Ạ|Ả|Ã|Â|Ầ|Ấ|Ậ|Ẩ|Ẫ|Ă|Ằ|Ắ|Ặ|Ẳ|Ẵ".toRegex(), "A")
            str = str.replace("È|É|Ẹ|Ẻ|Ẽ|Ê|Ề|Ế|Ệ|Ể|Ễ".toRegex(), "E")
            str = str.replace("Ì|Í|Ị|Ỉ|Ĩ".toRegex(), "I")
            str = str.replace("Ò|Ó|Ọ|Ỏ|Õ|Ô|Ồ|Ố|Ộ|Ổ|Ỗ|Ơ|Ờ|Ớ|Ợ|Ở|Ỡ".toRegex(), "O")
            str = str.replace("Ù|Ú|Ụ|Ủ|Ũ|Ư|Ừ|Ứ|Ự|Ử|Ữ".toRegex(), "U")
            str = str.replace("Ỳ|Ý|Ỵ|Ỷ|Ỹ".toRegex(), "Y")
            str = str.replace("Đ".toRegex(), "D")
            Log.e("removeAccentsString ", str.lowercase())

            return str.lowercase()
        }

        fun marginLayout(left: Float, right: Float, top: Float, bottom: Float, view: View) {
            val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
            val r: Resources = view.context.resources
            fun marginInDp(sizeInDP: Float): Int {
                return TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    sizeInDP,
                    r.displayMetrics
                ).toInt()
            }

            layoutParams.rightMargin = marginInDp(right)
            layoutParams.leftMargin = marginInDp(left)
            layoutParams.topMargin = marginInDp(top)
            layoutParams.bottomMargin = marginInDp(bottom)
        }

        fun formatDateOptionWithLocaleUS(strDate: String, oldFormat: String, newFormat: String): String? {
            return try {
                val serverFormat = SimpleDateFormat(oldFormat, Locale.US)
                val dateFormat = SimpleDateFormat(newFormat, Locale.US)
                dateFormat.format(serverFormat.parse(strDate))

            } catch (e: ParseException) {
                null
            }
        }
    }

}


fun EditText.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as
            InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun EditText.showKeyboard() {
    post {
        requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun dip(value: Int): Int {
    return (value * Resources.getSystem().displayMetrics.density).toInt()
}

private const val KEYBOARD_VISIBLE_THRESHOLD_DP = 100

fun Activity.isKeyboardOpened(): Boolean {
    val r = Rect()

    val activityRoot = getActivityRoot()
    val visibleThreshold = dip(KEYBOARD_VISIBLE_THRESHOLD_DP)

    activityRoot.getWindowVisibleDisplayFrame(r)

    val heightDiff = activityRoot.rootView.height - r.height()

    return heightDiff > visibleThreshold
}

fun Activity.getActivityRoot(): View {
    return (findViewById<ViewGroup>(android.R.id.content)).getChildAt(0)
}

fun Activity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(findViewById<ViewGroup>(android.R.id.content).windowToken, 0)
}



