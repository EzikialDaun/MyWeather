package com.example.myweather;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class LatXLngY {
    public double lat;
    public double lng;

    public int x;
    public int y;
}

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
            return e + "\n 주소 변환 오류";
        }
    }

    public static LatXLngY convertGRID_GPS(double lat_X, double lng_Y) {
        double RE = 6371.00877; // 지구 반경(km)
        double GRID = 5.0; // 격자 간격(km)
        double SLAT1 = 30.0; // 투영 위도1(degree)
        double SLAT2 = 60.0; // 투영 위도2(degree)
        double OLON = 126.0; // 기준점 경도(degree)
        double OLAT = 38.0; // 기준점 위도(degree)
        double XO = 43; // 기준점 X좌표(GRID)
        double YO = 136; // 기1준점 Y좌표(GRID)

        // LCC DFS 좌표변환 ( code : "TO_GRID"(위경도->좌표, lat_X:위도,  lng_Y:경도), "TO_GPS"(좌표->위경도,  lat_X:x, lng_Y:y) )

        final double DEG_RAD = Math.PI / 180.0;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEG_RAD;
        double slat2 = SLAT2 * DEG_RAD;
        double olon = OLON * DEG_RAD;
        double olat = OLAT * DEG_RAD;

        double temp = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / temp;
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = temp;
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);
        LatXLngY rs = new LatXLngY();

        rs.lat = lat_X;
        rs.lng = lng_Y;
        double ra = Math.tan(Math.PI * 0.25 + (lat_X) * DEG_RAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);
        double theta = lng_Y * DEG_RAD - olon;
        if (theta > Math.PI) theta -= 2.0 * Math.PI;
        if (theta < -Math.PI) theta += 2.0 * Math.PI;
        theta *= sn;
        rs.x = (int) Math.floor(ra * Math.sin(theta) + XO + 0.5);
        rs.y = (int) Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);

        return rs;
    }
}
