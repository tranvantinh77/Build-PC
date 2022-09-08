package com.rad.pc_common.pc_common.custom

import android.content.Context
import android.graphics.Typeface
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.text.method.KeyListener
import android.util.AttributeSet
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import com.rad.pc_common.R

class CustomEditText: AppCompatEditText {

    companion object {
        private val fontCache: MutableMap<String, Typeface> = mutableMapOf()

        fun getFontCache(context: Context, fontName: String): Typeface? {
            var typeface = fontCache[fontName]
            if (typeface == null) {
                try {
                    typeface = Typeface.createFromAsset(context.assets, "fonts/$fontName")
                    fontCache.put(fontName, typeface)
                } catch (e: Exception) {
                    return null
                }
            }
            return typeface
        }
    }

    constructor(context: Context): super(context)

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int): super(context, attrs, defStyle) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        includeFontPadding = false
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.Custom_TextView_EditText)
//            font - start
//            val fontName = typedArray.getString(R.styleable.Custom_TextView_EditText_fontValue)
            val fontName = when (typedArray.getString(R.styleable.Custom_TextView_EditText_fontValue)){
                "0" -> {
                    "sf_ui_text_bold"
                }
                "1" -> {
                    "sf_ui_text_bold_italic"
                }
                "2" -> {
                    "sf_ui_text_heavy"
                }
                "3" -> {
                    "sf_ui_text_heavy_italic"
                }
                "4" -> {
                    "sf_ui_text_italic"
                }
                "5" -> {
                    "sf_ui_text_light"
                }
                "6" -> {
                    "sf_ui_text_light_italic"
                }
                "7" -> {
                    "sf_ui_text_medium"
                }
                "8" -> {
                    "sf_ui_text_medium_italic"
                }
                "9" -> {
                    "sf_ui_text_regular"
                }
                "10" -> {
                    "sf_ui_text_semi_bold"
                }
                "11" -> {
                    "sf_ui_text_semi_bold_italic"
                }
                "12" -> {
                    "fs_north_land"
                }
                "13" -> {
                    "butterly"
                }
                "14" -> {
                    "inter_black"
                }
                "15" -> {
                    "inter_bold"
                }
                "16" -> {
                    "inter_extra_bold"
                }
                "17" -> {
                    "inter_extra_light"
                }
                "18" -> {
                    "inter_light"
                }
                "19" -> {
                    "inter_medium"
                }
                "20" -> {
                    "inter_regular"
                }
                "21" -> {
                    "inter_semi_bold"
                }
                "22" -> {
                    "inter_thin"
                }
                else -> {
                    null
                }
            }

            try {
                if (fontName != null) {
//                    val typeface = Typeface.createFromAsset(context.assets, "fonts/$fontName")
//                    setTypeface(typeface)

//                    val typeface = CustomEditText.getFontCache(context, fontName)
//                    if (typeface != null) setTypeface(typeface)
                    val typeface = CustomTextView.getFontCache(context, "$fontName.otf")
                    if (typeface != null) setTypeface(typeface)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
//            font - end

//            digits - start
//            val digitsValue = when (typedArray.getString(R.styleable.Custom_TextView_EditText_digitsValue)){
//                "0" -> {
//                    HiTextConstants.VIETNAMESE_DIACRITIC_CHARACTERS + HiTextConstants.FLOAT_NUMBER
//                }
//                "1" -> {
//                    HiTextConstants.DECIMAL_NUMBER + HiTextConstants.VIETNAMESE_DIACRITIC_CHARACTERS + HiTextConstants.ADDRESS_SYMBOL
//                }
//                else -> {
//                    null
//                }
//            }
//
////            try {
////                if (!digitsValue.isNullOrEmpty()) {
//////                    keyListener = DigitsKeyListener.getInstance(digitsValue).apply {
//////                        inputType = this@CustomEditText?.inputType ?: 0
//////                    }
//////                    keyListener = CustomDigitsKeyListener(this@CustomEditText, DigitsKeyListener.getInstance(digitsValue))
////
////                    keyListener = DigitsKeyListener.getInstance(digitsValue)
////                    setRawInputType(InputType.TYPE_CLASS_TEXT)
////
////                }
////            } catch (e: Exception) {
////                e.printStackTrace()
////            }
//            digits - end
            typedArray.recycle()
        }
    }

    class CustomDigitsKeyListener(val editText:EditText, digitsKeyListener: DigitsKeyListener): KeyListener by digitsKeyListener {
        override fun getInputType(): Int {
//            return editText.inputType
            return InputType.TYPE_CLASS_TEXT
        }
    }

    fun setFontValue(fontName: String?) {
        includeFontPadding = false
        try {
            if (fontName != null) {
//                    val typeface = Typeface.createFromAsset(context.assets, "fonts/$fontName")
//                    setTypeface(typeface)

                val typeface = CustomTextView.getFontCache(context, "$fontName.otf")
                if (typeface != null) setTypeface(typeface)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}