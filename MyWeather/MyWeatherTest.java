package MyWeather;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MyWeatherTest {
    public static void main(String[] args) throws IOException, ParseException {
        // 기상청에서 제공받은 서비스 키
        final String SERVICE_KEY = "A6z4dofotOGc96pa7%2FDl3WgE2cW1orNWcou7slmHGBqY9G1Z9LMaVoO1dETagYiNdaPI%2Fe6vGYftFK9e2EjEZA%3D%3D";
        final String KOREAN_DATE_TIME_FORMAT = "yyyy년 MM월 dd일 HH시 mm분";
        final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";
        final String SIMPLE_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
        final int HOUR_OF_DAY = 24;
        JSONArray yesterdayShortWeatherResult;
        JSONArray recentShortWeatherResult;
        // 좌표
        final int POS_X = 55, POS_Y = 127;
        // 좌표 문자열화
        String position = "(" + POS_X + ", " + POS_Y + ")";
        // 직전 초단기예보
        try {
            LocalDateTime recentForecastTime = MyDate.getLastMicroForecastTime();
            JSONArray recentMicroWeatherResult = MyWeather.getMicroForecast(POS_X, POS_Y, recentForecastTime, SERVICE_KEY);
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
                if (dataLine.get("fcstTime").equals(currentTime)) {
                    if (dataLine.get("category").equals("T1H")) {
                        currentTemp = Integer.parseInt(dataLine.get("fcstValue").toString());
                    } else if (dataLine.get("category").equals("PTY") && (dataLine.get("fcstTime").equals(currentTime))) {
                        switch (dataLine.get("fcstValue").toString()) {
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
                    } else if (dataLine.get("category").equals("SKY")) {
                        switch (dataLine.get("fcstValue").toString()) {
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
                    } else if (dataLine.get("category").equals("REH")) {
                        currentHumidity = Integer.parseInt(dataLine.get("fcstValue").toString());
                    } else if (dataLine.get("category").equals("VEC")) {
                        currentWindDegree = Integer.parseInt(dataLine.get("fcstValue").toString());
                    } else if (dataLine.get("category").equals("WSD")) {
                        currentWindSpeed = Integer.parseInt(dataLine.get("fcstValue").toString());
                    } else if (dataLine.get("category").equals("RN1")) {
                        currentRainScale = dataLine.get("fcstValue").toString();
                    }
                }
            }
            System.out.println("현재 시각: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern(KOREAN_DATE_TIME_FORMAT)));
            System.out.println("현재 기온: " + currentTemp + "°C");
            System.out.println("현재 강수상태: " + currentPty);
            System.out.println("현재 강수량: " + currentRainScale);
            System.out.println("현재 하늘상태: " + currentSky);
            System.out.println("현재 습도: " + currentHumidity + "%");
            System.out.println("현재 풍향: " + currentWindDegree + "°");
            System.out.println("현재 풍속: " + currentWindSpeed + "m/s");
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("오늘 최고/최저 기온 정보를 불러오지 못했습니다.");
        }
        // 어제 단기예보
        try {
            // 어제 마지막 단기예보 발표시각(하루 전 2300) 구하기
            LocalDateTime yesterday = MyDate.getYesterdayShortForecastTime();
            // 단기예보 요청하기
            yesterdayShortWeatherResult = MyWeather.getShortForecast(POS_X, POS_Y, yesterday, SERVICE_KEY, HOUR_OF_DAY);
            ArrayList<Integer> todayTempList = MyWeather.getSimpleTempList(yesterdayShortWeatherResult);
            // 오늘 최고/최저 기온 구하기
            TempMinMax todayResult = MyWeather.getTempMinMax(todayTempList);
            // 오늘 날짜 문자열
            String today = yesterday.plusDays(1).format(DateTimeFormatter.ofPattern(SIMPLE_DATE_FORMAT));
            // 발표 날짜 문자열(하루 전 2300)
            String forecastDate = MyDate.getYesterdayShortForecastTime().format(DateTimeFormatter.ofPattern(SIMPLE_DATE_TIME_FORMAT));
            System.out.println(position + " 위치의 오늘(" + today + ") 오전 최고 온도는 " + todayResult.getMaxAm() + "도, 최저 온도는 " + todayResult.getMinAm() + "도 입니다. 발표시각: " + forecastDate);
            System.out.println(position + " 위치의 오늘(" + today + ") 오후 최고 온도는 " + todayResult.getMaxPm() + "도, 최저 온도는 " + todayResult.getMinPm() + "도 입니다. 발표시각: " + forecastDate);
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("오늘 최고/최저 기온 정보를 불러오지 못했습니다.");
        }
        // 최근 단기예보(3일 뒤의 새벽 2시까지의 날씨, ex) 17일에 요청하면 20일 새벽 2시까지의 날씨)
        try {
            // 최근 단기예보 발표시각 구하기
            LocalDateTime recentForecastTime = MyDate.getRecentShortForecastTime();
            // 단기예보 받아오기
            recentShortWeatherResult = MyWeather.getShortForecast(POS_X, POS_Y, recentForecastTime, SERVICE_KEY, HOUR_OF_DAY * 4);
            ArrayList<Integer> tomorrowTempList = MyWeather.getTomorrowTempList(recentShortWeatherResult);
            // 내일 최고/최저 기온 구하기
            TempMinMax tomorrowResult = MyWeather.getTempMinMax(tomorrowTempList);
            // 내일 날짜 문자열
            String tomorrow = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern(SIMPLE_DATE_FORMAT));
            // 발표 날짜 문자열
            String forecastDate = recentForecastTime.format(DateTimeFormatter.ofPattern(SIMPLE_DATE_TIME_FORMAT));
            System.out.println(position + " 위치의 내일(" + tomorrow + ") 오전 최고 온도는 " + tomorrowResult.getMaxAm() + "도, 최저 온도는 " + tomorrowResult.getMinAm() + "도 입니다. 발표시각: " + forecastDate);
            System.out.println(position + " 위치의 내일(" + tomorrow + ") 오후 최고 온도는 " + tomorrowResult.getMaxPm() + "도, 최저 온도는 " + tomorrowResult.getMinPm() + "도 입니다. 발표시각: " + forecastDate);
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("내일 최고/최저 기온 정보를 불러오지 못했습니다.");
        }
    }
}
