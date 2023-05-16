package com.example.myweather;

public class DailyWeatherInfo {
    private int tempMinAm;
    private int tempMaxAm;
    private int tempMinPm;
    private int tempMaxPm;
    private int tempMin;
    private int tempMax;
    // 평균 강수확률 probability of precipitation
    private int pop;

    public DailyWeatherInfo() {

    }

    public int getTempMinAm() {
        return tempMinAm;
    }

    public void setTempMinAm(int tempMinAm) {
        this.tempMinAm = tempMinAm;
    }

    public int getTempMaxAm() {
        return tempMaxAm;
    }

    public void setTempMaxAm(int tempMaxAm) {
        this.tempMaxAm = tempMaxAm;
    }

    public int getTempMinPm() {
        return tempMinPm;
    }

    public void setTempMinPm(int tempMinPm) {
        this.tempMinPm = tempMinPm;
    }

    public int getTempMaxPm() {
        return tempMaxPm;
    }

    public void setTempMaxPm(int tempMaxPm) {
        this.tempMaxPm = tempMaxPm;
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

    public int getPop() {
        return pop;
    }

    public void setPop(int pop) {
        this.pop = pop;
    }
}
