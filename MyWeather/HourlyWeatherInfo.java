package MyWeather;

import java.time.LocalDateTime;

public class HourlyWeatherInfo {
  // 예보 대상 날짜
  private LocalDateTime forecastDate;
  // 예보 발표 날짜
  private LocalDateTime announceDate;
  // 기온(℃)
  private int temperature;
  // 습도(%)
  private int humidity;
  // 하늘 코드
  private String sky;
  // 강수 코드
  private String precipitation;
  // 풍향(degree)
  private int windDirection;
  // 풍속(m/s)
  private int windSpeed;
  // 강수량
  private String rainScale;

  public LocalDateTime getAnnounceDate() {
    return announceDate;
  }

  public void setAnnounceDate(LocalDateTime announceDate) {
    this.announceDate = announceDate;
  }

  public LocalDateTime getForecastDate() {
    return forecastDate;
  }

  public void setForecastDate(LocalDateTime forecastDate) {
    this.forecastDate = forecastDate;
  }

  public int getTemperature() {
    return temperature;
  }

  public void setTemperature(int temperature) {
    this.temperature = temperature;
  }

  public int getHumidity() {
    return humidity;
  }

  public void setHumidity(int humidity) {
    this.humidity = humidity;
  }

  public String getSky() {
    return sky;
  }

  public void setSky(String sky) {
    this.sky = switch (sky) {
      case "1" -> "맑음";
      case "3" -> "구름많음";
      case "4" -> "흐림";
      default -> "하늘상태코드 에러";
    };
  }

  public String getPrecipitation() {
    return precipitation;
  }

  public void setPrecipitation(String ptyCode) {
    this.precipitation = switch (ptyCode) {
      case "0" -> "강수없음";
      case "1" -> "비";
      case "2" -> "비/눈";
      case "3" -> "눈";
      case "4" -> "소나기";
      case "5" -> "빗방울";
      case "6" -> "빗방울/눈날림";
      case "7" -> "눈날림";
      default -> "강수상태코드 에러";
    };
  }

  public int getWindDirection() {
    return windDirection;
  }

  public void setWindDirection(int windDirection) {
    this.windDirection = windDirection;
  }

  public int getWindSpeed() {
    return windSpeed;
  }

  public void setWindSpeed(int windSpeed) {
    this.windSpeed = windSpeed;
  }

  public String getRainScale() {
    return rainScale;
  }

  public void setRainScale(String rainScale) {
    this.rainScale = rainScale;
  }
}