package key.boo.ard.ali

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.net.Uri
import android.util.AttributeSet
import kotlin.math.max

class GKeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : KeyboardView(context, attrs) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(
            "macro_prefs",
            Context.MODE_PRIVATE
        )

    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val iconPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val rect =
        RectF()

    private var baseColor =
        Color.WHITE

    private var clickColor =
        Color.rgb(208, 208, 208)

    private var surfaceColor =
        Color.rgb(230, 232, 235)

    private var textColor =
        Color.rgb(31, 31, 31)

    private var enterColor =
        Color.rgb(66, 133, 244)

    private var highlightColor =
        Color.argb(
            105,
            66,
            133,
            244
        )

    private var clickMode =
        "COLOR"

    private var layoutMode =
        "CUSTOM"

    private var texture: Bitmap? = null

    private var clickTexture: Bitmap? = null

    private var lastTextureUri = ""

    private var lastClickTextureUri = ""

    private val density =
        resources.displayMetrics.density

    init {

        setPreviewEnabled(false)

        setProximityCorrectionEnabled(false)

        isFocusable = true

        isFocusableInTouchMode = true

        setWillNotDraw(false)

        iconPaint.strokeWidth =
            2.2f * density

        iconPaint.style =
            Paint.Style.STROKE

        iconPaint.strokeCap =
            Paint.Cap.ROUND

        iconPaint.strokeJoin =
            Paint.Join.ROUND
    }

    fun refreshAppearance() {

        baseColor =
            parseColor(
                "key_color",
                "#FFFFFF"
            )

        clickColor =
            parseColor(
                "click_color",
                "#D0D0D0"
            )

        surfaceColor =
            parseColor(
                "background_color",
                "#E6E8EB"
            )

        textColor =
            parseColor(
                "text_color",
                "#1F1F1F"
            )

        enterColor =
            parseColor(
                "enter_color",
                "#4285F4"
            )

        highlightColor =
            parseColor(
                "highlight_color",
                "#664285F4"
            )

        clickMode =
            prefs.getString(
                "click_mode",
                "COLOR"
            ) ?: "COLOR"

        layoutMode =
            prefs.getString(
                "layout_mode",
                "CUSTOM"
            ) ?: "CUSTOM"

        loadTexturesIfNeeded()

        setBackgroundColor(
            surfaceColor
        )

        invalidate()
    }

    fun highlightColor(): Int =
        highlightColor

    fun keyForCharacter(
        char: Char
    ): Keyboard.Key? {

        val kbd =
            keyboard ?: return null

        val normalized =
            when (char) {

                'ي' -> 'ی'

                'ى' -> 'ی'

                'ك' -> 'ک'

                else -> char
            }

        return kbd.keys.firstOrNull { key ->

            key.codes.any {

                it == normalized.code
            }
        }
    }

    private fun parseColor(
        key: String,
        fallback: String
    ): Int {

        return try {

            Color.parseColor(
                prefs.getString(
                    key,
                    fallback
                ) ?: fallback
            )

        } catch (_: Exception) {

            Color.parseColor(fallback)
        }
    }

    private fun loadTexturesIfNeeded() {

        val uri =
            prefs.getString(
                "texture_uri",
                ""
            ) ?: ""

        if (uri != lastTextureUri) {

            texture =
                decodeUri(uri)

            lastTextureUri =
                uri
        }

        val clickUri =
            prefs.getString(
                "click_texture_uri",
                ""
            ) ?: ""

        if (
            clickUri !=
            lastClickTextureUri
        ) {

            clickTexture =
                decodeUri(clickUri)

            lastClickTextureUri =
                clickUri
        }
    }

    private fun decodeUri(
        raw: String
    ): Bitmap? {

        if (raw.isBlank()) {
            return null
        }

        return try {

            context.contentResolver
                .openInputStream(
                    Uri.parse(raw)
                )
                ?.use {

                    BitmapFactory
                        .decodeStream(it)
                }

        } catch (_: Exception) {

            null
        }
    }

    override fun onDraw(
        canvas: Canvas
    ) {

        canvas.drawColor(
            surfaceColor
        )

        val kbd =
            keyboard ?: return

        for (key in kbd.keys) {

            drawKey(
                canvas,
                key
            )
        }
    }

    private fun drawKey(
        canvas: Canvas,
        key: Keyboard.Key
    ) {

        val inset =
            2f * density

        rect.set(
            key.x + inset,
            key.y + inset,
            key.x + key.width - inset,
            key.y + key.height - inset
        )

        if (
            rect.width() <= 0f ||
            rect.height() <= 0f
        ) {
            return
        }

        val isEnter =
            key.codes.firstOrNull() == -4

        val pressed =
            key.pressed

        if (isEnter) {

            drawRounded(
                canvas,
                rect,
                if (pressed)
                    clickColor
                else
                    enterColor
            )

            drawEnterIcon(
                canvas,
                rect
            )

            return
        }

        val hasTexture =
            layoutMode != "SAMSUNG" &&
            texture != null

        if (hasTexture) {

            drawBitmapCrop(
                canvas,
                texture!!,
                rect
            )

        } else {

            drawRounded(
                canvas,
                rect,
                if (pressed)
                    clickColor
                else
                    baseColor
            )
        }

        if (
            pressed &&
            clickMode == "TEXTURE" &&
            clickTexture != null
        ) {

            drawBitmapCrop(
                canvas,
                clickTexture!!,
                rect
            )
        }

        val code =
            key.codes.firstOrNull()
                ?: 0

        when {

            code == -5 ->
                drawBackspaceIcon(
                    canvas,
                    rect,
                    pressed
                )

            code == 32 -> Unit

            else ->
                drawLabel(
                    canvas,
                    key,
                    code
                )
        }
    }

    private fun drawRounded(
        canvas: Canvas,
        r: RectF,
        color: Int
    ) {

        paint.style =
            Paint.Style.FILL

        paint.color =
            color

        canvas.drawRoundRect(
            r,
            6f * density,
            6f * density,
            paint
        )
    }

    private fun drawBitmapCrop(
        canvas: Canvas,
        bitmap: Bitmap,
        target: RectF
    ) {

        val save =
            canvas.save()

        val path =
            Path().apply {

                addRoundRect(
                    target,
                    6f * density,
                    6f * density,
                    Path.Direction.CW
                )
            }

        canvas.clipPath(path)

        val scale =
            max(
                target.width() /
                    bitmap.width.toFloat(),

                target.height() /
                    bitmap.height.toFloat()
            )

        val w =
            bitmap.width * scale

        val h =
            bitmap.height * scale

        val left =
            target.centerX() - w / 2f

        val top =
            target.centerY() - h / 2f

        val matrix =
            Matrix().apply {

                setScale(
                    scale,
                    scale
                )

                postTranslate(
                    left,
                    top
                )
            }

        canvas.drawBitmap(
            bitmap,
            matrix,
            paint
        )

        canvas.restoreToCount(
            save
        )
    }

    private fun drawLabel(
        canvas: Canvas,
        key: Keyboard.Key,
        code: Int
    ) {

        val label =
            key.label?.toString()
                ?: key.text?.toString()
                ?: runCatching {

                    String(
                        Character.toChars(
                            code
                        )
                    )

                }.getOrDefault("")

        if (label.isBlank()) {
            return
        }

        paint.style =
            Paint.Style.FILL

        paint.color =
            textColor

        paint.typeface =
            Typeface.create(
                Typeface.DEFAULT,
                Typeface.NORMAL
            )

        paint.textAlign =
            Paint.Align.CENTER

        val sizeSp =
            if (label.length > 1)
                13f
            else
                22f

        paint.textSize =
            sizeSp *
            resources.displayMetrics
                .scaledDensity

        val fm =
            paint.fontMetrics

        val cy =
            key.y +
            key.height / 2f

        val baseline =
            cy -
            (fm.ascent +
                fm.descent) / 2f

        canvas.drawText(
            label,
            key.x +
                key.width / 2f,
            baseline,
            paint
        )
    }

    private fun drawBackspaceIcon(
        canvas: Canvas,
        r: RectF,
        pressed: Boolean
    ) {

        iconPaint.color =
            textColor

        iconPaint.style =
            Paint.Style.STROKE

        iconPaint.strokeWidth =
            2.2f * density

        val p =
            Path()

        val left =
            r.left +
            r.width() * .28f

        val top =
            r.top +
            r.height() * .30f

        val right =
            r.right -
            r.width() * .18f

        val bottom =
            r.bottom -
            r.height() * .30f

        p.moveTo(
            left,
            top
        )

        p.lineTo(
            right,
            top
        )

        p.lineTo(
            right,
            bottom
        )

        p.lineTo(
            left,
            bottom
        )

        p.lineTo(
            r.left +
                r.width() * .12f,
            r.centerY()
        )

        p.close()

        canvas.drawPath(
            p,
            iconPaint
        )

        canvas.drawLine(
            r.centerX() -
                2f * density,

            r.centerY() -
                4f * density,

            r.centerX() +
                5f * density,

            r.centerY() +
                4f * density,

            iconPaint
        )

        canvas.drawLine(
            r.centerX() +
                5f * density,

            r.centerY() -
                4f * density,

            r.centerX() -
                2f * density,

            r.centerY() +
                4f * density,

            iconPaint
        )
    }

    private fun drawEnterIcon(
        canvas: Canvas,
        r: RectF
    ) {

        iconPaint.color =
            Color.WHITE

        iconPaint.style =
            Paint.Style.STROKE

        iconPaint.strokeWidth =
            2.2f * density

        val y =
            r.centerY()

        val left =
            r.left +
            r.width() * .24f

        val right =
            r.right -
            r.width() * .20f

        canvas.drawLine(
            left,
            y - 4f * density,
            right - 8f * density,
            y - 4f * density,
            iconPaint
        )

        canvas.drawLine(
            right - 8f * density,
            y - 4f * density,
            right,
            y + 2f * density,
            iconPaint
        )

        canvas.drawLine(
            right,
            y + 2f * density,
            right - 8f * density,
            y + 8f * density,
            iconPaint
        )

        canvas.drawLine(
            right,
            y + 2f * density,
            left + 4f * density,
            y + 2f * density,
            iconPaint
        )
    }
}
