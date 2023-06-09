package com.dicoding.picodiploma.nusa_nutritionscan.CustomUI

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.dicoding.picodiploma.nusa_nutritionscan.R

class CustomButtonCamera : AppCompatButton {
    private lateinit var enabledBackground: Drawable
    private lateinit var disabledBackground: Drawable
    private var txtColor: Int = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        background = if (isEnabled) enabledBackground else disabledBackground

        setTextColor(txtColor)
        textSize = 14f
        gravity = Gravity.CENTER
        text = "Camera"
    }

    private fun init(){
        txtColor = ContextCompat.getColor(context, android.R.color.holo_green_light)
        enabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button_white) as Drawable
        disabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button_white_disable) as Drawable
    }
}