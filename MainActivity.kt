package com.arcer.ai

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.widget.*
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private var recognizer: SpeechRecognizer? = null
    private var tts: TextToSpeech? = null
    private var femaleVoice = false
    private lateinit var status: TextView
    private lateinit var answer: TextView
    private lateinit var listen: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(5, 10, 24)
        window.navigationBarColor = Color.rgb(5, 10, 24)
        tts = TextToSpeech(this, this)
        buildUi()
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 10)
        }
    }

    private fun buildUi() {
        val bg = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(28, 40, 28, 24)
            setBackgroundColor(Color.rgb(5, 10, 24))
        }
        val title = TextView(this).apply {
            text = "ARCER AI"
            textSize = 32f
            setTextColor(Color.rgb(65, 170, 255))
            gravity = Gravity.CENTER
        }
        val sub = TextView(this).apply {
            text = "YOUR PERSONAL VOICE ASSISTANT"
            textSize = 11f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }
        status = TextView(this).apply {
            text = "Ready • Tap the button to speak"
            textSize = 15f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 36, 0, 18)
        }
        listen = Button(this).apply {
            text = "🎙  START LISTENING"
            setOnClickListener { startListening() }
        }
        val voice = Button(this).apply {
            text = "VOICE: ${if (femaleVoice) "FEMALE" else "MALE"}  •  CHANGE"
            setOnClickListener {
                femaleVoice = !femaleVoice
                text = "VOICE: ${if (femaleVoice) "FEMALE" else "MALE"}  •  CHANGE"
                speak("Voice preference changed")
            }
        }
        answer = TextView(this).apply {
            text = "Hello! I am ARCER. Say a command after tapping Start Listening."
            textSize = 18f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(8, 36, 8, 30)
        }
        val note = TextView(this).apply {
            text = "MVP • বাংলা + English • Basic offline commands"
            textSize = 12f
            setTextColor(Color.GRAY)
            gravity = Gravity.CENTER
        }
        bg.addView(title, LinearLayout.LayoutParams(-1, -2))
        bg.addView(sub, LinearLayout.LayoutParams(-1, -2))
        bg.addView(status, LinearLayout.LayoutParams(-1, -2))
        bg.addView(listen, LinearLayout.LayoutParams(-1, -2))
        bg.addView(voice, LinearLayout.LayoutParams(-1, -2))
        bg.addView(answer, LinearLayout.LayoutParams(-1, 0, 1f))
        bg.addView(note, LinearLayout.LayoutParams(-1, -2))
        setContentView(bg)
    }

    private fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            status.text = "Speech recognition is not available on this device."
            return
        }
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 10)
            return
        }
        recognizer?.destroy()
        recognizer = SpeechRecognizer.createSpeechRecognizer(this)
        recognizer?.setRecognitionListener(object : android.speech.RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { status.text = "Listening…"; listen.text = "LISTENING…" }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { listen.text = "🎙  START LISTENING" }
            override fun onError(error: Int) {
                status.text = "Didn't catch that. Tap and try again."
                listen.text = "🎙  START LISTENING"
            }
            override fun onResults(results: Bundle?) {
                listen.text = "🎙  START LISTENING"
                val words = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull().orEmpty()
                handleCommand(words)
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn-BD")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "bn-BD")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "হে ARCER, বলুন")
        }
        recognizer?.startListening(intent)
    }

    private fun handleCommand(raw: String) {
        val s = raw.lowercase(Locale.ROOT)
        status.text = "You said: $raw"
        val reply = when {
            s.contains("flash") || s.contains("টর্চ") || s.contains("লাইট") ->
                "ফ্ল্যাশলাইট নিয়ন্ত্রণের সুবিধা পরের সংস্করণে যোগ হবে।"
            s.contains("time") || s.contains("সময়") || s.contains("কয়টা") ->
                "এখন সময় ${java.text.SimpleDateFormat("hh:mm a", Locale.getDefault()).format(java.util.Date())}"
            s.contains("hello") || s.contains("হ্যালো") || s.contains("হাই") ->
                "হ্যালো! আমি ARCER। কীভাবে সাহায্য করতে পারি?"
            s.contains("open") || s.contains("খোলো") ->
                "কোন অ্যাপটি খুলতে চান? অ্যাপের নাম বলুন।"
            else -> "আমি শুনেছি: $raw। এই কমান্ডটি এখনো শেখানো হয়নি।"
        }
        answer.text = reply
        speak(reply)
    }

    private fun speak(text: String) {
        val language = tts?.setLanguage(Locale("bn", "BD"))
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "arcer_reply")
    }

    override fun onInit(statusCode: Int) {
        if (statusCode == TextToSpeech.SUCCESS) {
            tts?.language = Locale("bn", "BD")
        }
    }

    override fun onDestroy() {
        recognizer?.destroy()
        tts?.shutdown()
        super.onDestroy()
    }
}
