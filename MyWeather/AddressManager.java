package MyWeather;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddressManager {
  public static String getAddress(double lat, double lng) {
    try {
      final String GEOCODE_URL = "https://dapi.kakao.com/v2/local/geo/coord2address.json?";
      final String GEOCODE_USER_INFO = "KakaoAK aa138fc9cd6064e76f89db43faf65b7e";

      String x = Double.toString(lng);
      String y = Double.toString(lat);
      String coordinatesystem = "WGS84";

      URL obj = new URL(GEOCODE_URL + "x=" + x + "&y=" + y + "&input_coord=" + coordinatesystem);

      HttpURLConnection con = (HttpURLConnection) obj.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("Authorization", GEOCODE_USER_INFO);
      con.setRequestProperty("content-type", "application/json");
      con.setDoOutput(true);
      con.setUseCaches(false);

      BufferedReader rd;
      if (con.getResponseCode() >= 200 && con.getResponseCode() <= 300) {
        rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
      } else {
        rd = new BufferedReader(new InputStreamReader(con.getErrorStream()));
      }
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = rd.readLine()) != null) {
        sb.append(line);
      }
      rd.close();
      con.disconnect();
      String data = sb.toString();

      JSONParser parser = new JSONParser();
      JSONObject jsonObject = (JSONObject) parser.parse(data);
      JSONArray parse_documents = (JSONArray) jsonObject.get("documents");
      JSONObject parse_document = (JSONObject) parse_documents.get(0);
      JSONObject parse_road = (JSONObject) parse_document.get("address");
      String region1depthName = (String) parse_road.get("region_1depth_name");
      String region2depthName = (String) parse_road.get("region_2depth_name");
      String region3depthName = (String) parse_road.get("region_3depth_name");
      return region1depthName + " " + region2depthName + " " + region3depthName;
    } catch (Exception e) {
      e.printStackTrace();
      return "Error";
    }
  }
}
