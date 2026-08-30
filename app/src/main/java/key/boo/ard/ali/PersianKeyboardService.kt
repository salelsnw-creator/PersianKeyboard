package key.boo.ard.ali

import android.graphics.Color
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.LinearLayout
import android.widget.TextView
import android.content.SharedPreferences

class PersianKeyboardService :
    InputMethodService() {

    private lateinit var prefs:
        SharedPreferences

    private val handler =
        Handler(
            Looper.getMainLooper()
        )

    private var rootView:
        LinearLayout? = null

    private var keyboardView:
        GKeyboardView? = null

    private var highlightView:
        HighlightOverlayView? = null

    private var progressView:
        TextView? = null

    private var macroItems:
        List<String> =
            emptyList()

    private var lineIndex =
        0

    private var charIndex =
        0

    private var autoRunning =
        false

    private var autoRunnable:
        Runnable? = null

    override fun onCreate() {

        super.onCreate()

        prefs =
            getSharedPreferences(
                "macro_prefs",
                MODE_PRIVATE
            )

        loadState()
    }

    override fun onCreateInputView():
        View {

        val root =
            layoutInflater.inflate(
                R.layout.ime_view,
                null,
                false
            ) as LinearLayout

        rootView =
            root

        progressView =
            root.findViewById(
                R.id.progress_text
            )

        keyboardView =
            root.findViewById(
                R.id.keyboard_view
            )

        highlightView =
            root.findViewById(
                R.id.highlight_overlay
            )

        keyboardView?.setOnKeyboardActionListener(
            object :
                android.inputmethodservice
                .KeyboardView
                .OnKeyboardActionListener {

                override fun onPress(
                    primaryCode: Int
                ) {

                    keyboardView?.invalidate()
                }

                override fun onRelease(
                    primaryCode: Int
                ) {

                    keyboardView?.invalidate()
                }

                override fun onKey(
                    primaryCode: Int,
                    keyCodes: IntArray
                ) {

                    handleKey(
                        primaryCode
                    )
                }

                override fun onText(
                    text: CharSequence
                ) {

                    currentInputConnection
                        ?.commitText(
                            text,
                            1
                        )
                }

                override fun swipeLeft() {}

                override fun swipeRight() {}

                override fun swipeDown() {}

                override fun swipeUp() {}
            }
        )

        rebuildKeyboard()

        updateProgress()

        return root
    }

    override fun onStartInputView(
        info:
            android.view.inputmethod.EditorInfo?,
        restarting: Boolean
    ) {

        super.onStartInputView(
            info,
            restarting
        )

        loadState()

        rebuildKeyboard()

        updateProgress()
    }

    private fun rebuildKeyboard() {

        val kv =
            keyboardView
                ?: return

        val size =
            prefs.getString(
                "keyboard_size",
                "MEDIUM"
            ) ?: "MEDIUM"

        val mode =
            prefs.getString(
                "layout_mode",
                "CUSTOM"
            ) ?: "CUSTOM"

        val resId =
            when (
                mode to size
            ) {

                "SAMSUNG" to "SMALL" ->
                    R.xml.keyboard_samsung_small

                "SAMSUNG" to "LARGE" ->
                    R.xml.keyboard_samsung_large

                "SAMSUNG" to "MEDIUM" ->
                    R.xml.keyboard_samsung_medium

                "CUSTOM" to "SMALL" ->
                    R.xml.keyboard_small

                "CUSTOM" to "LARGE" ->
                    R.xml.keyboard_large

                else ->
                    R.xml.keyboard_medium
            }

        kv.keyboard =
            Keyboard(
                this,
                resId
            )

        kv.setPreviewEnabled(
            false
        )

        kv.refreshAppearance()
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

            Color.parseColor(
                fallback
            )
        }
    }

    private fun loadState() {

        macroItems =
            (
                prefs.getString(
                    "macro_items",
                    ""
                ) ?: ""
            )
                .replace(
                    "\r\n",
                    "\n"
                )
                .split('\n')
                .map {
                    it.trimEnd()
                }
                .filter {
                    it.isNotEmpty()
                }

        lineIndex =
            prefs.getInt(
                "macro_line_index",
                0
            )
                .coerceIn(
                    0,
                    maxOf(
                        0,
                        macroItems.size - 1
                    )
                )

        charIndex =
            prefs.getInt(
                "macro_char_index",
                0
            )
                .coerceAtLeast(0)
    }

    private fun saveState() {

        prefs.edit()

            .putInt(
                "macro_line_index",
                lineIndex
            )

            .putInt(
                "macro_char_index",
                charIndex
            )

            .apply()
    }

    private fun handleKey(
        code: Int
    ) {

        performFeedback()

        when (code) {

            -10 ->
                startMacro()

            -11 ->
                resetMacro()

            -13 -> {

                val current =
                    prefs.getString(
                        "layout_mode",
                        "CUSTOM"
                    ) ?: "CUSTOM"

                prefs.edit()
                    .putString(
                        "layout_mode",
                        if (
                            current ==
                            "CUSTOM"
                        )
                            "SAMSUNG"
                        else
                            "CUSTOM"
                    )
                    .apply()

                rebuildKeyboard()
            }

            -5 ->
                handleBackspace()

            -4 ->
                sendRealEnter()

            32 ->
                currentInputConnection
                    ?.commitText(
                        " ",
                        1
                    )

            else -> {

                if (code != 0) {

                    val chars =
                        runCatching {

                            String(
                                Character
                                    .toChars(
                                        code
                                    )
                            )

                        }.getOrNull()

                    if (
                        !chars.isNullOrEmpty()
                    ) {

                        currentInputConnection
                            ?.commitText(
                                chars,
                                1
                            )
                    }
                }
            }
        }
    }

    private fun handleBackspace() {

        currentInputConnection
            ?.deleteSurroundingText(
                1,
                0
            )
    }

    private fun sendRealEnter() {

        val ic =
            currentInputConnection
                ?: return

        ic.sendKeyEvent(
            KeyEvent(
                KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_ENTER
            )
        )

        ic.sendKeyEvent(
            KeyEvent(
                KeyEvent.ACTION_UP,
                KeyEvent.KEYCODE_ENTER
            )
        )
    }

    private fun startMacro() {

        loadState()

        if (
            autoRunning ||
            macroItems.isEmpty()
        ) {
            return
        }

        if (
            lineIndex >=
            macroItems.size
        ) {

            lineIndex = 0
        }

        if (
            lineIndex < 0
        ) {

            lineIndex = 0
        }

        charIndex =
            charIndex.coerceIn(
                0,
                macroItems[
                    lineIndex
                ].length
            )

        val mode =
            prefs.getString(
                "auto_mode",
                "FULL"
            ) ?: "FULL"

        if (
            mode == "CHAR"
        ) {

            startCharMode()

        } else {

            startFullMode()
        }
    }

    private fun startFullMode() {

        if (
            macroItems.isEmpty()
        ) {
            return
        }

        val text =
            macroItems[
                lineIndex
            ]

        currentInputConnection
            ?.commitText(
                text,
                1
            )

        val enterDelay =
            prefs.getInt(
                "auto_enter_delay_ms",
                0
            )
                .coerceIn(
                    0,
                    500
                )

        autoRunning =
            true

        updateProgress()

        handler.postDelayed({

            sendRealEnter()

            advanceItem()

            autoRunning =
                false

            updateProgress()

        }, enterDelay.toLong())
    }

    private fun startCharMode() {

        autoRunning =
            true

        updateProgress()

        val startDelay =
            prefs.getInt(
                "auto_start_delay_ms",
                0
            )
                .coerceIn(
                    0,
                    500
                )

        autoRunnable =
            object : Runnable {

                override fun run() {

                    if (
                        !autoRunning ||
                        macroItems.isEmpty()
                    ) {
                        return
                    }

                    val item =
                        macroItems[
                            lineIndex
                        ]

                    if (
                        charIndex >=
                        item.length
                    ) {

                        sendRealEnter()

                        advanceItem()

                        autoRunning =
                            false

                        updateProgress()

                        return
                    }

                    val c =
                        item[
                            charIndex
                        ]

                    currentInputConnection
                        ?.commitText(
                            c.toString(),
                            1
                        )

                    highlightChar(c)

                    charIndex++

                    saveState()

                    updateProgress()

                    val speed =
                        prefs.getInt(
                            "auto_speed_ms",
                            45
                        )
                            .coerceIn(
                                10,
                                300
                            )

                    handler.postDelayed(
                        this,
                        speed.toLong()
                    )
                }
            }

        handler.postDelayed(
            autoRunnable!!,
            startDelay.toLong()
        )
    }

    private fun advanceItem() {

        if (
            macroItems.isEmpty()
        ) {

            lineIndex = 0

            charIndex = 0

        } else {

            lineIndex =
                (
                    lineIndex + 1
                ) %
                macroItems.size

            charIndex = 0
        }

        saveState()
    }

    private fun resetMacro() {

        autoRunning =
            false

        autoRunnable?.let {

            handler.removeCallbacks(
                it
            )
        }

        autoRunnable =
            null

        handler.removeCallbacksAndMessages(
            null
        )

        lineIndex = 0

        charIndex = 0

        saveState()

        highlightView?.clear()

        updateProgress()
    }

    private fun highlightChar(
        c: Char
    ) {

        val kv =
            keyboardView
                ?: return

        val overlay =
            highlightView
                ?: return

        val key =
            kv.keyForCharacter(c)
                ?: return

        overlay.showKey(
            key,
            kv.highlightColor(),
            90L
        )
    }

    private fun updateProgress() {

        if (
            !::prefs.isInitialized
        ) {
            return
        }

        val count =
            macroItems.size

        val current =
            if (count == 0)
                0
            else
                (
                    lineIndex + 1
                ).coerceAtMost(
                    count
                )

        val charPart =
            if (
                count > 0 &&
                lineIndex < count &&
                charIndex > 0
            ) {

                " • حرف: $charIndex/${macroItems[lineIndex].length}"

            } else {
                ""
            }

        progressView?.text =
            "SALAR @ Ditayl  •  پیشرفت: $current/$count$charPart"
    }

    private fun performFeedback() {

        if (
            prefs.getBoolean(
                "haptic_enabled",
                false
            )
        ) {

            val vibrator =
                getSystemService(
                    VIBRATOR_SERVICE
                ) as Vibrator

            if (
                Build.VERSION.SDK_INT >= 26
            ) {

                vibrator.vibrate(
                    VibrationEffect
                        .createOneShot(
                            8L,
                            35
                        )
                )

            } else {

                @Suppress(
                    "DEPRECATION"
                )

                vibrator.vibrate(
                    8L
                )
            }
        }

        if (
            prefs.getBoolean(
                "sound_enabled",
                false
            )
        ) {

            val audio =
                getSystemService(
                    AUDIO_SERVICE
                ) as AudioManager

            audio.playSoundEffect(
                AudioManager.FX_KEY_CLICK
            )
        }
    }

    override fun onDestroy() {

        autoRunning =
            false

        autoRunnable?.let {

            handler.removeCallbacks(
                it
            )
        }

        handler.removeCallbacksAndMessages(
            null
        )

        super.onDestroy()
    }
    }
