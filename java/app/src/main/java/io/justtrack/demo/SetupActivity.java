package io.justtrack.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import java.util.UUID;

public class SetupActivity extends Activity {

    SwitchCompat manualStartSwitch;
    EditText userIdEditText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        manualStartSwitch = findViewById(R.id.manualStartSwitch);
        userIdEditText = findViewById(R.id.userIdEditText);
    }

    public void start(View view) {
        boolean isManualStart = manualStartSwitch.isChecked();
        String userId = null;

        if (!userIdEditText.getText().toString().isEmpty()) {
            userId = userIdEditText.getText().toString();
        }

        Intent startIntent = new Intent(this, MainActivity.class);
        startIntent.putExtra("isManualStart", isManualStart);
        if (userId != null) {
            startIntent.putExtra("userId", userId);
        }

        startActivity(startIntent);
    }

    public void generateRandomUserId(View view) {
        String randomId = UUID.randomUUID().toString();
        userIdEditText.setText(randomId);
    }
}
