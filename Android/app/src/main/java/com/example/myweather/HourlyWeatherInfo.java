package com.example.myweather;

import java.time.LocalDateTime;

enum SkyCode {
    SUNNY, CLOUDY, OVERCAST, ERROR
}

enum PrecipitationCode {
    NO_PRECIPITATION, RAIN, RAIN_SNOW, SNOW, SHOWER, RAINDROP, RAINDROP_SNOW_DRIFT, SNOW_DRIFT, ERROR
}

public class HourlyWeatherInfo {
    // 발표 날짜
    private LocalDateTime baseDate;
    // 대상 날짜
    private LocalDateTime targetDate;
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
        SkyCode result;
        switch (sky) {
            case "1":
                result = SkyCode.SUNNY;
                break;
            case "3":
                result = SkyCode.CLOUDY;
                break;
            case "4":
                result = SkyCode.OVERCAST;
                break;
            default:
                result = SkyCode.ERROR;
                break;
        }
        this.sky = result;
    }

    public PrecipitationCode getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(String ptyCode) {
        PrecipitationCode result;
        switch (ptyCode) {
            case "0":
                result = PrecipitationCode.NO_PRECIPITATION;
                break;
            case "1":
                result = PrecipitationCode.RAIN;
                break;
            case "2":
                result = PrecipitationCode.RAIN_SNOW;
                break;
            case "3":
                result = PrecipitationCode.SNOW;
                break;
            case "4":
                result = PrecipitationCode.SHOWER;
                break;
            case "5":
                result = PrecipitationCode.RAINDROP;
                break;
            case "6":
                result = PrecipitationCode.RAINDROP_SNOW_DRIFT;
                break;
            case "7":
                result = PrecipitationCode.SNOW_DRIFT;
                break;
            default:
                result = PrecipitationCode.ERROR;
                break;
        }
        this.precipitation = result;
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
        String result;
        switch (this.precipitation) {
            case NO_PRECIPITATION:
                result = "강수없음";
                break;
            case RAIN:
                result = "비";
                break;
            case RAIN_SNOW:
                result = "비/눈";
                break;
            case SNOW:
                result = "눈";
                break;
            case SHOWER:
                result = "소나기";
                break;
            case RAINDROP:
                result = "빗방울";
                break;
            case RAINDROP_SNOW_DRIFT:
                result = "빗방울/눈날림";
                break;
            case SNOW_DRIFT:
                result = "눈날림";
                break;
            case ERROR:
            default:
                result = "강수코드 에러";
                break;
        }
        return result;
    }

    public String getSkyString() {
        String result;
        switch (this.sky) {
            case SUNNY:
                result = "맑음";
                break;
            case CLOUDY:
                result = "구름많음";
                break;
            case OVERCAST:
                result = "흐림";
                break;
            case ERROR:
            default:
                result = "하늘코드 에러";
                break;
        }
        return result;
    }

    public LocalDateTime getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDateTime targetDate) {
        this.targetDate = targetDate;
    }

    public LocalDateTime getBaseDate() {
        return baseDate;
    }

    public void setBaseDate(LocalDateTime baseDate) {
        this.baseDate = baseDate;
    }
}