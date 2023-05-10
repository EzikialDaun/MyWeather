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
    final int NUMBER_PER_DATE_ULTRA_SHORT_FORECAST = 10;
    final int DAYS_IN_WEEK = 7;
    final int HOURS_ULTRA_LIMIT = 6;

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

    // 날짜 포맷
    final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";
    final String SIMPLE_TIME_FORMAT = "HHmm";
    final String SIMPLE_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
    final String KOREAN_SIMPLE_TIME_12H_FORMAT = "a hh시";
    final String FORECAST_DATE_FORMAT = "yyyyMMdd";

    // 속성 키
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

    // 어제 발표된 단기예보 결과
    JSONArray yesterdaySrtFcstResult;
    JSONArray recentSrtFcstResult;
    JSONArray lastUltraSrtFcstResult;
    JSONArray recentUltraSrtFcstResult;
    JSONArray recentMidTermTemp;

    // 초단기예보를 통해 얻은 현재의 날씨를 저장한 객체
    HourlyWeatherInfo currentWeather = new HourlyWeatherInfo();
    HourlyWeatherInfo[] weatherWithinSixHours = new HourlyWeatherInfo[HOURS_ULTRA_LIMIT];
    HourlyWeatherInfo[] weatherAfterSixHours = new HourlyWeatherInfo[HOUR_OF_DAY - HOURS_ULTRA_LIMIT];
    // 주간 날씨, 최고/최저 기온을 저장한 객체
    DailyWeatherInfo[] weatherInWeek = new DailyWeatherInfo[DAYS_IN_WEEK];

    // 직전 초단기예보를 통해 현재 날씨 조회(완료)
    try {
      LocalDateTime lastUltraSrtFcstTime = DateManager.getLastUltraSrtFcstTime();
      lastUltraSrtFcstResult = WeatherManager.getUltraSrtFcst(POS_X, POS_Y, lastUltraSrtFcstTime, SERVICE_KEY);
      LocalDateTime currentDate = lastUltraSrtFcstTime.plusHours(1).withMinute(0);
      String currentHour = currentDate.format(DateTimeFormatter.ofPattern(SIMPLE_TIME_FORMAT));
      currentWeather.setAnnounceDate(lastUltraSrtFcstTime);
      currentWeather.setForecastDate(currentDate);
      if (lastUltraSrtFcstResult.size() == NUMBER_PER_DATE_ULTRA_SHORT_FORECAST * HOURS_ULTRA_LIMIT) {
        for (Object o : lastUltraSrtFcstResult) {
          JSONObject dataLine = (JSONObject) o;
          if (dataLine.get(PROPERTY_FORECAST_TIME).equals(currentHour)) {
            if (dataLine.get(PROPERTY_CATEGORY).equals(PROPERTY_TEMPERATURE_ONE_HOUR)) {
              currentWeather.setTemperature(Double.parseDouble(dataLine.get(PROPERTY_FORECAST_VALUE).toString()));
            } else if (dataLine.get(PROPERTY_CATEGORY).equals(PROPERTY_PRECIPITATION)) {
              currentWeather.setPrecipitation(dataLine.get(PROPERTY_FORECAST_VALUE).toString());
            } else if (dataLine.get(PROPERTY_CATEGORY).equals(PROPERTY_SKY)) {
              currentWeather.setSky(dataLine.get(PROPERTY_FORECAST_VALUE).toString());
            } else if (dataLine.get(PROPERTY_CATEGORY).equals(PROPERTY_HUMIDITY)) {
              currentWeather.setHumidity(Integer.parseInt(dataLine.get(PROPERTY_FORECAST_VALUE).toString()));
            } else if (dataLine.get(PROPERTY_CATEGORY).equals(PROPERTY_WIND_DIRECTION)) {
              currentWeather.setWindDirection(Integer.parseInt(dataLine.get(PROPERTY_FORECAST_VALUE).toString()));
            } else if (dataLine.get(PROPERTY_CATEGORY).equals(PROPERTY_WIND_SPEED)) {
              currentWeather.setWindSpeed(Double.parseDouble(dataLine.get(PROPERTY_FORECAST_VALUE).toString()));
            } else if (dataLine.get(PROPERTY_CATEGORY).equals(PROPERTY_RAIN_ONE_HOUR)) {
              currentWeather.setRainScale(dataLine.get(PROPERTY_FORECAST_VALUE).toString());
            }
          }
        }
        currentWeather.printWeather();
        System.out.println();
      } else {
        throw new NullPointerException();
      }
    } catch (NullPointerException e) {
      e.printStackTrace();
      System.out.println("현재 날씨 정보를 불러오지 못했습니다.");
    }
    // 최근 초단기예보를 통해 6시간까지의 날씨 조회(완료)
    try {
      LocalDateTime recentUltraSrtFcstTime = DateManager.getRecentUltraSrtFcstTime();
      recentUltraSrtFcstResult = WeatherManager.getUltraSrtFcst(POS_X, POS_Y, recentUltraSrtFcstTime, SERVICE_KEY);
      LocalDateTime firstHour = recentUltraSrtFcstTime.plusHours(1).withMinute(0);
      if (recentUltraSrtFcstResult.size() == NUMBER_PER_DATE_ULTRA_SHORT_FORECAST * HOURS_ULTRA_LIMIT) {
        for (int i = 0; i < HOURS_ULTRA_LIMIT; i++) {
          String precipitationCode = ((JSONObject) recentUltraSrtFcstResult.get((HOURS_ULTRA_LIMIT + i))).get(PROPERTY_FORECAST_VALUE).toString();
          String rainScale = ((JSONObject) recentUltraSrtFcstResult.get((2 * HOURS_ULTRA_LIMIT + i))).get(PROPERTY_FORECAST_VALUE).toString();
          String skyCode = ((JSONObject) recentUltraSrtFcstResult.get((3 * HOURS_ULTRA_LIMIT + i))).get(PROPERTY_FORECAST_VALUE).toString();
          double temperature = Double.parseDouble(((JSONObject) recentUltraSrtFcstResult.get((4 * HOURS_ULTRA_LIMIT + i))).get(PROPERTY_FORECAST_VALUE).toString());
          int humidity = Integer.parseInt(((JSONObject) recentUltraSrtFcstResult.get((5 * HOURS_ULTRA_LIMIT + i))).get(PROPERTY_FORECAST_VALUE).toString());
          int windDirection = Integer.parseInt(((JSONObject) recentUltraSrtFcstResult.get((8 * HOURS_ULTRA_LIMIT + i))).get(PROPERTY_FORECAST_VALUE).toString());
          int windSpeed = Integer.parseInt(((JSONObject) recentUltraSrtFcstResult.get((9 * HOURS_ULTRA_LIMIT + i))).get(PROPERTY_FORECAST_VALUE).toString());
          weatherWithinSixHours[i] = new HourlyWeatherInfo();
          weatherWithinSixHours[i].setAnnounceDate(recentUltraSrtFcstTime);
          weatherWithinSixHours[i].setForecastDate(firstHour.plusHours(i));
          weatherWithinSixHours[i].setPrecipitation(precipitationCode);
          weatherWithinSixHours[i].setRainScale(rainScale);
          weatherWithinSixHours[i].setSky(skyCode);
          weatherWithinSixHours[i].setTemperature(temperature);
          weatherWithinSixHours[i].setHumidity(humidity);
          weatherWithinSixHours[i].setWindDirection(windDirection);
          weatherWithinSixHours[i].setWindSpeed(windSpeed);
        }
        for (HourlyWeatherInfo weatherInfo : weatherWithinSixHours) {
          System.out.println("예보 대상시각: " + weatherInfo.getForecastDate().format(DateTimeFormatter.ofPattern(KOREAN_SIMPLE_TIME_12H_FORMAT)));
          System.out.println("강수상태: " + weatherInfo.getPrecipitationString());
          System.out.println("기온: " + weatherInfo.getTemperature());
          System.out.println();
        }
      } else {
        throw new NullPointerException();
      }
    } catch (NullPointerException e) {
      e.printStackTrace();
      System.out.println("현재부터 6시간까지의 날씨 정보를 불러오지 못했습니다.");
    }
    // 어제 단기예보를 통해 오늘 최고/최저 기온 조회(완료)
    try {
      final int NUMBER_REQUEST = HOUR_OF_DAY * NUMBER_PER_DATE_SHORT_FORECAST;
      // 어제 마지막 단기예보 발표시각(하루 전 2300) 구하기
      LocalDateTime yesterday = DateManager.getYesterdayShortForecastTime();
      // 단기예보 요청하기
      yesterdaySrtFcstResult = WeatherManager.getVillageFcst(POS_X, POS_Y, yesterday, SERVICE_KEY, NUMBER_REQUEST);
      if (yesterdaySrtFcstResult.size() == NUMBER_REQUEST) {
        ArrayList<Integer> tempList = new ArrayList<>();
        for (int i = 0; i < NUMBER_REQUEST; i++) {
          JSONObject dataLine = (JSONObject) yesterdaySrtFcstResult.get(i);
          if (dataLine.get(PROPERTY_CATEGORY).equals(PROPERTY_TEMPERATURE)) {
            tempList.add(Integer.parseInt(dataLine.get(PROPERTY_FORECAST_VALUE).toString()));
          }
        }
        // 데이터 길이 체크
        if (tempList.size() == HOUR_OF_DAY) {
          weatherInWeek[INDEX_TODAY] = new DailyWeatherInfo(yesterday);
          weatherInWeek[INDEX_TODAY].setTempMax(Collections.max(tempList));
          weatherInWeek[INDEX_TODAY].setTempMin(Collections.min(tempList));
          // 리스트를 반으로 나누어 각각의 최고, 최저 기온 구하기
          final int HALF_DAY = HOUR_OF_DAY / 2;
          ArrayList<Integer> tempListAm = new ArrayList<>(tempList.subList(0, HALF_DAY));
          ArrayList<Integer> tempListPm = new ArrayList<>(tempList.subList(HALF_DAY, HOUR_OF_DAY));
          weatherInWeek[INDEX_TODAY].setTempMaxAm(Collections.max(tempListAm));
          weatherInWeek[INDEX_TODAY].setTempMinAm(Collections.min(tempListAm));
          weatherInWeek[INDEX_TODAY].setTempMaxPm(Collections.max(tempListPm));
          weatherInWeek[INDEX_TODAY].setTempMinPm(Collections.min(tempListPm));
          LocalDateTime todayDate = yesterday.plusDays(1);
          String today = todayDate.format(DateTimeFormatter.ofPattern(SIMPLE_DATE_FORMAT));
          String forecastDate = DateManager.getYesterdayShortForecastTime().format(DateTimeFormatter.ofPattern(SIMPLE_DATE_TIME_FORMAT));
          weatherInWeek[INDEX_TODAY].setForecastDate(todayDate);
          System.out.println(TEST_POSITION + " 위치의 오늘(" + today + ") 오전 최고 온도는 " + weatherInWeek[INDEX_TODAY].getTempMaxAm() + "°C, 최저 온도는 " + weatherInWeek[INDEX_TODAY].getTempMinAm() + "°C 입니다. 발표시각: " + forecastDate);
          System.out.println(TEST_POSITION + " 위치의 오늘(" + today + ") 오후 최고 온도는 " + weatherInWeek[INDEX_TODAY].getTempMaxPm() + "°C, 최저 온도는 " + weatherInWeek[INDEX_TODAY].getTempMinPm() + "°C 입니다. 발표시각: " + forecastDate);
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
    // 최근 단기예보를 통해 3일까지의 날씨 조회
    try {
      final int MAX_DAY_SHORT_FORECAST = 4;
      final int NUMBER_REQUEST = HOUR_OF_DAY * MAX_DAY_SHORT_FORECAST * NUMBER_PER_DATE_SHORT_FORECAST;
      // 최근 단기예보 발표시각 구하기
      LocalDateTime recentForecastTime = DateManager.getRecentShortForecastTime();
      LocalDateTime tomorrowDate = LocalDateTime.now().plusDays(1);
      LocalDateTime dayAfterTomorrowDate = LocalDateTime.now().plusDays(2);
      // 단기예보 받아오기
      recentSrtFcstResult = WeatherManager.getVillageFcst(POS_X, POS_Y, recentForecastTime, SERVICE_KEY, NUMBER_REQUEST);
      ArrayList<Integer> tomorrowTempList = new ArrayList<>();
      ArrayList<Integer> dayAfterTomorrowTempList = new ArrayList<>();
      for (Object o : recentSrtFcstResult) {
        JSONObject dataLine = (JSONObject) o;
        if (dataLine.get(PROPERTY_FORECAST_DATE).equals(tomorrowDate.format(DateTimeFormatter.ofPattern(FORECAST_DATE_FORMAT))) && dataLine.get(PROPERTY_CATEGORY).equals(PROPERTY_TEMPERATURE)) {
          tomorrowTempList.add(Integer.parseInt(dataLine.get(PROPERTY_FORECAST_VALUE).toString()));
        }
        if (dataLine.get(PROPERTY_FORECAST_DATE).equals(dayAfterTomorrowDate.format(DateTimeFormatter.ofPattern(FORECAST_DATE_FORMAT))) && dataLine.get(PROPERTY_CATEGORY).equals(PROPERTY_TEMPERATURE)) {
          dayAfterTomorrowTempList.add(Integer.parseInt(dataLine.get(PROPERTY_FORECAST_VALUE).toString()));
        }
      }
      if (tomorrowTempList.size() == HOUR_OF_DAY) {
        weatherInWeek[INDEX_TOMORROW] = new DailyWeatherInfo(recentForecastTime);
        weatherInWeek[INDEX_TOMORROW].setTempMax(Collections.max(tomorrowTempList));
        weatherInWeek[INDEX_TOMORROW].setTempMin(Collections.min(tomorrowTempList));
        final int HALF_DAY = HOUR_OF_DAY / 2;
        ArrayList<Integer> tempListAm = new ArrayList<>(tomorrowTempList.subList(0, HALF_DAY));
        ArrayList<Integer> tempListPm = new ArrayList<>(tomorrowTempList.subList(HALF_DAY, HOUR_OF_DAY));
        weatherInWeek[INDEX_TOMORROW].setTempMaxAm(Collections.max(tempListAm));
        weatherInWeek[INDEX_TOMORROW].setTempMinAm(Collections.min(tempListAm));
        weatherInWeek[INDEX_TOMORROW].setTempMaxPm(Collections.max(tempListPm));
        weatherInWeek[INDEX_TOMORROW].setTempMinPm(Collections.min(tempListPm));
        String tomorrow = tomorrowDate.format(DateTimeFormatter.ofPattern(SIMPLE_DATE_FORMAT));
        String forecastDate = recentForecastTime.format(DateTimeFormatter.ofPattern(SIMPLE_DATE_TIME_FORMAT));
        weatherInWeek[INDEX_TOMORROW].setForecastDate(tomorrowDate);
        System.out.println(TEST_POSITION + " 위치의 내일(" + tomorrow + ") 오전 최고 온도는 " + weatherInWeek[INDEX_TOMORROW].getTempMaxAm() + SCALE_CELSIUS + ", 최저 온도는 " + weatherInWeek[INDEX_TOMORROW].getTempMinAm() + SCALE_CELSIUS + " 입니다. 발표시각: " + forecastDate);
        System.out.println(TEST_POSITION + " 위치의 내일(" + tomorrow + ") 오후 최고 온도는 " + weatherInWeek[INDEX_TOMORROW].getTempMinPm() + SCALE_CELSIUS + ", 최저 온도는 " + weatherInWeek[INDEX_TOMORROW].getTempMinPm() + SCALE_CELSIUS + " 입니다. 발표시각: " + forecastDate);
        System.out.println();
      } else {
        throw new NullPointerException();
      }
      if (dayAfterTomorrowTempList.size() == HOUR_OF_DAY) {
        weatherInWeek[INDEX_DAY_AFTER_TOMORROW] = new DailyWeatherInfo(recentForecastTime);
        weatherInWeek[INDEX_DAY_AFTER_TOMORROW].setTempMax(Collections.max(dayAfterTomorrowTempList));
        weatherInWeek[INDEX_DAY_AFTER_TOMORROW].setTempMin(Collections.min(dayAfterTomorrowTempList));
        weatherInWeek[INDEX_DAY_AFTER_TOMORROW].setForecastDate(dayAfterTomorrowDate);
      } else {
        throw new NullPointerException();
      }
    } catch (NullPointerException e) {
      e.printStackTrace();
      System.out.println("내일 최고/최저 기온 정보를 불러오지 못했습니다.");
    }
    // 최근 중기예보를 통해 3일부터 7일까지의 날씨 조회
    try {
      LocalDateTime recentMidFcstTime = DateManager.getRecentMidForecastTime();
      recentMidTermTemp = WeatherManager.getMidFcstTemp(REG_ID_INCHEON, recentMidFcstTime, SERVICE_KEY);
      // 중기예보는 JSON 사이즈가 항상 1임
      if (recentMidTermTemp.size() == 1) {
        // 오늘 날짜로부터 3 ~ 6일의 기온 저장
        final int MIN_INDEX_MID_FORECAST = 3;
        final int MAX_INDEX_MID_FORECAST = 6;
        // 사이즈가 1이므로 첫 인덱스인 0 선택
        JSONObject dataLine = (JSONObject) recentMidTermTemp.get(0);
        for (int i = MIN_INDEX_MID_FORECAST; i <= MAX_INDEX_MID_FORECAST; i++) {
          LocalDateTime destDate = LocalDateTime.now().plusDays(i);
          weatherInWeek[i] = new DailyWeatherInfo(recentMidFcstTime);
          weatherInWeek[i].setForecastDate(destDate);
          weatherInWeek[i].setTempMax(Integer.parseInt(dataLine.get(PROPERTY_TEMPERATURE_MAX + (i + 1)).toString()));
          weatherInWeek[i].setTempMin(Integer.parseInt(dataLine.get(PROPERTY_TEMPERATURE_MIN + (i + 1)).toString()));
        }
        // 주간 최고/최저 기온 결과 출력
        for (DailyWeatherInfo d : weatherInWeek) {
          System.out.println("날짜 : " + d.getForecastDate().format(DateTimeFormatter.ofPattern(SIMPLE_DATE_FORMAT)));
          System.out.println("최고 기온 : " + d.getTempMax() + SCALE_CELSIUS);
          System.out.println("최저 기온 : " + d.getTempMin() + SCALE_CELSIUS);
          System.out.println();
        }
      } else {
        throw new NullPointerException();
      }
    } catch (NullPointerException e) {
      e.printStackTrace();
      System.out.println("주간 최고/최저 기온 정보를 불러오지 못했습니다.");
    }

    System.out.println(AddressManager.getAddress(37.95217365428529, 127.31069667727351));
  }
}
