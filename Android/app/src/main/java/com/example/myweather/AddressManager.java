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

class TMCoord {
    public double tmX;
    public double tmY;
}

public class AddressManager {
    private static final String SERVICE_KEY = "KakaoAK aa138fc9cd6064e76f89db43faf65b7e";

    public static TMCoord transCoord(double latitude, double longitude) {
        TMCoord result = new TMCoord();
        result.tmX = 0.0;
        result.tmY = 0.0;

        try {
            final String REQUEST_URL = "https://dapi.kakao.com/v2/local/geo/transcoord.json?";

            String x = Double.toString(longitude);
            String y = Double.toString(latitude);
            String target = "TM";

            URL obj = new URL(REQUEST_URL + "x=" + x + "&y=" + y + "&output_coord=" + target);

            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", SERVICE_KEY);
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
            JSONObject resultObject = (JSONObject) parse_documents.get(0);

            result.tmX = (double) resultObject.get("x");
            result.tmY = (double) resultObject.get("y");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String getAddress(double lat, double lng) {
        try {
            final String GEOCODE_URL = "https://dapi.kakao.com/v2/local/geo/coord2address.json?";

            String x = Double.toString(lng);
            String y = Double.toString(lat);
            String coordinatesystem = "WGS84";

            URL obj = new URL(GEOCODE_URL + "x=" + x + "&y=" + y + "&input_coord=" + coordinatesystem);

            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", SERVICE_KEY);
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

    // 주소에서 법정동 코드로
    public static String addressToAreaId(String address) {
        if (address.contains("인천")) {
            return "2800000000";
        } else if (address.contains("강원")) {
            return "4200000000";
        } else if (address.contains("경기")) {
            return "4100000000";
        } else if (address.contains("경남")) {
            return "4800000000";
        } else if (address.contains("경북")) {
            return "4700000000";
        } else if (address.contains("광주")) {
            return "2900000000";
        } else if (address.contains("대구")) {
            return "2700000000";
        } else if (address.contains("대전")) {
            return "3000000000";
        } else if (address.contains("부산")) {
            return "2600000000";
        } else if (address.contains("서울")) {
            return "1100000000";
        } else if (address.contains("세종")) {
            return "3611000000";
        } else if (address.contains("울산")) {
            return "3100000000";
        } else if (address.contains("전남")) {
            return "4600000000";
        } else if (address.contains("전북")) {
            return "4500000000";
        } else if (address.contains("제주")) {
            return "5000000000";
        } else if (address.contains("충남")) {
            return "4400000000";
        } else if (address.contains("충북")) {
            return "4300000000";
        }
        return "";
    }

    // 주소에서 중기예보 RegId로 변환
    public static String addressToRegId(String address) {
        if (address.contains("백령면")) {
            return "11A00101";
        } else if (address.contains("서울")) {
            return "11B10101";
        } else if (address.contains("과천")) {
            return "11B10102";
        } else if (address.contains("광명")) {
            return "11B10103";
        } else if (address.contains("강화")) {
            return "11B20101";
        } else if (address.contains("김포")) {
            return "11B20102";
        } else if (address.contains("인천")) {
            return "11B20201";
        } else if (address.contains("안산")) {
            return "11B20203";
        } else if (address.contains("부천")) {
            return "11B20204";
        } else if (address.contains("의정부")) {
            return "11B20301";
        } else if (address.contains("고양")) {
            return "11B20302";
        } else if (address.contains("양주")) {
            return "11B20304";
        } else if (address.contains("파주")) {
            return "11B20305";
        } else if (address.contains("동두천")) {
            return "11B20401";
        } else if (address.contains("연천")) {
            return "11B20402";
        } else if (address.contains("포천")) {
            return "11B20403";
        } else if (address.contains("구리")) {
            return "11B20501";
        } else if (address.contains("남양주")) {
            return "11B20502";
        } else if (address.contains("양평")) {
            return "11B20503";
        } else if (address.contains("하남")) {
            return "11B20504";
        } else if (address.contains("수원")) {
            return "11B20601";
        } else if (address.contains("안양")) {
            return "11B20602";
        } else if (address.contains("오산")) {
            return "11B20603";
        } else if (address.contains("화성")) {
            return "11B20604";
        } else if (address.contains("성남")) {
            return "11B20605";
        } else if (address.contains("평택")) {
            return "11B20606";
        } else if (address.contains("의왕")) {
            return "11B20609";
        } else if (address.contains("군포")) {
            return "11B20610";
        } else if (address.contains("안성")) {
            return "11B20611";
        } else if (address.contains("용인")) {
            return "11B20612";
        } else if (address.contains("이천")) {
            return "11B20701";
        } else if (address.contains("광주") && address.contains("경기")) {
            return "11B20702";
        } else if (address.contains("여주")) {
            return "11B20703";
        } else if (address.contains("충주")) {
            return "11C10101";
        } else if (address.contains("진천")) {
            return "11C10102";
        } else if (address.contains("음성")) {
            return "11C10103";
        } else if (address.contains("제천")) {
            return "11C10201";
        } else if (address.contains("단양")) {
            return "11C10202";
        } else if (address.contains("청주")) {
            return "11C10301";
        } else if (address.contains("보은")) {
            return "11C10302";
        } else if (address.contains("괴산")) {
            return "11C10303";
        } else if (address.contains("증평")) {
            return "11C10304";
        } else if (address.contains("추풍령")) {
            return "11C10401";
        } else if (address.contains("영동")) {
            return "11C10402";
        } else if (address.contains("옥천")) {
            return "11C10403";
        } else if (address.contains("서산")) {
            return "11C20101";
        } else if (address.contains("태안")) {
            return "11C20102";
        } else if (address.contains("당진")) {
            return "11C20103";
        } else if (address.contains("홍성")) {
            return "11C20104";
        } else if (address.contains("보령")) {
            return "11C20201";
        } else if (address.contains("서천")) {
            return "11C20202";
        } else if (address.contains("천안")) {
            return "11C20301";
        } else if (address.contains("아산")) {
            return "11C20302";
        } else if (address.contains("예산")) {
            return "11C20303";
        } else if (address.contains("대전")) {
            return "11C20401";
        } else if (address.contains("공주")) {
            return "11C20402";
        } else if (address.contains("계룡")) {
            return "11C20403";
        } else if (address.contains("세종")) {
            return "11C20404";
        } else if (address.contains("부여")) {
            return "11C20501";
        } else if (address.contains("청양")) {
            return "11C20502";
        } else if (address.contains("금산")) {
            return "11C20602";
        } else if (address.contains("논산")) {
            return "11C20602";
        } else if (address.contains("철원")) {
            return "11D10101";
        } else if (address.contains("화천")) {
            return "11D10102";
        } else if (address.contains("인제")) {
            return "11D10201";
        } else if (address.contains("양구")) {
            return "11D10202";
        } else if (address.contains("춘천")) {
            return "11D10301";
        } else if (address.contains("홍천")) {
            return "11D10302";
        } else if (address.contains("원주")) {
            return "11D10401";
        } else if (address.contains("횡성")) {
            return "11D10402";
        } else if (address.contains("영월")) {
            return "11D10501";
        } else if (address.contains("정선")) {
            return "11D10502";
        } else if (address.contains("평창")) {
            return "11D10503";
        } else if (address.contains("대관령")) {
            return "11D20201";
        } else if (address.contains("태백")) {
            return "11D20301";
        } else if (address.contains("속초")) {
            return "11D20401";
        } else if (address.contains("고성") && address.contains("강원")) {
            return "11D20402";
        } else if (address.contains("양양")) {
            return "11D20403";
        } else if (address.contains("강릉")) {
            return "11D20501";
        } else if (address.contains("동해")) {
            return "11D20601";
        } else if (address.contains("삼척")) {
            return "11F10201";
        } else if (address.contains("전주")) {
            return "11D20602";
        } else if (address.contains("익산")) {
            return "11F10202";
        } else if (address.contains("정읍")) {
            return "11F10203";
        } else if (address.contains("완주")) {
            return "11F10204";
        } else if (address.contains("장수")) {
            return "11F10301";
        } else if (address.contains("무주")) {
            return "11F10302";
        } else if (address.contains("진안")) {
            return "11F10303";
        } else if (address.contains("남원") && address.contains("전북")) {
            return "11F10401";
        } else if (address.contains("임실")) {
            return "11F10402";
        } else if (address.contains("순창")) {
            return "11F10403";
        } else if (address.contains("군산")) {
            return "21F10501";
        } else if (address.contains("김제")) {
            return "21F10502";
        } else if (address.contains("고창")) {
            return "21F10601";
        } else if (address.contains("부안")) {
            return "21F10602";
        } else if (address.contains("함평")) {
            return "21F20101";
        } else if (address.contains("영광")) {
            return "21F20102";
        } else if (address.contains("진도")) {
            return "21F20201";
        } else if (address.contains("완도")) {
            return "11F20301";
        } else if (address.contains("해남")) {
            return "11F20302";
        } else if (address.contains("강진")) {
            return "11F20303";
        } else if (address.contains("장흥")) {
            return "11F20304";
        } else if (address.contains("여수")) {
            return "11F20401";
        } else if (address.contains("광양")) {
            return "11F20402";
        } else if (address.contains("고흥")) {
            return "11F20402";
        } else if (address.contains("보성")) {
            return "11F20404";
        } else if (address.contains("순천") && address.contains("전남")) {
            return "11F20405";
        } else if (address.contains("광주")) {
            return "11F20501";
        } else if (address.contains("장성")) {
            return "11F20502";
        } else if (address.contains("나주")) {
            return "11F20503";
        } else if (address.contains("담양")) {
            return "11F20504";
        } else if (address.contains("화순")) {
            return "11F20505";
        } else if (address.contains("구례")) {
            return "11F20601";
        } else if (address.contains("공성")) {
            return "11F20602";
        } else if (address.contains("순천")) {
            return "11F20603";
        } else if (address.contains("흑산도")) {
            return "11F20701";
        } else if (address.contains("목포")) {
            return "21F20801";
        } else if (address.contains("영암")) {
            return "21F20802";
        } else if (address.contains("신안")) {
            return "21F20803";
        } else if (address.contains("무안")) {
            return "21F20804";
        } else if (address.contains("성산")) {
            return "11G00101";
        } else if (address.contains("제주")) {
            return "11G00201";
        } else if (address.contains("성판악")) {
            return "11G00302";
        } else if (address.contains("서귀포")) {
            return "11G00401";
        } else if (address.contains("고산")) {
            return "11G00501";
        } else if (address.contains("이어도")) {
            return "11G00601";
        } else if (address.contains("추자도")) {
            return "11G00800";
        } else if (address.contains("울진")) {
            return "11H10101";
        } else if (address.contains("영덕")) {
            return "11H10102";
        } else if (address.contains("포항")) {
            return "11H10201";
        } else if (address.contains("경주")) {
            return "11H10202";
        } else if (address.contains("문경")) {
            return "11H10301";
        } else if (address.contains("상주")) {
            return "11H10302";
        } else if (address.contains("예천")) {
            return "11H10303";
        } else if (address.contains("영주")) {
            return "11H10401";
        } else if (address.contains("봉화")) {
            return "11H10402";
        } else if (address.contains("영양")) {
            return "11H10403";
        } else if (address.contains("안동")) {
            return "11H10501";
        } else if (address.contains("의성")) {
            return "11H10502";
        } else if (address.contains("청송")) {
            return "11H10503";
        } else if (address.contains("김천")) {
            return "11H10601";
        } else if (address.contains("구미")) {
            return "11H10602";
        } else if (address.contains("군위")) {
            return "11H10603";
        } else if (address.contains("고령")) {
            return "11H10604";
        } else if (address.contains("성주")) {
            return "11H10605";
        } else if (address.contains("대구")) {
            return "11H10701";
        } else if (address.contains("영천")) {
            return "11H10702";
        } else if (address.contains("경산")) {
            return "11H10703";
        } else if (address.contains("청도")) {
            return "11H10704";
        } else if (address.contains("칠곡")) {
            return "11H10705";
        } else if (address.contains("울산")) {
            return "11H20101";
        } else if (address.contains("양산")) {
            return "11H20102";
        } else if (address.contains("부산")) {
            return "11H20201";
        } else if (address.contains("창원")) {
            return "11H20301";
        } else if (address.contains("김해")) {
            return "11H20304";
        } else if (address.contains("통영")) {
            return "11H20401";
        } else if (address.contains("사천")) {
            return "11H20402";
        } else if (address.contains("거제")) {
            return "11H20403";
        } else if (address.contains("고성")) {
            return "11H20404";
        } else if (address.contains("남해")) {
            return "11H20405";
        } else if (address.contains("함양")) {
            return "11H20501";
        } else if (address.contains("거창")) {
            return "11H20502";
        } else if (address.contains("합천")) {
            return "11H20503";
        } else if (address.contains("밀양")) {
            return "11H20601";
        } else if (address.contains("의령")) {
            return "11H20602";
        } else if (address.contains("함안")) {
            return "11H20603";
        } else if (address.contains("창녕")) {
            return "11H20604";
        } else if (address.contains("진주")) {
            return "11H20701";
        } else if (address.contains("산청")) {
            return "11H20703";
        } else if (address.contains("하동")) {
            return "11H20704";
        }
        return ""; // 처리할 조건이 없는 경우 빈 문자열을 반환하거나 다른 처리를 추가할 수 있습니다.
    }
}
