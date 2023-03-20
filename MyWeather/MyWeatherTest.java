package MyWeather;

import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MyWeatherTest {
    public static void main(String[] args) throws IOException, ParseException {
        // 기상청에서 제공받은 서비스 키
        final String SERVICE_KEY = "A6z4dofotOGc96pa7%2FDl3WgE2cW1orNWcou7slmHGBqY9G1Z9LMaVoO1dETagYiNdaPI%2Fe6vGYftFK9e2EjEZA%3D%3D";
        final int HOUR_OF_DAY = 24;
        JSONArray yesterdayShortWeatherResult;
        JSONArray recentShortWeatherResult;
        // 좌표
        final int POS_X = 55, POS_Y = 127;
        // 좌표 문자열화
        String position = "(" + POS_X + ", " + POS_Y + ")";
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
            String today = yesterday.plusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            // 발표 날짜 문자열(하루 전 2300)
            String forecastDate = MyDate.getYesterdayShortForecastTime().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm"));
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
            String tomorrow = recentForecastTime.plusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            // 발표 날짜 문자열
            String forecastDate = recentForecastTime.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm"));
            System.out.println(position + " 위치의 내일(" + tomorrow + ") 오전 최고 온도는 " + tomorrowResult.getMaxAm() + "도, 최저 온도는 " + tomorrowResult.getMinAm() + "도 입니다. 발표시각: " + forecastDate);
            System.out.println(position + " 위치의 내일(" + tomorrow + ") 오후 최고 온도는 " + tomorrowResult.getMaxPm() + "도, 최저 온도는 " + tomorrowResult.getMinPm() + "도 입니다. 발표시각: " + forecastDate);
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("내일 최고/최저 기온 정보를 불러오지 못했습니다.");
        }
    }
}
