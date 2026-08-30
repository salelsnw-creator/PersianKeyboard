package key.boo.ard.ali

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity :
    AppCompatActivity() {

    private val prefs by lazy {

        getSharedPreferences(
            "macro_prefs",
            MODE_PRIVATE
        )
    }

    private lateinit var macroEdit:
        EditText

    private lateinit var modeGroup:
        RadioGroup

    private lateinit var sizeGroup:
        RadioGroup

    private lateinit var layoutGroup:
        RadioGroup

    private lateinit var clickGroup:
        RadioGroup

    private lateinit var speedValue:
        TextView

    private lateinit var enterValue:
        TextView

    private lateinit var startValue:
        TextView

    private lateinit var keyColor:
        EditText

    private lateinit var clickColor:
        EditText

    private lateinit var bgColor:
        EditText

    private lateinit var textColor:
        EditText

    private lateinit var hapticCheck:
        CheckBox

    private lateinit var soundCheck:
        CheckBox

    private val PICK_BASE_TEXTURE =
        1001

    private val PICK_CLICK_TEXTURE =
        1002

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        buildUi()

        loadUi()
    }

    private fun dp(
        value: Int
    ): Int {

        return (
            value *
            resources.displayMetrics
                .density
        ).toInt()
    }

    private fun label(
        text: String,
        size: Float = 15f
    ): TextView {

        return TextView(this).apply {

            this.text = text

            textSize = size

            setTextColor(
                Color.rgb(
                    31,
                    31,
                    31
                )
            )

            setPadding(
                dp(4),
                dp(8),
                dp(4),
                dp(4)
            )
        }
    }

    private fun section(
        text: String
    ): TextView {

        return TextView(this).apply {

            this.text = text

            textSize = 19f

            setTextColor(
                Color.rgb(
                    40,
                    65,
                    100
                )
            )

            setPadding(
                0,
                dp(18),
                0,
                dp(8)
            )
        }
    }

    private fun radio(
        id: Int,
        text: String
    ): RadioButton {

        return RadioButton(this).apply {

            this.id = id

            this.text = text

            textSize = 15f
        }
    }

    private fun seek(
        min: Int,
        max: Int,
        current: Int,
        onChanged: (Int) -> Unit
    ): SeekBar {

        return SeekBar(this).apply {

            this.max =
                max - min

            progress =
                current
                    .coerceIn(
                        min,
                        max
                    ) - min

            setOnSeekBarChangeListener(

                object :
                    SeekBar
                    .OnSeekBarChangeListener {

                    override fun
                        onProgressChanged(
                            sb: SeekBar?,
                            p: Int,
                            fromUser: Boolean
                        ) {

                        onChanged(
                            p + min
                        )
                    }

                    override fun
                        onStartTrackingTouch(
                            sb: SeekBar?
                        ) {}

                    override fun
                        onStopTrackingTouch(
                            sb: SeekBar?
                        ) {}
                }
            )
        }
    }

    private fun buildUi() {

        val scroll =
            android.widget.ScrollView(
                this
            )

        val box =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL

                setPadding(
                    dp(16),
                    dp(12),
                    dp(16),
                    dp(24)
                )
            }

        scroll.addView(box)

        box.addView(
            TextView(this).apply {

                text =
                    "SALAR @ Ditayl"

                textSize = 27f

                gravity =
                    Gravity.CENTER_HORIZONTAL

                setTextColor(
                    Color.rgb(
                        31,
                        31,
                        31
                    )
                )

                setPadding(
                    0,
                    dp(8),
                    0,
                    dp(4)
                )
            }
        )

        box.addView(
            label(
                "پنل کامل کیبورد و Auto Typer • بدون Toast و ذخیره‌سازی بی‌صدا",
                13f
            )
        )

        box.addView(
            section(
                "۱) متن‌های آماده"
            )
        )

        macroEdit =
            EditText(this).apply {

                hint =
                    "هر خط یک کلمه یا جمله مستقل"

                minLines = 5

                maxLines = 12

                gravity =
                    Gravity.TOP or
                    Gravity.START

                setTextSize(16f)
            }

        box.addView(
            macroEdit,
            ViewGroup.LayoutParams(
                -1,
                dp(180)
            )
        )

        box.addView(
            section(
                "۲) حالت Auto Typer"
            )
        )

        modeGroup =
            RadioGroup(this).apply {

                orientation =
                    RadioGroup.HORIZONTAL

                addView(
                    radio(
                        1,
                        "کامل"
                    )
                )

                addView(
                    radio(
                        2,
                        "حرف‌به‌حرف"
                    )
                )
            }

        box.addView(
            modeGroup
        )

        box.addView(
            section(
                "۳) سرعت هر حرف — 10 تا 300ms"
            )
        )

        speedValue =
            label(
                "",
                14f
            )

        box.addView(
            speedValue
        )

        box.addView(
            seek(
                10,
                300,
                45
            ) {

                speedValue.text =
                    "سرعت: ${it}ms"
            }
        )

        box.addView(
            section(
                "۴) تأخیر Enter — 0 تا 500ms"
            )
        )

        enterValue =
            label(
                "",
                14f
            )

        box.addView(
            enterValue
        )

        box.addView(
            seek(
                0,
                500,
                0
            ) {

                enterValue.text =
                    "تأخیر Enter: ${it}ms"
            }
        )

        box.addView(
            section(
                "۵) تأخیر شروع Auto — 0 تا 500ms"
            )
        )

        startValue =
            label(
                "",
                14f
            )

        box.addView(
            startValue
        )

        box.addView(
            seek(
                0,
                500,
                0
            ) {

                startValue.text =
                    "تأخیر شروع: ${it}ms"
            }
        )

        box.addView(
            section(
                "۶) اندازه کیبورد"
            )
        )

        sizeGroup =
            RadioGroup(this).apply {

                orientation =
                    RadioGroup.HORIZONTAL

                addView(
                    radio(
                        10,
                        "کوچک"
                    )
                )

                addView(
                    radio(
                        11,
                        "متوسط"
                    )
                )

                addView(
                    radio(
                        12,
                        "بزرگ"
                    )
                )
            }

        box.addView(
            sizeGroup
        )

        box.addView(
            section(
                "۷) چیدمان"
            )
        )

        layoutGroup =
            RadioGroup(this).apply {

                orientation =
                    RadioGroup.HORIZONTAL

                addView(
                    radio(
                        20,
                        "Samsung"
                    )
                )

                addView(
                    radio(
                        21,
                        "Custom"
                    )
                )
            }

        box.addView(
            layoutGroup
        )

        box.addView(
            section(
                "۸) رنگ‌های کیبورد"
            )
        )

        keyColor =
            colorEdit(
                "رنگ کلیدها — مثلاً #FFFFFF"
            )

        clickColor =
            colorEdit(
                "رنگ کلیک کلید — مثلاً #D0D0D0"
            )

        bgColor =
            colorEdit(
                "رنگ پشت‌زمینه — مثلاً #E6E8EB"
            )

        textColor =
            colorEdit(
                "رنگ کلمات — مثلاً #1F1F1F"
            )

        box.addView(
            keyColor
        )

        box.addView(
            clickColor
        )

        box.addView(
            bgColor
        )

        box.addView(
            textColor
        )

        box.addView(
            section(
                "۹) نوع کلیک"
            )
        )

        clickGroup =
            RadioGroup(this).apply {

                orientation =
                    RadioGroup.HORIZONTAL

                addView(
                    radio(
                        30,
                        "رنگ کلیک"
                    )
                )

                addView(
                    radio(
                        31,
                        "تکسچر کلیک"
                    )
                )
            }

        box.addView(
            clickGroup
        )

        box.addView(
            section(
                "۱۰) تکسچر"
            )
        )

        box.addView(
            Button(this).apply {

                text =
                    "انتخاب تکسچر خود کیبورد"

                setOnClickListener {

                    pickImage(
                        PICK_BASE_TEXTURE
                    )
                }
            }
        )

        box.addView(
            Button(this).apply {

                text =
                    "انتخاب تکسچر کلیک"

                setOnClickListener {

                    pickImage(
                        PICK_CLICK_TEXTURE
                    )
                }
            }
        )

        box.addView(
            label(
                "برش هوشمند آفلاین: تصویر هنگام رسم به‌صورت خودکار Center-Crop می‌شود تا لبه‌ها پر شوند. این بخش مدل هوش مصنوعی خارجی ندارد.",
                12f
            )
        )

        box.addView(
            section(
                "۱۱) بازخورد"
            )
        )

        hapticCheck =
            CheckBox(this).apply {

                text =
                    "ویبره کلیک"
            }

        soundCheck =
            CheckBox(this).apply {

                text =
                    "صدای کلیک"
            }

        box.addView(
            hapticCheck
        )

        box.addView(
            soundCheck
        )

        box.addView(
            section(
                "۱۲) کنترل شمارنده"
            )
        )

        box.addView(
            Button(this).apply {

                text =
                    "ریست شمارنده Auto Typer"

                setOnClickListener {

                    prefs.edit()

                        .putInt(
                            "macro_line_index",
                            0
                        )

                        .putInt(
                            "macro_char_index",
                            0
                        )

                        .apply()
                }
            }
        )

        box.addView(
            section(
                "۱۳) فعال‌سازی کیبورد"
            )
        )

        box.addView(
            Button(this).apply {

                text =
                    "باز کردن تنظیمات کیبوردهای اندروید"

                setOnClickListener {

                    startActivity(
                        Intent(
                            Settings
                                .ACTION_INPUT_METHOD_SETTINGS
                        )
                    )
                }
            }
        )

        box.addView(
            Button(this).apply {

                text =
                    "ذخیره تنظیمات"

                textSize = 17f

                setOnClickListener {

                    saveUi()
                }
            }
        )

        box.addView(
            label(
                "راهنما: در حالت FULL با یک لمس کل آیتم ارسال می‌شود و سپس Enter واقعی اندروید فرستاده می‌شود. در حالت CHAR با یک لمس چرخه داخلی شروع می‌شود و هر حرف با فاصله تعیین‌شده commitText می‌شود؛ بعد Enter واقعی ارسال و شمارنده چرخشی جلو می‌رود.",
                12f
            )
        )

        setContentView(
            scroll
        )
    }

    private fun colorEdit(
        hint: String
    ): EditText {

        return EditText(this).apply {

            this.hint =
                hint

            setTextSize(15f)

            singleLine = true

            setPadding(
                dp(8),
                dp(4),
                dp(8),
                dp(4)
            )
        }
    }

    private fun loadUi() {

        macroEdit.setText(
            prefs.getString(
                "macro_items",
                ""
            ) ?: ""
        )

        val mode =
            prefs.getString(
                "auto_mode",
                "FULL"
            ) ?: "FULL"

        modeGroup.check(
            if (
                mode == "CHAR"
            )
                2
            else
                1
        )

        val size =
            prefs.getString(
                "keyboard_size",
                "MEDIUM"
            ) ?: "MEDIUM"

        sizeGroup.check(

            when (size) {

                "SMALL" ->
                    10

                "LARGE" ->
                    12

                else ->
                    11
            }
        )

        val layout =
            prefs.getString(
                "layout_mode",
                "CUSTOM"
            ) ?: "CUSTOM"

        layoutGroup.check(

            if (
                layout ==
                "SAMSUNG"
            )
                20
            else
                21
        )

        val click =
            prefs.getString(
                "click_mode",
                "COLOR"
            ) ?: "COLOR"

        clickGroup.check(

            if (
                click ==
                "TEXTURE"
            )
                31
            else
                30
        )

        keyColor.setText(
            prefs.getString(
                "key_color",
                "#FFFFFF"
            )
        )

        clickColor.setText(
            prefs.getString(
                "click_color",
                "#D0D0D0"
            )
        )

        bgColor.setText(
            prefs.getString(
                "background_color",
                "#E6E8EB"
            )
        )

        textColor.setText(
            prefs.getString(
                "text_color",
                "#1F1F1F"
            )
        )

        hapticCheck.isChecked =
            prefs.getBoolean(
                "haptic_enabled",
                false
            )

        soundCheck.isChecked =
            prefs.getBoolean(
                "sound_enabled",
                false
            )

        val speed =
            prefs.getInt(
                "auto_speed_ms",
                45
            )

        val enter =
            prefs.getInt(
                "auto_enter_delay_ms",
                0
            )

        val start =
            prefs.getInt(
                "auto_start_delay_ms",
                0
            )

        speedValue.text =
            "سرعت: ${speed}ms"

        enterValue.text =
            "تأخیر Enter: ${enter}ms"

        startValue.text =
            "تأخیر شروع: ${start}ms"
    }

    private fun saveUi() {

        val mode =
            if (
                modeGroup
                    .checkedRadioButtonId ==
                2
            )
                "CHAR"
            else
                "FULL"

        val size =
            when (
                sizeGroup
                    .checkedRadioButtonId
            ) {

                10 ->
                    "SMALL"

                12 ->
                    "LARGE"

                else ->
                    "MEDIUM"
            }

        val layout =
            if (
                layoutGroup
                    .checkedRadioButtonId ==
                20
            )
                "SAMSUNG"
            else
                "CUSTOM"

        val click =
            if (
                clickGroup
                    .checkedRadioButtonId ==
                31
            )
                "TEXTURE"
            else
                "COLOR"

        val speeds =
            mutableListOf<Int>()

        collectSeekValues(
            window.decorView,
            speeds
        )

        val speed =
            speeds.getOrNull(0)
                ?.coerceIn(
                    10,
                    300
                )
                ?: 45

        val enter =
            speeds.getOrNull(1)
                ?.coerceIn(
                    0,
                    500
                )
                ?: 0

        val start =
            speeds.getOrNull(2)
                ?.coerceIn(
                    0,
                    500
                )
                ?: 0

        prefs.edit()

            .putString(
                "macro_items",
                macroEdit.text
                    .toString()
                    .replace(
                        "\r\n",
                        "\n"
                    )
            )

            .putString(
                "auto_mode",
                mode
            )

            .putInt(
                "auto_speed_ms",
                speed
            )

            .putInt(
                "auto_enter_delay_ms",
                enter
            )

            .putInt(
                "auto_start_delay_ms",
                start
            )

            .putString(
                "keyboard_size",
                size
            )

            .putString(
                "layout_mode",
                layout
            )

            .putString(
                "click_mode",
                click
            )

            .putString(
                "key_color",
                safeColor(
                    keyColor.text.toString(),
                    "#FFFFFF"
                )
            )

            .putString(
                "click_color",
                safeColor(
                    clickColor.text.toString(),
                    "#D0D0D0"
                )
            )

            .putString(
                "background_color",
                safeColor(
                    bgColor.text.toString(),
                    "#E6E8EB"
                )
            )

            .putString(
                "text_color",
                safeColor(
                    textColor.text.toString(),
                    "#1F1F1F"
                )
            )

            .putBoolean(
                "haptic_enabled",
                hapticCheck.isChecked
            )

            .putBoolean(
                "sound_enabled",
                soundCheck.isChecked
            )

            .putInt(
                "macro_line_index",
                0
            )

            .putInt(
                "macro_char_index",
                0
            )

            .apply()
    }

    private fun safeColor(
        raw: String,
        fallback: String
    ): String {

        return try {

            Color.parseColor(
                raw.trim()
            )

            raw.trim()

        } catch (_: Exception) {

            fallback
        }
    }

    private fun collectSeekValues(
        view: View,
        out: MutableList<Int>
    ) {

        if (
            view is SeekBar
        ) {

            val min =
                when (out.size) {

                    0 -> 10

                    else -> 0
                }

            out +=
                view.progress +
                min
        }

        if (
            view is ViewGroup
        ) {

            for (
                i in 0 until
                    view.childCount
            ) {

                collectSeekValues(
                    view.getChildAt(i),
                    out
                )
            }
        }
    }

    private fun pickImage(
        requestCode: Int
    ) {

        val intent =
            Intent(
                Intent.ACTION_OPEN_DOCUMENT
            ).apply {

                addCategory(
                    Intent.CATEGORY_OPENABLE
                )

                type =
                    "image/*"

                addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                addFlags(
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                )
            }

        startActivityForResult(
            intent,
            requestCode
        )
    }

    @Deprecated(
        "Android legacy activity result API is intentionally used for API 26 compatibility."
    )
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (
            resultCode !=
            Activity.RESULT_OK
        ) {
            return
        }

        val uri: Uri =
            data?.data
                ?: return

        try {

            contentResolver
                .takePersistableUriPermission(

                    uri,

                    Intent
                        .FLAG_GRANT_READ_URI_PERMISSION
                )

        } catch (_: Exception) {
        }

        prefs.edit()

            .putString(

                if (
                    requestCode ==
                    PICK_CLICK_TEXTURE
                )
                    "click_texture_uri"
                else
                    "texture_uri",

                uri.toString()
            )

            .apply()
    }
}
