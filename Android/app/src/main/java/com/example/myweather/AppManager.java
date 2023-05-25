package com.example.myweather;

import android.app.Application;

public class AppManager extends Application {
    private final int HOURS_IN_DAY = 24;
    private final int DAYS_IN_WEEK = 7;
    private final int SHORT_DAY_LIMIT = 2;
    private String address;
    private int UV;
    private HourlyWeatherInfo currentWeatherInfo;
    private HourlyWeatherInfo[] hour24WeatherInfos = new HourlyWeatherInfo[HOURS_IN_DAY];
    private DailyWeatherInfo[] weeklyTempInfo = new DailyWeatherInfo[DAYS_IN_WEEK];

    public int getUV() {
        return UV;
    }

    public void setUV(int UV) {
        this.UV = UV;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public HourlyWeatherInfo getCurrentWeatherInfo() {
        return currentWeatherInfo;
    }

    public void setCurrentWeatherInfo(HourlyWeatherInfo currentWeatherInfo) {
        this.currentWeatherInfo = currentWeatherInfo;
    }

    public HourlyWeatherInfo[] getHour24WeatherInfos() {
        return hour24WeatherInfos;
    }

    public void setHour24WeatherInfos(HourlyWeatherInfo[] hour24WeatherInfos) {
        this.hour24WeatherInfos = hour24WeatherInfos;
    }

    public DailyWeatherInfo getWeeklyTempInfo(int index) {
        return weeklyTempInfo[index];
    }

    public DailyWeatherInfo[] getWeeklyTempInfos() {
        return weeklyTempInfo;
    }

    public void setWeeklyTempInfo(DailyWeatherInfo dayTempInfo, int index) {
        this.weeklyTempInfo[index] = dayTempInfo;
    }

    public int getHoursInDay() {
        return HOURS_IN_DAY;
    }

    public int getDaysInWeek() {
        return DAYS_IN_WEEK;
    }

    public int getShortDayLimit() {
        return SHORT_DAY_LIMIT;
    }
}
