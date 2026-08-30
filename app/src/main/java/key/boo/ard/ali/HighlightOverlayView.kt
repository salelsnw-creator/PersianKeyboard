package key.boo.ard.ali

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View

class HighlightOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val rect = RectF()

    private val handler = Handler(Looper.getMainLooper())

    private var visibleUntil = 0L

    init {
        setWillNotDraw(false)

        isClickable = false

        isFocusable = false
    }

    fun showKey(
        key: android.inputmethodservice.Keyboard.Key,
        color: Int,
        durationMs: Long = 95L
    ) {

        rect.set(
            key.x + 2f,
            key.y + 2f,
            key.x + key.width - 2f,
            key.y + key.height - 2f
        )

        paint.color = color

        visibleUntil =
            System.currentTimeMillis() + durationMs

        invalidate()

        handler.removeCallbacksAndMessages(null)

        handler.postDelayed({

            visibleUntil = 0L

            invalidate()

        }, durationMs)
    }

    fun clear() {

        visibleUntil = 0L

        handler.removeCallbacksAndMessages(null)

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)

        if (visibleUntil > System.currentTimeMillis()) {

            canvas.drawRoundRect(
                rect,
                6f,
                6f,
                paint
            )

            postInvalidateDelayed(16L)
        }
    }

    override fun onDetachedFromWindow() {

        handler.removeCallbacksAndMessages(null)

        super.onDetachedFromWindow()
    }
}
