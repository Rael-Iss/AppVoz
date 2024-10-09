package com.example.appvoz

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private var permissionToRecordAccepted = false
    private val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)

        val micButton: ImageButton = findViewById(R.id.micButton)
        val mainLayout: RelativeLayout = findViewById(R.id.mainLayout)

        micButton.setOnClickListener {
            if (permissionToRecordAccepted) {
                startVoiceRecognition()
            } else {
                Toast.makeText(this, "Permiso de grabación de audio denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES") // Configura el idioma a español
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)

        try {
            startActivityForResult(intent, REQUEST_RECORD_AUDIO_PERMISSION)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Reconocimiento de voz no soportado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION && resultCode == RESULT_OK) {
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            results?.let {
                val recognizedColor = it[0]
                changeBackgroundColor(recognizedColor)
            }
        }
    }

    private fun changeBackgroundColor(colorName: String) {
        val mainLayout: RelativeLayout = findViewById(R.id.mainLayout)
        val color = when (colorName.lowercase()) {
            "rojo" -> Color.RED
            "verde" -> Color.GREEN
            "azul" -> Color.BLUE
            "amarillo" -> Color.YELLOW
            else -> Color.WHITE // color por defecto
        }
        mainLayout.setBackgroundColor(color)
        Toast.makeText(this, "Color reconocido: $colorName", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_RECORD_AUDIO_PERMISSION -> {
                permissionToRecordAccepted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            }
        }
        if (!permissionToRecordAccepted) {
            Toast.makeText(this, "Permiso de grabación de audio denegado", Toast.LENGTH_SHORT).show()
            finish() // Cierra la aplicación si no se concede el permiso
        }
    }
}
