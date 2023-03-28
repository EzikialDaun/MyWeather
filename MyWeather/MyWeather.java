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

public class MyWeather {
  // 초단기예보
  public static JSONArray getMicroForecast(int posX, int posY, LocalDateTime baseDate, String serviceKey) throws IOException, ParseException {
    String apiUrl = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst";
    final int NUMBER_MAX_TIME = 6;
    final int NUMBER_PER_DATE = 10;
    return getVillageForecast(posX, posY, baseDate, serviceKey, apiUrl, NUMBER_MAX_TIME * NUMBER_PER_DATE);
  }

  // 단기예보
  public static JSONArray getShortForecast(int posX, int posY, LocalDateTime baseDate, String serviceKey, int numOfTime) throws IOException, ParseException {
    String apiUrl = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
    return getVillageForecast(posX, posY, baseDate, serviceKey, apiUrl, numOfTime);
  }

  // 중기예보 :: 육상기온
  public static JSONArray getMidTermTemp(String regId, LocalDateTime baseDate, String serviceKey) throws IOException, ParseException {
    String apiUrl = "https://apis.data.go.kr/1360000/MidFcstInfoService/getMidTa";
    return getMidTermForecast(regId, baseDate, serviceKey, apiUrl);
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
    JSONArray result = (JSONArray) parse_items.get("item");

    return result;
  }

  public static JSONArray getMidTermForecast(String regId, LocalDateTime baseDate, String serviceKey, String apiUrl) throws IOException, ParseException {
    StringBuilder urlBuilder = new StringBuilder(apiUrl);
    urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + serviceKey);
    // 지역 ID
    urlBuilder.append("&" + URLEncoder.encode("regId", "UTF-8") + "=" + URLEncoder.encode(regId, "UTF-8"));
    urlBuilder.append("&" + URLEncoder.encode("tmFc", "UTF-8") + "=" + URLEncoder.encode(baseDate.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")), "UTF-8")); /* 조회하고싶은 날짜*/
    urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"));    /* 타입 */
    urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));    /* 한 페이지 결과 수 */

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
    JSONArray result = (JSONArray) parse_items.get("item");

    return result;
  }
}
