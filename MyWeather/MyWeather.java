package MyWeather;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

public class MyWeather {
    // 초단기예보
    public static JSONArray getMicroForecast(int posX, int posY, LocalDateTime baseDate, String serviceKey) throws IOException, ParseException {
        String apiUrl = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst";    //동네예보조회
        final int NUMBER_MAX_TIME = 6;
        final int NUMBER_PER_DATE = 10;
        return getVillageForecast(posX, posY, baseDate, serviceKey, apiUrl, NUMBER_MAX_TIME * NUMBER_PER_DATE);
    }

    // 단기예보
    public static JSONArray getShortForecast(int posX, int posY, LocalDateTime baseDate, String serviceKey, int numOfTime) throws IOException, ParseException {
        String apiUrl = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";    //동네예보조회
        final int NUMBER_PER_DATE = 12;
        return getVillageForecast(posX, posY, baseDate, serviceKey, apiUrl, numOfTime * NUMBER_PER_DATE);
    }

    // 동네예보 공통 모듈
    public static JSONArray getVillageForecast(int posX, int posY, LocalDateTime baseDate, String serviceKey, String apiUrl, int numOfRows) throws IOException, ParseException {
        String nx = Integer.toString(posX);    //위도
        String ny = Integer.toString(posY);    //경도
        String dataType = "json";    //타입 xml, json
        String numberOfRows = Integer.toString(numOfRows);
        StringBuilder urlBuilder = new StringBuilder(apiUrl);
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + serviceKey);
        urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8")); //경도
        urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8")); //위도
        urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(baseDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")), "UTF-8")); /* 조회하고싶은 날짜*/
        urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(baseDate.format(DateTimeFormatter.ofPattern("HHmm")), "UTF-8")); /* 조회하고싶은 시간 AM 02시부터 3시간 단위 */
        urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode(dataType, "UTF-8"));    /* 타입 */
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode(numberOfRows, "UTF-8"));    /* 한 페이지 결과 수 */

        // GET 방식으로 전송해서 파라미터 받아오기
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        String data = sb.toString();

        // Json parser를 만들어 만들어진 문자열 데이터를 객체화
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(data);
        // response 키를 가지고 데이터를 파싱
        JSONObject parse_response = (JSONObject) obj.get("response");
        // response 로 부터 body 찾기
        JSONObject parse_body = (JSONObject) parse_response.get("body");
        // body 로 부터 items 찾기
        JSONObject parse_items = (JSONObject) parse_body.get("items");
        JSONArray parse_item = (JSONArray) parse_items.get("item");
        //JSONObject item = (JSONObject) parse_item.get("item");

        return parse_item;
    }

    public static ArrayList<Integer> getSimpleTempList(JSONArray data) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            JSONObject dataLine = (JSONObject) data.get(i);
            if (dataLine.get("category").toString().contains("TMP")) {
                result.add(Integer.parseInt(dataLine.get("fcstValue").toString()));
            }
        }
        return result;
    }

    public static ArrayList<Integer> getTomorrowTempList(JSONArray data) {
        ArrayList<Integer> result = new ArrayList<>();
        String tomorrowDate = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        for (int i = 0; i < data.size(); i++) {
            JSONObject dataLine = (JSONObject) data.get(i);
            if (dataLine.get("fcstDate").equals(tomorrowDate) && dataLine.get("category").toString().contains("TMP")) {
                result.add(Integer.parseInt(dataLine.get("fcstValue").toString()));
            }
        }
        return result;
    }

    // 오전, 오후, 최고, 최저 기온 구하기
    public static TempMinMax getTempMinMax(ArrayList<Integer> data) {
        final int HOUR_OF_DAY = 24;
        ArrayList<Integer> tempListAm = new ArrayList<>(), tempListPm = new ArrayList<>();
        int maxAm, minAm, maxPm, minPm;
        // 오전 오후로 쪼개기
        if (data.size() == HOUR_OF_DAY) {
            for (int i = 0; i < (HOUR_OF_DAY / 2); i++) {
                tempListAm.add(data.get(i));
            }
            for (int i = (HOUR_OF_DAY / 2); i < HOUR_OF_DAY; i++) {
                tempListPm.add(data.get(i));
            }
        }
        maxAm = Collections.max(tempListAm);
        minAm = Collections.min(tempListAm);
        maxPm = Collections.max(tempListPm);
        minPm = Collections.min(tempListPm);
        return new TempMinMax(maxAm, minAm, maxPm, minPm);
    }
}
