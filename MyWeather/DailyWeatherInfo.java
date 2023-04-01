package MyWeather;

import java.time.LocalDateTime;

public class DailyWeatherInfo {
  // 예보 대상 날짜
  private LocalDateTime forecastDate;
  // 예보 발표 날짜
  private LocalDateTime announceDate;
  private int tempMinAm;
  private int tempMaxAm;
  private int tempMinPm;
  private int tempMaxPm;
  private int tempMin;
  private int tempMax;

  public DailyWeatherInfo(LocalDateTime announceDate) {
    this.announceDate = announceDate;
  }

  public LocalDateTime getForecastDate() {
    return forecastDate;
  }

  public void setForecastDate(LocalDateTime forecastDate) {
    this.forecastDate = forecastDate;
  }

  public LocalDateTime getAnnounceDate() {
    return announceDate;
  }

  public void setAnnounceDate(LocalDateTime announceDate) {
    this.announceDate = announceDate;
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
}
