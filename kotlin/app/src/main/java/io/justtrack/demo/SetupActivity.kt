package io.justtrack.demo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.SwitchCompat
import java.util.UUID


class SetupActivity : Activity() {
    private lateinit var manualStartSwitch: SwitchCompat
    private lateinit var userIdEditText: EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        manualStartSwitch = findViewById(R.id.manualStartSwitch)
        userIdEditText = findViewById(R.id.userIdEditText)
    }

    fun start(view: View?) {
        val isManualStart = manualStartSwitch.isChecked
        var userId: String? = null

        if (userIdEditText.text.toString().isNotEmpty()) {
            userId = userIdEditText.text.toString()
        }

        val startIntent = Intent(this, MainActivity::class.java)
        startIntent.putExtra("isManualStart", isManualStart)
        if (userId != null) {
            startIntent.putExtra("userId", userId)
        }

        startActivity(startIntent)
    }

    fun generateRandomUserId(view: View?) {
        val randomId = UUID.randomUUID().toString()
        userIdEditText.setText(randomId)
    }
}
