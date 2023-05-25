package com.example.myweather;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableRow;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class InfoActivity extends AppCompatActivity {
    private TextView txtInfoAddress;
    private TextView txtDatetime;
    private TableRow rowTime;
    private TableRow rowStatus;
    private TableRow rowTemp;
    private TableRow rowDate;
    private TableRow rowTempRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        txtInfoAddress = findViewById(R.id.textview_info_address);
        txtDatetime = findViewById(R.id.textview_datetime);
        rowTime = findViewById(R.id.tablerow_time);
        rowStatus = findViewById(R.id.tablerow_status);
        rowTemp = findViewById(R.id.tablerow_temp);
        rowDate = findViewById(R.id.tablerow_date);
        rowTempRange = findViewById(R.id.tablerow_temp_range);

        // 타이틀 바 제거
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        HourlyWeatherInfo[] hourlyWeatherInfos = ((AppManager) getApplication()).getHour24WeatherInfos();
        DailyWeatherInfo[] weeklyTempInfos = ((AppManager) getApplication()).getWeeklyTempInfos();
        String pattern = getResources().getString(R.string.format_korean_12h);
        String am = getResources().getString(R.string.am);
        String amKor = getResources().getString(R.string.am_kor);
        String pm = getResources().getString(R.string.pm);
        String pmKor = getResources().getString(R.string.pm_kor);

        txtInfoAddress.setText(((AppManager) getApplication()).getAddress());
        txtDatetime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern)).replace(am, amKor).replace(pm, pmKor));

        for (int i = 0; i < ((AppManager) getApplication()).getHoursInDay(); i++) {
            Typeface typeface = getResources().getFont(R.font.dovemayo_gothic);

            TextView viewTime = new TextView(this);
            viewTime.setText(hourlyWeatherInfos[i].getTargetDate().format(DateTimeFormatter.ofPattern(pattern)).replace(am, amKor).replace(pm, pmKor));
            viewTime.setGravity(Gravity.CENTER);
            viewTime.setPadding(20, 0, 20, 0);
            viewTime.setTypeface(typeface);
            rowTime.addView(viewTime);

            TextView viewStatus = new TextView(this);
            viewStatus.setText(hourlyWeatherInfos[i].getSkyString());
            viewStatus.setGravity(Gravity.CENTER);
            viewStatus.setTypeface(typeface);
            rowStatus.addView(viewStatus);

            TextView viewTemp = new TextView(this);
            viewTemp.setText(String.valueOf((int) (hourlyWeatherInfos[i].getTemperature())));
            viewTemp.setGravity(Gravity.CENTER);
            viewTemp.setTypeface(typeface);
            rowTemp.addView(viewTemp);
        }

        for (int i = 0; i < ((AppManager) getApplication()).getDaysInWeek(); i++) {
            Typeface typeface = getResources().getFont(R.font.dovemayo_gothic);

            TextView viewDate = new TextView(this);
            if (i == 0) {
                viewDate.setText("오늘");
            } else {
                viewDate.setText(weeklyTempInfos[i].getTargetDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREA));
            }

            viewDate.setGravity(Gravity.CENTER);
            viewDate.setPadding(48, 0, 48, 0);
            viewDate.setTypeface(typeface);
            rowDate.addView(viewDate);

            TextView viewTempRange = new TextView(this);
            viewTempRange.setText(weeklyTempInfos[i].getTempMax() + " | " + weeklyTempInfos[i].getTempMin());
            viewTempRange.setGravity(Gravity.CENTER);
            viewTempRange.setTypeface(typeface);
            rowTempRange.addView(viewTempRange);
        }
    }
}