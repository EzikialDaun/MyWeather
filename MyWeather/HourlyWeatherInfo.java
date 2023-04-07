package MyWeather;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

enum SkyCode {
  SUNNY, CLOUDY, OVERCAST, ERROR
}

enum PrecipitationCode {
  NO_PRECIPITATION, RAIN, RAIN_SNOW, SNOW, SHOWER, RAINDROP, RAINDROP_SNOW_DRIFT, SNOW_DRIFT, ERROR
}

public class HourlyWeatherInfo {
  private final static String KOREAN_DATE_TIME_FORMAT = "yyyy년 MM월 dd일 HH시 mm분";
  // 예보 대상 날짜
  private LocalDateTime forecastDate;
  // 예보 발표 날짜
  private LocalDateTime announceDate;
  // 기온(℃)
  private double temperature;
  // 습도(%)
  private int humidity;
  // 하늘 코드
  private SkyCode sky;
  // 강수 코드
  private PrecipitationCode precipitation;
  // 풍향(degree)
  private int windDirection;
  // 풍속(m/s)
  private double windSpeed;
  // 강수량
  private String rainScale;
  // 강수확률 probability of precipitation
  private int pop;

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

  public double getTemperature() {
    return temperature;
  }

  public void setTemperature(double temperature) {
    this.temperature = temperature;
  }

  public int getHumidity() {
    return humidity;
  }

  public void setHumidity(int humidity) {
    this.humidity = humidity;
  }

  public SkyCode getSky() {
    return sky;
  }

  public void setSky(String sky) {
    this.sky = switch (sky) {
      case "1" -> SkyCode.SUNNY;
      case "3" -> SkyCode.CLOUDY;
      case "4" -> SkyCode.OVERCAST;
      default -> SkyCode.ERROR;
    };
  }

  public PrecipitationCode getPrecipitation() {
    return precipitation;
  }

  public void setPrecipitation(String ptyCode) {
    this.precipitation = switch (ptyCode) {
      case "0" -> PrecipitationCode.NO_PRECIPITATION;
      case "1" -> PrecipitationCode.RAIN;
      case "2" -> PrecipitationCode.RAIN_SNOW;
      case "3" -> PrecipitationCode.SNOW;
      case "4" -> PrecipitationCode.SHOWER;
      case "5" -> PrecipitationCode.RAINDROP;
      case "6" -> PrecipitationCode.RAINDROP_SNOW_DRIFT;
      case "7" -> PrecipitationCode.SNOW_DRIFT;
      default -> PrecipitationCode.ERROR;
    };
  }

  public int getWindDirection() {
    return windDirection;
  }

  public void setWindDirection(int windDirection) {
    this.windDirection = windDirection;
  }

  public double getWindSpeed() {
    return windSpeed;
  }

  public void setWindSpeed(double windSpeed) {
    this.windSpeed = windSpeed;
  }

  public String getRainScale() {
    return rainScale;
  }

  public void setRainScale(String rainScale) {
    this.rainScale = rainScale;
  }

  public int getPop() {
    return pop;
  }

  public void setPop(int pop) {
    this.pop = pop;
  }

  public String getPrecipitationString() {
    String result = switch (this.precipitation) {
      case NO_PRECIPITATION -> "강수없음";
      case RAIN -> "비";
      case RAIN_SNOW -> "비/눈";
      case SNOW -> "눈";
      case SHOWER -> "소나기";
      case RAINDROP -> "빗방울";
      case RAINDROP_SNOW_DRIFT -> "빗방울/눈날림";
      case SNOW_DRIFT -> "눈날림";
      case ERROR -> "강수코드 에러";
    };
    return result;
  }

  public String getSkyString() {
    String result = switch (this.sky) {
      case SUNNY -> "맑음";
      case CLOUDY -> "구름많음";
      case OVERCAST -> "흐림";
      case ERROR -> "하늘코드 에러";
    };
    return result;
  }

  public String getForecastDateString() {
    return forecastDate.format(DateTimeFormatter.ofPattern(KOREAN_DATE_TIME_FORMAT));
  }

  public String getAnnounceDateString() {
    return announceDate.format(DateTimeFormatter.ofPattern(KOREAN_DATE_TIME_FORMAT));
  }

  public void printWeather() {
    System.out.println("예보 대상시각: " + getForecastDateString());
    System.out.println("예보 발표시각: " + getAnnounceDateString());
    System.out.println("기온: " + temperature + "°C");
    System.out.println("강수상태: " + getPrecipitationString());
    System.out.println("강수량: " + rainScale);
    System.out.println("하늘상태: " + getSkyString());
    System.out.println("습도: " + humidity + "%");
    System.out.println("풍향: " + windDirection + "°");
    System.out.println("풍속: " + windSpeed + "m/s");
  }
}