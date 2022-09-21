package com.udacity

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator = ValueAnimator()

    private var backgroundButtonColor = 0
    private var circleProgressColor = 0
    private var textColor = 0

    private var buttonProgres = 0
    private var circleRadiusProgress = 0F

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create( "", Typeface.BOLD)
        textSize = 40F
    }


    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when(new) {
            ButtonState.Loading -> {
                loadingAnimation()
                valueAnimator.start()
            }
            ButtonState.Completed -> {
                valueAnimator.cancel()
            }
        }
        invalidate()
    }


    init {
        isClickable = true
        buttonState = ButtonState.init
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            backgroundButtonColor =
                getColor(R.styleable.LoadingButton_buttonColor, Color.BLACK)
            circleProgressColor =
                getColor(R.styleable.LoadingButton_circleColor, Color.YELLOW)
            textColor =  getColor(R.styleable.LoadingButton_textColor, Color.WHITE)
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        paint.color = Color.GRAY
        canvas?.drawColor(paint.color)

        when (buttonState) {
            ButtonState.Loading -> {
                onLoading(canvas)
                paint.color = textColor
                canvas?.drawText("Downloading", widthSize / 2f, heightSize / 2f, paint)
            }
            ButtonState.Completed -> {
                paint.color = textColor
                canvas?.drawText("Download Finished", widthSize / 2f, heightSize / 2f, paint)
            }
            else -> {
                paint.color = textColor
                canvas?.drawText("Download", widthSize / 2f, heightSize / 2f, paint)
            }

        }
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }


    private fun onLoading(canvas: Canvas?){
        paint.color = backgroundButtonColor
        canvas?.drawRect(0f,
            0f,
            buttonProgres.toFloat(),
            heightSize.toFloat(),
            paint)

        val rectF = RectF()
        rectF.set(40F, 40F, 80F, 80F)

        paint.color = circleProgressColor
        canvas?.drawArc(
            rectF,
            360f,
            circleRadiusProgress,
            true,
            paint)
    }

    private fun loadingAnimation(){
        valueAnimator.setValues(
            PropertyValuesHolder.ofInt("buttonColor", 0, widthSize),
            PropertyValuesHolder.ofFloat("radius", 0F, 360F)
        )
        valueAnimator.duration = 3000
        valueAnimator.repeatMode = ValueAnimator.RESTART
        valueAnimator.repeatCount = ValueAnimator.INFINITE

        valueAnimator.addUpdateListener {
            buttonProgres = valueAnimator.getAnimatedValue("buttonColor") as Int
            circleRadiusProgress = valueAnimator.getAnimatedValue("radius") as Float
            invalidate()
        }
    }
}