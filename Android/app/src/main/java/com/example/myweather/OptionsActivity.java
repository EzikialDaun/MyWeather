package com.example.myweather;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;

import java.time.LocalDateTime;

public class OptionsActivity extends AppCompatActivity {
    private RadioButton radioAuto;
    private RadioButton radioSpring;
    private RadioButton radioSummer;
    private RadioButton radioAutumn;
    private RadioButton radioWinter;
    private Button btnApply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        // 타이틀 바 제거
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        radioAuto = findViewById(R.id.radioAuto);
        radioSpring = findViewById(R.id.radioSpring);
        radioSummer = findViewById(R.id.radioSummer);
        radioAutumn = findViewById(R.id.radioAutumn);
        radioWinter = findViewById(R.id.radioWinter);
        btnApply = findViewById(R.id.btnApply);

        if (((AppManager) getApplication()).isAutoBg()) {
            radioAuto.setChecked(true);
            radioSpring.setChecked(false);
            radioSummer.setChecked(false);
            radioAutumn.setChecked(false);
            radioWinter.setChecked(false);
        } else if (((AppManager) getApplication()).isSpringBg()) {
            radioAuto.setChecked(false);
            radioSpring.setChecked(true);
            radioSummer.setChecked(false);
            radioAutumn.setChecked(false);
            radioWinter.setChecked(false);
        } else if (((AppManager) getApplication()).isSummerBg()) {
            radioAuto.setChecked(false);
            radioSpring.setChecked(false);
            radioSummer.setChecked(true);
            radioAutumn.setChecked(false);
            radioWinter.setChecked(false);
        } else if (((AppManager) getApplication()).isAutumnBg()) {
            radioAuto.setChecked(false);
            radioSpring.setChecked(false);
            radioSummer.setChecked(false);
            radioAutumn.setChecked(true);
            radioWinter.setChecked(false);
        } else if (((AppManager) getApplication()).isWinterBg()) {
            radioAuto.setChecked(false);
            radioSpring.setChecked(false);
            radioSummer.setChecked(false);
            radioAutumn.setChecked(false);
            radioWinter.setChecked(true);
        }

        btnApply.setOnClickListener(view -> {
            if (radioAuto.isChecked()) {
                ((AppManager) getApplication()).setAutoBg(true);
                ((AppManager) getApplication()).setSpringBg(false);
                ((AppManager) getApplication()).setSummerBg(false);
                ((AppManager) getApplication()).setAutumnBg(false);
                ((AppManager) getApplication()).setWinterBg(false);
            } else if (radioSpring.isChecked()) {
                ((AppManager) getApplication()).setAutoBg(false);
                ((AppManager) getApplication()).setSpringBg(true);
                ((AppManager) getApplication()).setSummerBg(false);
                ((AppManager) getApplication()).setAutumnBg(false);
                ((AppManager) getApplication()).setWinterBg(false);
            } else if (radioSummer.isChecked()) {
                ((AppManager) getApplication()).setAutoBg(false);
                ((AppManager) getApplication()).setSpringBg(false);
                ((AppManager) getApplication()).setSummerBg(true);
                ((AppManager) getApplication()).setAutumnBg(false);
                ((AppManager) getApplication()).setWinterBg(false);
            } else if (radioAutumn.isChecked()) {
                ((AppManager) getApplication()).setAutoBg(false);
                ((AppManager) getApplication()).setSpringBg(false);
                ((AppManager) getApplication()).setSummerBg(false);
                ((AppManager) getApplication()).setAutumnBg(true);
                ((AppManager) getApplication()).setWinterBg(false);
            } else if (radioWinter.isChecked()) {
                ((AppManager) getApplication()).setAutoBg(false);
                ((AppManager) getApplication()).setSpringBg(false);
                ((AppManager) getApplication()).setSummerBg(false);
                ((AppManager) getApplication()).setAutumnBg(false);
                ((AppManager) getApplication()).setWinterBg(true);
            }
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}