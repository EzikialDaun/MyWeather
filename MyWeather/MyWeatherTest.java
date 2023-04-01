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
    final int DAYS_IN_WEEK = 7;

    // 주안동 기상청 격자 좌표
    final int POS_X = 55;
    final int POS_Y = 127;

    final int INDEX_TODAY = 0;
    final int INDEX_TOMORROW = 1;
    final int INDEX_DAY_AFTER_TOMORROW = 2;

    // 기상청에서 제공받은 서비스 키
    final String SERVICE_KEY = "A6z4dofotOGc96pa7%2FDl3WgE2cW1orNWcou7slmHGBqY9G1Z9LMaVoO1dETagYiNdaPI%2Fe6vGYftFK9e2EjEZA%3D%3D";
    // 인천 지역 코드
    final String REG_ID_INCHEON = "11B20201";

    final String SCALE_CELSIUS = "°C";

    final String KOREAN_DATE_TIME_FORMAT = "yyyy년 MM월 dd일 HH시 mm분";
    final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";
    final String SIMPLE_TIME_FORMAT = "HHmm";
    final String SIMPLE_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
    final String FORECAST_DATE_FORMAT = "yyyyMMdd";

    final String PROPERTY_FORECAST_TIME = "fcstTime";
    final String PROPERTY_FORECAST_VALUE = "fcstValue";
    final String PROPERTY_FORECAST_DATE = "fcstDate";
    final String PROPERTY_CATEGORY = "category";
    final String PROPERTY_TEMPERATURE = "TMP";
    final String PROPERTY_TEMPERATURE_ONE_HOUR = "T1H";
    final String PROPERTY_PRECIPITATION = "PTY";
    final String PROPERTY_SKY = "SKY";
    final String PROPERTY_HUMIDITY = "REH";
    final String PROPERTY_WIND_DIRECTION = "VEC";
    final String PROPERTY_WIND_SPEED = "WSD";
    final String PROPERTY_RAIN_ONE_HOUR = "RN1";
    final String PROPERTY_TEMPERATURE_MAX = "taMax";
    final String PROPERTY_TEMPERATURE_MIN = "taMin";

    final String TEST_POSITION = "(" + POS_X + ", " + POS_Y + ")";

    JSONArray yesterdayShortWeatherResult;
    JSONArray recentShortWeatherResult;
    JSONArray recentMicroWeatherResult;
    JSONArray recentMidTermTemp;

    // 현재 날씨, 기온을 저장한 객체
    HourlyWeatherInfo currentForecast = new HourlyWeatherInfo();
    // 주간 날씨, 최고/최저 기온을 저장한 객체
    DailyWeatherInfo[] weekForecast = new DailyWeatherInfo[DAYS_IN_WEEK];

    // 직전 초단기예보
    try {
      LocalDateTime lastForecastTime = DateManager.getLastMicroForecastTime();
      recentMicroWeatherResult = WeatherManager.getMicroForecast(POS_X, POS_Y, lastForecastTime, SERVICE_KEY);
      LocalDateTime currentHour = LocalDateTime.now().withMinute(0);
      currentForecast.setForecastDate(currentHour);
      currentForecast.setAnnounceDate(lastForecastTime);
      for (Object o : recentMicroWeatherResult) {
        JSONObject dataLine = (JSONObject) o;
        if (dataLine.get(PROPERTY_FORECAST_TIME).equals(currentForecast.getForecastDate().format(DateTimeFormatter.ofPattern(SIMPLE_TIME_FORMAT)))) {
          if (dataLine.get(PROPERTY_CATEGORY).equals(PROPERTY_TEMPERATURE_ONE_HOUR)) {
            currentForecast.setTemperature(Integer.parseInt(dataLine.get(PROPERTY_FORECAST_VALUE).toString()));
          } else if (dataLine.get(PROPERTY_CATEGORY).equals(PROPERTY_PRECIPITATION)) {
            currentForecast.setPrecipitation(dataLine.get(PROPERTY_FORECAST_VALUE).toString());
          } else if (dataLine.get(PROPERTY_CATEGORY).equals(PROPERTY_SKY)) {
            currentForecast.setSky(dataLine.get(PROPERTY_FORECAST_VALUE).toString());
          } else if (dataLine.get(PROPERTY_CATEGORY).equals(PROPERTY_HUMIDITY)) {
            currentForecast.setHumidity(Integer.parseInt(dataLine.get(PROPERTY_FORECAST_VALUE).toString()));
          } else if (dataLine.get(PROPERTY_CATEGORY).equals(PROPERTY_WIND_DIRECTION)) {
            currentForecast.setWindDirection(Integer.parseInt(dataLine.get(PROPERTY_FORECAST_VALUE).toString()));
          } else if (dataLine.get(PROPERTY_CATEGORY).equals(PROPERTY_WIND_SPEED)) {
            currentForecast.setWindSpeed(Integer.parseInt(dataLine.get(PROPERTY_FORECAST_VALUE).toString()));
          } else if (dataLine.get(PROPERTY_CATEGORY).equals(PROPERTY_RAIN_ONE_HOUR)) {
            currentForecast.setRainScale(dataLine.get(PROPERTY_FORECAST_VALUE).toString());
          }
        }
      }
      System.out.println("현재 시각: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern(KOREAN_DATE_TIME_FORMAT)));
      System.out.println("예보 발표시각: " + currentForecast.getAnnounceDate().format(DateTimeFormatter.ofPattern(KOREAN_DATE_TIME_FORMAT)));
      System.out.println("기온: " + currentForecast.getTemperature() + SCALE_CELSIUS);
      System.out.println("강수상태: " + currentForecast.getPrecipitation());
      System.out.println("강수량: " + currentForecast.getRainScale());
      System.out.println("하늘상태: " + currentForecast.getSky());
      System.out.println("습도: " + currentForecast.getHumidity() + "%");
      System.out.println("풍향: " + currentForecast.getWindDirection() + "°");
      System.out.println("풍속: " + currentForecast.getWindSpeed() + "m/s");
      System.out.println();
    } catch (NullPointerException e) {
      e.printStackTrace();
      System.out.println("현재 날씨 정보를 불러오지 못했습니다.");
    }
    // 어제 단기예보
    try {
      final int NUMBER_REQUEST = HOUR_OF_DAY * NUMBER_PER_DATE_SHORT_FORECAST;
      // 어제 마지막 단기예보 발표시각(하루 전 2300) 구하기
      LocalDateTime yesterday = DateManager.getYesterdayShortForecastTime();
      // 단기예보 요청하기
      yesterdayShortWeatherResult = WeatherManager.getShortForecast(POS_X, POS_Y, yesterday, SERVICE_KEY, NUMBER_REQUEST);
      if (yesterdayShortWeatherResult.size() == NUMBER_REQUEST) {
        ArrayList<Integer> tempList = new ArrayList<>();
        for (int i = 0; i < NUMBER_REQUEST; i++) {
          JSONObject dataLine = (JSONObject) yesterdayShortWeatherResult.get(i);
          if (dataLine.get(PROPERTY_CATEGORY).equals(PROPERTY_TEMPERATURE)) {
            tempList.add(Integer.parseInt(dataLine.get(PROPERTY_FORECAST_VALUE).toString()));
          }
        }
        if (tempList.size() == HOUR_OF_DAY) {
          weekForecast[INDEX_TODAY] = new DailyWeatherInfo(yesterday);
          weekForecast[INDEX_TODAY].setTempMax(Collections.max(tempList));
          weekForecast[INDEX_TODAY].setTempMin(Collections.min(tempList));
          // 리스트를 반으로 나누어 각각의 최고, 최저 기온 구하기
          final int HALF_DAY = HOUR_OF_DAY / 2;
          ArrayList<Integer> tempListAm = new ArrayList<>(tempList.subList(0, HALF_DAY));
          ArrayList<Integer> tempListPm = new ArrayList<>(tempList.subList(HALF_DAY, HOUR_OF_DAY));
          weekForecast[INDEX_TODAY].setTempMaxAm(Collections.max(tempListAm));
          weekForecast[INDEX_TODAY].setTempMinAm(Collections.min(tempListAm));
          weekForecast[INDEX_TODAY].setTempMaxPm(Collections.max(tempListPm));
          weekForecast[INDEX_TODAY].setTempMinPm(Collections.min(tempListPm));
          LocalDateTime todayDate = yesterday.plusDays(1);
          String today = todayDate.format(DateTimeFormatter.ofPattern(SIMPLE_DATE_FORMAT));
          String forecastDate = DateManager.getYesterdayShortForecastTime().format(DateTimeFormatter.ofPattern(SIMPLE_DATE_TIME_FORMAT));
          weekForecast[INDEX_TODAY].setForecastDate(todayDate);
          System.out.println(TEST_POSITION + " 위치의 오늘(" + today + ") 오전 최고 온도는 " + weekForecast[INDEX_TODAY].getTempMaxAm() + "°C, 최저 온도는 " + weekForecast[INDEX_TODAY].getTempMinAm() + "°C 입니다. 발표시각: " + forecastDate);
          System.out.println(TEST_POSITION + " 위치의 오늘(" + today + ") 오후 최고 온도는 " + weekForecast[INDEX_TODAY].getTempMaxPm() + "°C, 최저 온도는 " + weekForecast[INDEX_TODAY].getTempMinPm() + "°C 입니다. 발표시각: " + forecastDate);
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
      LocalDateTime recentForecastTime = DateManager.getRecentShortForecastTime();
      LocalDateTime tomorrowDate = LocalDateTime.now().plusDays(1);
      LocalDateTime dayAfterTomorrowDate = LocalDateTime.now().plusDays(2);
      // 단기예보 받아오기
      recentShortWeatherResult = WeatherManager.getShortForecast(POS_X, POS_Y, recentForecastTime, SERVICE_KEY, NUMBER_REQUEST);
      ArrayList<Integer> tomorrowTempList = new ArrayList<>();
      ArrayList<Integer> dayAfterTomorrowTempList = new ArrayList<>();
      for (Object o : recentShortWeatherResult) {
        JSONObject dataLine = (JSONObject) o;
        if (dataLine.get(PROPERTY_FORECAST_DATE).equals(tomorrowDate.format(DateTimeFormatter.ofPattern(FORECAST_DATE_FORMAT))) && dataLine.get(PROPERTY_CATEGORY).equals(PROPERTY_TEMPERATURE)) {
          tomorrowTempList.add(Integer.parseInt(dataLine.get(PROPERTY_FORECAST_VALUE).toString()));
        }
        if (dataLine.get(PROPERTY_FORECAST_DATE).equals(dayAfterTomorrowDate.format(DateTimeFormatter.ofPattern(FORECAST_DATE_FORMAT))) && dataLine.get(PROPERTY_CATEGORY).equals(PROPERTY_TEMPERATURE)) {
          dayAfterTomorrowTempList.add(Integer.parseInt(dataLine.get(PROPERTY_FORECAST_VALUE).toString()));
        }
      }
      if (tomorrowTempList.size() == HOUR_OF_DAY) {
        weekForecast[INDEX_TOMORROW] = new DailyWeatherInfo(recentForecastTime);
        weekForecast[INDEX_TOMORROW].setTempMax(Collections.max(tomorrowTempList));
        weekForecast[INDEX_TOMORROW].setTempMin(Collections.min(tomorrowTempList));
        final int HALF_DAY = HOUR_OF_DAY / 2;
        ArrayList<Integer> tempListAm = new ArrayList<>(tomorrowTempList.subList(0, HALF_DAY));
        ArrayList<Integer> tempListPm = new ArrayList<>(tomorrowTempList.subList(HALF_DAY, HOUR_OF_DAY));
        weekForecast[INDEX_TOMORROW].setTempMaxAm(Collections.max(tempListAm));
        weekForecast[INDEX_TOMORROW].setTempMinAm(Collections.min(tempListAm));
        weekForecast[INDEX_TOMORROW].setTempMaxPm(Collections.max(tempListPm));
        weekForecast[INDEX_TOMORROW].setTempMinPm(Collections.min(tempListPm));
        String tomorrow = tomorrowDate.format(DateTimeFormatter.ofPattern(SIMPLE_DATE_FORMAT));
        String forecastDate = recentForecastTime.format(DateTimeFormatter.ofPattern(SIMPLE_DATE_TIME_FORMAT));
        weekForecast[INDEX_TOMORROW].setForecastDate(tomorrowDate);
        System.out.println(TEST_POSITION + " 위치의 내일(" + tomorrow + ") 오전 최고 온도는 " + weekForecast[INDEX_TOMORROW].getTempMaxAm() + SCALE_CELSIUS + ", 최저 온도는 " + weekForecast[INDEX_TOMORROW].getTempMinAm() + SCALE_CELSIUS + " 입니다. 발표시각: " + forecastDate);
        System.out.println(TEST_POSITION + " 위치의 내일(" + tomorrow + ") 오후 최고 온도는 " + weekForecast[INDEX_TOMORROW].getTempMinPm() + SCALE_CELSIUS + ", 최저 온도는 " + weekForecast[INDEX_TOMORROW].getTempMinPm() + SCALE_CELSIUS + " 입니다. 발표시각: " + forecastDate);
        System.out.println();
      } else {
        throw new NullPointerException();
      }
      if (dayAfterTomorrowTempList.size() == HOUR_OF_DAY) {
        weekForecast[INDEX_DAY_AFTER_TOMORROW] = new DailyWeatherInfo(recentForecastTime);
        weekForecast[INDEX_DAY_AFTER_TOMORROW].setTempMax(Collections.max(dayAfterTomorrowTempList));
        weekForecast[INDEX_DAY_AFTER_TOMORROW].setTempMin(Collections.min(dayAfterTomorrowTempList));
        weekForecast[INDEX_DAY_AFTER_TOMORROW].setForecastDate(dayAfterTomorrowDate);
      } else {
        throw new NullPointerException();
      }
    } catch (NullPointerException e) {
      e.printStackTrace();
      System.out.println("내일 최고/최저 기온 정보를 불러오지 못했습니다.");
    }
    try {
      LocalDateTime recentForecastTime = DateManager.getRecentMidForecastTime();
      recentMidTermTemp = WeatherManager.getMidTermTemp(REG_ID_INCHEON, recentForecastTime, SERVICE_KEY);
      // 중기예보는 JSON 사이즈가 1임
      if (recentMidTermTemp.size() == 1) {
        // 오늘 날짜로부터 3 ~ 6일의 기온 저장
        final int MIN_INDEX_MID_FORECAST = 3;
        final int MAX_INDEX_MID_FORECAST = 6;
        // 사이즈가 1이므로 첫 인덱스인 0 선택
        JSONObject dataLine = (JSONObject) recentMidTermTemp.get(0);
        for (int i = MIN_INDEX_MID_FORECAST; i <= MAX_INDEX_MID_FORECAST; i++) {
          LocalDateTime destDate = LocalDateTime.now().plusDays(i);
          weekForecast[i] = new DailyWeatherInfo(recentForecastTime);
          weekForecast[i].setForecastDate(destDate);
          weekForecast[i].setTempMax(Integer.parseInt(dataLine.get(PROPERTY_TEMPERATURE_MAX + (i + 1)).toString()));
          weekForecast[i].setTempMin(Integer.parseInt(dataLine.get(PROPERTY_TEMPERATURE_MIN + (i + 1)).toString()));
        }
      }
      // 주간 최고/최저 기온 결과 출력
      for (DailyWeatherInfo d : weekForecast) {
        System.out.println("날짜 : " + d.getForecastDate().format(DateTimeFormatter.ofPattern(SIMPLE_DATE_FORMAT)));
        System.out.println("최고 기온 : " + d.getTempMax() + SCALE_CELSIUS);
        System.out.println("최저 기온 : " + d.getTempMin() + SCALE_CELSIUS);
        System.out.println();
      }
    } catch (NullPointerException e) {
      e.printStackTrace();
      System.out.println("주간 최고/최저 기온 정보를 불러오지 못했습니다.");
    }
  }
}
