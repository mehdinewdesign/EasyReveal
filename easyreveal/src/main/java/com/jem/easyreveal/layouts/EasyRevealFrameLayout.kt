package com.jem.easyreveal.layouts

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import com.jem.easyreveal.ClipPathProvider
import com.jem.easyreveal.RevealAnimatorManager
import com.jem.easyreveal.RevealLayout
import com.jem.easyreveal.clippathproviders.LinearClipPathProvider

class EasyRevealFrameLayout : FrameLayout, RevealLayout {
    // Store path in local variable rather then getting it from ClipPathProvider each time
    private var path: Path? = null
    // ClipPathProvider provides the aforementioned path used for clipping
    var clipPathProvider: ClipPathProvider = LinearClipPathProvider()
    // RevealAnimator is used to perform reveal/hide animation
    private val revealAnimatorManager: RevealAnimatorManager = RevealAnimatorManager()
    // Reveal animation duration
    var revealAnimationDuration: Long = 1000
    // Hide animation duration
    var hideAnimationDuration: Long = 1000

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun draw(canvas: Canvas?) {
        try {
            canvas?.save()
            path?.let {
                canvas?.clipPath(it, clipPathProvider.op)
            }
            super.draw(canvas)
        } finally {
            canvas?.restore()
        }
    }

    override fun reveal(onUpdate: ((it: ValueAnimator) -> Unit)?) {
        revealAnimatorManager.reveal(revealAnimationDuration) {
            path = clipPathProvider.getPath(it.animatedValue as Float, this@EasyRevealFrameLayout)
            invalidate()
            onUpdate?.invoke(it)
        }
    }

    override fun hide(onUpdate: ((it: ValueAnimator) -> Unit)?) {
        revealAnimatorManager.hide(hideAnimationDuration) {
            path = clipPathProvider.getPath(it.animatedValue as Float, this@EasyRevealFrameLayout)
            invalidate()
            onUpdate?.invoke(it)
        }
    }

    override fun revealForPercentage(percent: Float) {
        path = clipPathProvider.getPath(percent, this@EasyRevealFrameLayout)
        invalidate()
    }
}