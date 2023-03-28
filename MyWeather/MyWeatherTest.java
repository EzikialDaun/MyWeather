package MyWeather;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

public class MyWeatherTest {
  public static void main(String[] args) throws IOException, ParseException {
    final int HOUR_OF_DAY = 24;
    final int NUMBER_PER_DATE_SHORT_FORECAST = 12;
    final int POS_X = 55;
    final int POS_Y = 127;

    // 기상청에서 제공받은 서비스 키
    final String SERVICE_KEY = "A6z4dofotOGc96pa7%2FDl3WgE2cW1orNWcou7slmHGBqY9G1Z9LMaVoO1dETagYiNdaPI%2Fe6vGYftFK9e2EjEZA%3D%3D";
    final String REG_ID_INCHEON = "11B20201";
    final String KOREAN_DATE_TIME_FORMAT = "yyyy년 MM월 dd일 HH시 mm분";
    final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";
    final String FORECAST_DATE_FORMAT = "yyyyMMdd";
    final String SIMPLE_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    final String PROPERTY_FORECAST_TIME = "fcstTime";
    final String PROPERTY_FORECAST_VALUE = "fcstValue";
    final String PROPERTY_FORECAST_DATE = "fcstDate";
    final String PROPERTY_CATEGORY = "category";
    final String TEST_POSITION = "(" + POS_X + ", " + POS_Y + ")";

    JSONArray yesterdayShortWeatherResult;
    JSONArray recentShortWeatherResult;
    JSONArray recentMicroWeatherResult;
    JSONArray recentMidTermTemp;

    // 직전 초단기예보
    try {
      LocalDateTime lastForecastTime = MyDate.getLastMicroForecastTime();
      recentMicroWeatherResult = MyWeather.getMicroForecast(POS_X, POS_Y, lastForecastTime, SERVICE_KEY);
      int currentTemp = -100;
      int currentHumidity = -100;
      int currentWindDegree = -1000;
      int currentWindSpeed = -100;
      String currentRainScale = "";
      String currentSky = "";
      String currentPty = "";
      String currentTime = LocalDateTime.now().withMinute(0).format(DateTimeFormatter.ofPattern("HHmm"));
      for (int i = 0; i < recentMicroWeatherResult.size(); i++) {
        JSONObject dataLine = (JSONObject) recentMicroWeatherResult.get(i);
        if (dataLine.get(PROPERTY_FORECAST_TIME).equals(currentTime)) {
          if (dataLine.get(PROPERTY_CATEGORY).equals("T1H")) {
            currentTemp = Integer.parseInt(dataLine.get(PROPERTY_FORECAST_VALUE).toString());
          } else if (dataLine.get(PROPERTY_CATEGORY).equals("PTY") && (dataLine.get(PROPERTY_FORECAST_TIME).equals(currentTime))) {
            switch (dataLine.get(PROPERTY_FORECAST_VALUE).toString()) {
              case "0":
                currentPty = "강수없음";
                break;
              case "1":
                currentPty = "비";
                break;
              case "2":
                currentPty = "비/눈";
                break;
              case "3":
                currentPty = "눈";
                break;
              case "4":
                currentPty = "소나기";
                break;
              case "5":
                currentPty = "빗방울";
                break;
              case "6":
                currentPty = "빗방울/눈날림";
                break;
              case "7":
                currentPty = "눈날림";
                break;
              default:
                currentPty = "강수상태코드 에러";
                break;
            }
          } else if (dataLine.get(PROPERTY_CATEGORY).equals("SKY")) {
            switch (dataLine.get(PROPERTY_FORECAST_VALUE).toString()) {
              case "1":
                currentSky = "맑음";
                break;
              case "3":
                currentSky = "구름많음";
                break;
              case "4":
                currentSky = "흐림";
                break;
              default:
                currentSky = "하늘상태코드 에러";
                break;
            }
          } else if (dataLine.get(PROPERTY_CATEGORY).equals("REH")) {
            currentHumidity = Integer.parseInt(dataLine.get(PROPERTY_FORECAST_VALUE).toString());
          } else if (dataLine.get(PROPERTY_CATEGORY).equals("VEC")) {
            currentWindDegree = Integer.parseInt(dataLine.get(PROPERTY_FORECAST_VALUE).toString());
          } else if (dataLine.get(PROPERTY_CATEGORY).equals("WSD")) {
            currentWindSpeed = Integer.parseInt(dataLine.get(PROPERTY_FORECAST_VALUE).toString());
          } else if (dataLine.get(PROPERTY_CATEGORY).equals("RN1")) {
            currentRainScale = dataLine.get(PROPERTY_FORECAST_VALUE).toString();
          }
        }
      }
      System.out.println("현재 시각: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern(KOREAN_DATE_TIME_FORMAT)));
      System.out.println("예보 발표시각: " + lastForecastTime.format(DateTimeFormatter.ofPattern(KOREAN_DATE_TIME_FORMAT)));
      System.out.println("기온: " + currentTemp + "°C");
      System.out.println("강수상태: " + currentPty);
      System.out.println("강수량: " + currentRainScale);
      System.out.println("하늘상태: " + currentSky);
      System.out.println("습도: " + currentHumidity + "%");
      System.out.println("풍향: " + currentWindDegree + "°");
      System.out.println("풍속: " + currentWindSpeed + "m/s");
      System.out.println();
    } catch (NullPointerException e) {
      e.printStackTrace();
      System.out.println("오늘 최고/최저 기온 정보를 불러오지 못했습니다.");
    }
    // 어제 단기예보
    try {
      final int NUMBER_REQUEST = HOUR_OF_DAY * NUMBER_PER_DATE_SHORT_FORECAST;
      // 어제 마지막 단기예보 발표시각(하루 전 2300) 구하기
      LocalDateTime yesterday = MyDate.getYesterdayShortForecastTime();
      // 단기예보 요청하기
      yesterdayShortWeatherResult = MyWeather.getShortForecast(POS_X, POS_Y, yesterday, SERVICE_KEY, NUMBER_REQUEST);
      if (yesterdayShortWeatherResult.size() == NUMBER_REQUEST) {
        ArrayList<Integer> tempList = new ArrayList<>();
        for (int i = 0; i < NUMBER_REQUEST; i++) {
          JSONObject dataLine = (JSONObject) yesterdayShortWeatherResult.get(i);
          if (dataLine.get(PROPERTY_CATEGORY).equals("TMP")) {
            tempList.add(Integer.parseInt(dataLine.get(PROPERTY_FORECAST_VALUE).toString()));
          }
        }
        if (tempList.size() == HOUR_OF_DAY) {
          final int HALF_DAY = HOUR_OF_DAY / 2;
          ArrayList<Integer> tempListAm = new ArrayList<>(tempList.subList(0, HALF_DAY));
          ArrayList<Integer> tempListPm = new ArrayList<>(tempList.subList(HALF_DAY, HOUR_OF_DAY));
          int maxAm = Collections.max(tempListAm);
          int minAm = Collections.min(tempListAm);
          int maxPm = Collections.max(tempListPm);
          int minPm = Collections.min(tempListPm);
          String today = yesterday.plusDays(1).format(DateTimeFormatter.ofPattern(SIMPLE_DATE_FORMAT));
          String forecastDate = MyDate.getYesterdayShortForecastTime().format(DateTimeFormatter.ofPattern(SIMPLE_DATE_TIME_FORMAT));
          System.out.println(TEST_POSITION + " 위치의 오늘(" + today + ") 오전 최고 온도는 " + maxAm + "도, 최저 온도는 " + minAm + "도 입니다. 발표시각: " + forecastDate);
          System.out.println(TEST_POSITION + " 위치의 오늘(" + today + ") 오후 최고 온도는 " + maxPm + "도, 최저 온도는 " + minPm + "도 입니다. 발표시각: " + forecastDate);
          System.out.println();
        } else {
          throw new NullPointerException();
        }
      } else {
        throw new NullPointerException();
      }
    } catch (NullPointerException e) {
      e.printStackTrace();
      System.out.println("오늘 최고/최저 기온 정보를 불러오지 못했습니다.");
    }
    // 최근 단기예보(3일 뒤의 새벽 2시까지의 날씨, ex) 17일에 요청하면 20일 새벽 2시까지의 날씨)
    try {
      final int MAX_DAY_SHORT_FORECAST = 4;
      final int NUMBER_REQUEST = HOUR_OF_DAY * MAX_DAY_SHORT_FORECAST * NUMBER_PER_DATE_SHORT_FORECAST;
      // 최근 단기예보 발표시각 구하기
      LocalDateTime recentForecastTime = MyDate.getRecentShortForecastTime();
      LocalDateTime tomorrowDate = LocalDateTime.now().plusDays(1);
      // 단기예보 받아오기
      recentShortWeatherResult = MyWeather.getShortForecast(POS_X, POS_Y, recentForecastTime, SERVICE_KEY, NUMBER_REQUEST);
      ArrayList<Integer> tempList = new ArrayList<>();
      for (int i = 0; i < NUMBER_REQUEST; i++) {
        JSONObject dataLine = (JSONObject) recentShortWeatherResult.get(i);
        if (dataLine.get(PROPERTY_FORECAST_DATE).equals(tomorrowDate.format(DateTimeFormatter.ofPattern(FORECAST_DATE_FORMAT))) && dataLine.get(PROPERTY_CATEGORY).equals("TMP")) {
          tempList.add(Integer.parseInt(dataLine.get(PROPERTY_FORECAST_VALUE).toString()));
        }
        if (tempList.size() == HOUR_OF_DAY) {
          break;
        }
      }
      if (tempList.size() == HOUR_OF_DAY) {
        final int HALF_DAY = HOUR_OF_DAY / 2;
        ArrayList<Integer> tempListAm = new ArrayList<>(tempList.subList(0, HALF_DAY));
        ArrayList<Integer> tempListPm = new ArrayList<>(tempList.subList(HALF_DAY, HOUR_OF_DAY));
        int maxAm = Collections.max(tempListAm);
        int minAm = Collections.min(tempListAm);
        int maxPm = Collections.max(tempListPm);
        int minPm = Collections.min(tempListPm);
        String tomorrow = tomorrowDate.format(DateTimeFormatter.ofPattern(SIMPLE_DATE_FORMAT));
        String forecastDate = recentForecastTime.format(DateTimeFormatter.ofPattern(SIMPLE_DATE_TIME_FORMAT));
        System.out.println(TEST_POSITION + " 위치의 내일(" + tomorrow + ") 오전 최고 온도는 " + maxAm + "도, 최저 온도는 " + minAm + "도 입니다. 발표시각: " + forecastDate);
        System.out.println(TEST_POSITION + " 위치의 내일(" + tomorrow + ") 오후 최고 온도는 " + maxPm + "도, 최저 온도는 " + minPm + "도 입니다. 발표시각: " + forecastDate);
        System.out.println();
      } else {
        throw new NullPointerException();
      }
    } catch (NullPointerException e) {
      e.printStackTrace();
      System.out.println("내일 최고/최저 기온 정보를 불러오지 못했습니다.");
    } catch (IndexOutOfBoundsException e) {
      e.printStackTrace();
      System.out.println("내일 최고/최저 기온 정보를 불러오지 못했습니다.");
    }
    try {
      LocalDateTime recentForecastTime = MyDate.getRecentMidForecastTime();
      recentMidTermTemp = MyWeather.getMidTermTemp(REG_ID_INCHEON, recentForecastTime, SERVICE_KEY);
      if (recentMidTermTemp.size() == 1) {
        final int MIN_INDEX_MID_FORECAST = 4;
        final int MAX_INDEX_MID_FORECAST = 7;
        JSONObject dataLine = (JSONObject) recentMidTermTemp.get(0);
        for (int i = MIN_INDEX_MID_FORECAST; i <= MAX_INDEX_MID_FORECAST; i++) {
          System.out.println("날짜 : " + LocalDateTime.now().plusDays(i - 1).format(DateTimeFormatter.ofPattern(FORECAST_DATE_FORMAT)));
          System.out.println("최고 기온 : " + dataLine.get("taMax" + i));
          System.out.println("최저 기온 : " + dataLine.get("taMin" + i));
          System.out.println();
        }
      }
    } catch (NullPointerException e) {
      e.printStackTrace();
      System.out.println("주간 최고/최저 기온 정보를 불러오지 못했습니다.");
    }
  }
}
