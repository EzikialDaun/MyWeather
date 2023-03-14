package MyWeather;

import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MyWeatherTest {
    public static void main(String[] args) throws IOException, ParseException {
        String serviceKey = "A6z4dofotOGc96pa7%2FDl3WgE2cW1orNWcou7slmHGBqY9G1Z9LMaVoO1dETagYiNdaPI%2Fe6vGYftFK9e2EjEZA%3D%3D";
        final int HOUR_OF_DAY = 24;
        JSONArray weatherResult;
        try {
            weatherResult = MyWeather.getShortForecast(55, 127, MyDate.getSimpleDate(-1), "2359", serviceKey, HOUR_OF_DAY);
            ArrayList<Integer> tempList = MyWeather.getTempList(weatherResult);
            ArrayList<Integer> tempListAm = new ArrayList<>(), tempListPm = new ArrayList<>();
            int maxAm, minAm, maxPm, minPm;
            if (tempList.size() == HOUR_OF_DAY) {
                for (int i = 0; i < (HOUR_OF_DAY / 2); i++) {
                    tempListAm.add(tempList.get(i));
                }
                for (int i = (HOUR_OF_DAY / 2); i < HOUR_OF_DAY; i++) {
                    tempListPm.add(tempList.get(i));
                }
            }
            maxAm = Collections.max(tempListAm);
            minAm = Collections.min(tempListAm);
            maxPm = Collections.max(tempListPm);
            minPm = Collections.min(tempListPm);
            System.out.println(MyDate.getSimpleDate() + "의 오전 최고 온도는 " + maxAm + "도, 최저 온도는 " + minAm + "도 입니다.");
            System.out.println(MyDate.getSimpleDate() + "의 오후 최고 온도는 " + maxPm + "도, 최저 온도는 " + minPm + "도 입니다.");
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("날씨 정보를 불러오지 못했습니다.");
        }
    }
}
