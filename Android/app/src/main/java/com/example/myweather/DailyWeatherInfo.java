package com.example.myweather;

import java.time.LocalDateTime;

public class DailyWeatherInfo {
    // 발표 날짜
    private LocalDateTime baseDate;
    // 대상 날짜
    private LocalDateTime targetDate;
    private int tempMin;
    private int tempMax;

    public DailyWeatherInfo() {

    }

    public int getTempMin() {
        return tempMin;
    }

    public void setTempMin(int tempMin) {
        this.tempMin = tempMin;
    }

    public int getTempMax() {
        return tempMax;
    }

    public void setTempMax(int tempMax) {
        this.tempMax = tempMax;
    }

    public LocalDateTime getBaseDate() {
        return baseDate;
    }

    public void setBaseDate(LocalDateTime baseDate) {
        this.baseDate = baseDate;
    }

    public LocalDateTime getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDateTime targetDate) {
        this.targetDate = targetDate;
    }
}
