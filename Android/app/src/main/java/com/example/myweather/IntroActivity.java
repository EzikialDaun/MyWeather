package com.example.myweather;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;


public class IntroActivity extends AppCompatActivity {
    private LocationManager locationManager = null;

    // GPS 핸들링
    private final LocationListener gpsLocationListener = new LocationListener() {
        // 위치 변동 감지 시
        public void onLocationChanged(Location location) {
            if (location != null) {
                // 1회만 정보 필요하기 때문에 리스너 삭제
                locationManager.removeUpdates(gpsLocationListener);
                // 위도
                ((AppManager) getApplication()).setLatitude(location.getLatitude());
                // 경도
                ((AppManager) getApplication()).setLongitude(location.getLongitude());
                // 테스트용 위경도
                // ((AppManager) getApplication()).setLatitude(37.44634751587985);
                // ((AppManager) getApplication()).setLongitude(126.6816659048645);
                // 위경도를 기상청 격자좌표로 변환
                LatXLngY grid = AddressManager.convertGRID_GPS(((AppManager) getApplication()).getLatitude(), ((AppManager) getApplication()).getLongitude());
                // 데이터 요청부
                new Thread(() -> {
                    try {
                        // 카카오 API 리버스 지오코딩
                        String address = AddressManager.getAddress(((AppManager) getApplication()).getLatitude(), ((AppManager) getApplication()).getLongitude());
                        ((AppManager) getApplication()).setAddress(address);

                        // 현재 날씨(초단기 예보)
                        HourlyWeatherInfo currentWeatherInfo = getCurrentWeatherInfo(grid.x, grid.y, getResources().getString(R.string.service_key));
                        ((AppManager) getApplication()).setCurrentWeatherInfo(currentWeatherInfo);

                        // 최근 24시간 날씨(최근 단기예보)
                        HourlyWeatherInfo[] recent24HourWeatherInfo = get24HourWeatherInfo(grid.x, grid.y, getResources().getString(R.string.service_key));
                        ((AppManager) getApplication()).setHour24WeatherInfos(recent24HourWeatherInfo);

                        // 오늘 최고 최저 기온(전 날 마지막 단기예보)
                        DailyWeatherInfo todayWeatherInfo = getTodayTempRange(grid.x, grid.y, getResources().getString(R.string.service_key));
                        ((AppManager) getApplication()).setWeeklyTempInfo(todayWeatherInfo, 0);

                        // 2 ~ 3일 최고 최저 기온(최근 단기예보)
                        DailyWeatherInfo[] twoDaysWeatherInfo = getAfterTodayWeatherInfo(grid.x, grid.y, getResources().getString(R.string.service_key));
                        ((AppManager) getApplication()).setWeeklyTempInfo(twoDaysWeatherInfo[0], 1);
                        ((AppManager) getApplication()).setWeeklyTempInfo(twoDaysWeatherInfo[1], 2);

                        // 4 ~ 7일 최고 최저 기온(최근 중기예보)
                        DailyWeatherInfo[] afterThreeDaysWeatherInfo = getAfterThreeDaysWeatherInfo(AddressManager.addressToRegId(address), getResources().getString(R.string.service_key));
                        ((AppManager) getApplication()).setWeeklyTempInfo(afterThreeDaysWeatherInfo[0], 3);
                        ((AppManager) getApplication()).setWeeklyTempInfo(afterThreeDaysWeatherInfo[1], 4);
                        ((AppManager) getApplication()).setWeeklyTempInfo(afterThreeDaysWeatherInfo[2], 5);
                        ((AppManager) getApplication()).setWeeklyTempInfo(afterThreeDaysWeatherInfo[3], 6);

                        int UV = getUVInfo(AddressManager.addressToAreaId(address), getResources().getString(R.string.service_key));
                        ((AppManager) getApplication()).setUV(UV);

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent); //인트로 실행 후 바로 MainActivity로 넘어감.
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    private int getUVInfo(String areaId, String serviceKey) throws IOException, ParseException {
        LocalDateTime baseDate = DateManager.getLastUVForecastTime();
        JSONArray result = WeatherManager.getUVFcst(areaId, baseDate, serviceKey);
        JSONObject jsonObject = (JSONObject) result.get(0);
        return Integer.parseInt((String) jsonObject.get("h18"));
    }

    private HourlyWeatherInfo[] get24HourWeatherInfo(int nx, int ny, String serviceKey) throws IOException, ParseException {
        HourlyWeatherInfo[] HourlyInfo = new HourlyWeatherInfo[((AppManager) getApplication()).getHoursInDay()];
        LocalDateTime baseDate = DateManager.getRecentShortForecastTime();
        JSONArray srtFcstResult = WeatherManager.getshortForecast(nx, ny, baseDate, serviceKey, 0);
        ArrayList<String> tempArray = new ArrayList<>();
        ArrayList<String> skyArray = new ArrayList<>();
        try {
            for (int i = 0; i < srtFcstResult.size(); i++) {
                JSONObject jsonObject = (JSONObject) srtFcstResult.get(i);
                if (Objects.equals(jsonObject.get(getResources().getString(R.string.category)), getResources().getString(R.string.temperature))) {
                    String temp = (String) jsonObject.get(getResources().getString(R.string.forecast_value));
                    tempArray.add(temp);
                } else if (Objects.equals(jsonObject.get(getResources().getString(R.string.category)), getResources().getString(R.string.sky))) {
                    String sky = (String) jsonObject.get(getResources().getString(R.string.forecast_value));
                    skyArray.add(sky);
                }
            }
            for (int i = 0; i < ((AppManager) getApplication()).getHoursInDay(); i++) {
                String temp = tempArray.get(i);
                HourlyInfo[i] = new HourlyWeatherInfo();
                HourlyInfo[i].setBaseDate(baseDate);
                HourlyInfo[i].setTargetDate(baseDate.plusHours(i));
                HourlyInfo[i].setTemperature(Double.parseDouble(temp));
                HourlyInfo[i].setSky(skyArray.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return HourlyInfo;
    }

    private DailyWeatherInfo[] getAfterTodayWeatherInfo(int nx, int ny, String serviceKey) throws IOException, ParseException {
        DailyWeatherInfo[] DailyInfo = new DailyWeatherInfo[((AppManager) getApplication()).getShortDayLimit()];
        LocalDateTime baseDate = DateManager.getRecentShortForecastTime();
        JSONArray srtFcstResult = WeatherManager.getshortForecast(nx, ny, baseDate, serviceKey, 0);
        ArrayList<String> maxArray = new ArrayList<>();
        ArrayList<String> minArray = new ArrayList<>();
        try {
            for (int i = 0; i < srtFcstResult.size(); i++) {
                JSONObject jsonObject = (JSONObject) srtFcstResult.get(i);
                if (Objects.equals(jsonObject.get(getResources().getString(R.string.category)), getResources().getString(R.string.temp_max))) {
                    if (!Objects.equals(jsonObject.get(getResources().getString(R.string.base_date)), jsonObject.get(getResources().getString(R.string.forecast_date)))) {
                        String max = (String) jsonObject.get(getResources().getString(R.string.forecast_value));
                        maxArray.add(max);
                    }
                } else if (Objects.equals(jsonObject.get(getResources().getString(R.string.category)), getResources().getString(R.string.temp_min))) {
                    if (!Objects.equals(jsonObject.get(getResources().getString(R.string.base_date)), jsonObject.get(getResources().getString(R.string.forecast_date)))) {
                        String min = (String) jsonObject.get(getResources().getString(R.string.forecast_value));
                        minArray.add(min);
                    }
                }
            }
            for (int i = 0; i < ((AppManager) getApplication()).getShortDayLimit(); i++) {
                LocalDateTime date = baseDate.plusDays(i + 1);
                DailyInfo[i] = new DailyWeatherInfo();
                DailyInfo[i].setBaseDate(baseDate);
                DailyInfo[i].setTargetDate(date);
                int max = (int) Double.parseDouble(maxArray.get(i));
                DailyInfo[i].setTempMax(max);
                int min = (int) Double.parseDouble(minArray.get(i));
                DailyInfo[i].setTempMin(min);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DailyInfo;
    }

    private HourlyWeatherInfo getCurrentWeatherInfo(int nx, int ny, String serviceKey) throws IOException, ParseException {
        HourlyWeatherInfo result = new HourlyWeatherInfo();
        LocalDateTime lastUltraSrtFcstTime = DateManager.getLastUltraSrtFcstTime();
        JSONArray lastUltraSrtFcstResult = WeatherManager.getUltraSrtFcst(nx, ny, lastUltraSrtFcstTime, serviceKey);
        String FORMAT_SIMPLE_TIME = "HHmm";
        String currentHour = lastUltraSrtFcstTime.plusHours(1).withMinute(0).format(DateTimeFormatter.ofPattern(FORMAT_SIMPLE_TIME));
        for (Object o : lastUltraSrtFcstResult) {
            JSONObject dataLine = (JSONObject) o;
            String PROPERTY_FORECAST_TIME = "fcstTime";
            if (Objects.equals(dataLine.get(PROPERTY_FORECAST_TIME), currentHour)) {
                String PROPERTY_TEMPERATURE_ONE_HOUR = "T1H";
                String PROPERTY_PRECIPITATION = "PTY";
                String PROPERTY_WIND_DIRECTION = "VEC";
                String PROPERTY_HUMIDITY = "REH";
                String PROPERTY_WIND_SPEED = "WSD";
                String PROPERTY_RAIN_ONE_HOUR = "RN1";
                if (Objects.equals(dataLine.get(getResources().getString(R.string.category)), PROPERTY_TEMPERATURE_ONE_HOUR)) {
                    result.setTemperature(Double.parseDouble(dataLine.get(getResources().getString(R.string.forecast_value)).toString()));
                } else if (Objects.equals(dataLine.get(getResources().getString(R.string.category)), PROPERTY_PRECIPITATION)) {
                    result.setPrecipitation(dataLine.get(getResources().getString(R.string.forecast_value)).toString());
                } else if (Objects.equals(dataLine.get(getResources().getString(R.string.category)), getResources().getString(R.string.sky))) {
                    result.setSky(dataLine.get(getResources().getString(R.string.forecast_value)).toString());
                } else if (Objects.equals(dataLine.get(getResources().getString(R.string.category)), PROPERTY_HUMIDITY)) {
                    result.setHumidity(Integer.parseInt(dataLine.get(getResources().getString(R.string.forecast_value)).toString()));
                } else if (Objects.equals(dataLine.get(getResources().getString(R.string.category)), PROPERTY_WIND_DIRECTION)) {
                    result.setWindDirection(Integer.parseInt(dataLine.get(getResources().getString(R.string.forecast_value)).toString()));
                } else if (Objects.equals(dataLine.get(getResources().getString(R.string.category)), PROPERTY_WIND_SPEED)) {
                    result.setWindSpeed(Double.parseDouble(dataLine.get(getResources().getString(R.string.forecast_value)).toString()));
                } else if (Objects.equals(dataLine.get(getResources().getString(R.string.category)), PROPERTY_RAIN_ONE_HOUR)) {
                    result.setRainScale(dataLine.get(getResources().getString(R.string.forecast_value)).toString());
                }
            }
        }
        return result;
    }

    private DailyWeatherInfo getTodayTempRange(int nx, int ny, String serviceKey) throws IOException, ParseException {
        DailyWeatherInfo result = new DailyWeatherInfo();
        // 어제 마지막 단기예보 발표시각(하루 전 2300) 구하기
        LocalDateTime yesterday = DateManager.getYesterdayShortForecastTime();
        // 단기예보 요청하기
        JSONArray yesterdaySrtFcstResult = WeatherManager.getshortForecast(nx, ny, yesterday, serviceKey, 0);
        for (int i = 0; i < yesterdaySrtFcstResult.size(); i++) {
            JSONObject jsonObject = (JSONObject) yesterdaySrtFcstResult.get(i);
            if (Objects.equals(jsonObject.get(getResources().getString(R.string.category)), getResources().getString(R.string.temp_max))) {
                if (!Objects.equals(jsonObject.get(getResources().getString(R.string.base_date)), jsonObject.get(getResources().getString(R.string.forecast_date)))) {
                    int max = (int) Double.parseDouble((String) jsonObject.get(getResources().getString(R.string.forecast_value)));
                    result.setTempMax(max);
                }
            } else if (Objects.equals(jsonObject.get(getResources().getString(R.string.category)), getResources().getString(R.string.temp_min))) {
                if (!Objects.equals(jsonObject.get(getResources().getString(R.string.base_date)), jsonObject.get(getResources().getString(R.string.forecast_date)))) {
                    int min = (int) Double.parseDouble((String) jsonObject.get(getResources().getString(R.string.forecast_value)));
                    result.setTempMin(min);
                }
            }
        }
        return result;
    }

    private DailyWeatherInfo[] getAfterThreeDaysWeatherInfo(String regId, String serviceKey) throws IOException, ParseException {
        // 1일 2일 4일
        final int MAX_INDEX = ((AppManager) getApplication()).getDaysInWeek() - ((AppManager) getApplication()).getShortDayLimit() - 1;
        DailyWeatherInfo[] result = new DailyWeatherInfo[MAX_INDEX];
        LocalDateTime baseDate = DateManager.getRecentMidForecastTime();
        JSONObject fcstResult = (JSONObject) WeatherManager.getMidFcstTemp(regId, baseDate, serviceKey).get(0);
        for (int i = 0; i < MAX_INDEX; i++) {
            result[i] = new DailyWeatherInfo();
            result[i].setBaseDate(baseDate);
            result[i].setTargetDate(baseDate.plusDays(i + 3));
        }
        result[0].setTempMax(Integer.parseInt(String.valueOf(fcstResult.get(getResources().getString(R.string.temp_max_3)))));
        result[0].setTempMin(Integer.parseInt(String.valueOf(fcstResult.get(getResources().getString(R.string.temp_min_3)))));
        result[1].setTempMax(Integer.parseInt(String.valueOf(fcstResult.get(getResources().getString(R.string.temp_max_4)))));
        result[1].setTempMin(Integer.parseInt(String.valueOf(fcstResult.get(getResources().getString(R.string.temp_min_4)))));
        result[2].setTempMax(Integer.parseInt(String.valueOf(fcstResult.get(getResources().getString(R.string.temp_max_5)))));
        result[2].setTempMin(Integer.parseInt(String.valueOf(fcstResult.get(getResources().getString(R.string.temp_min_5)))));
        result[3].setTempMax(Integer.parseInt(String.valueOf(fcstResult.get(getResources().getString(R.string.temp_max_6)))));
        result[3].setTempMin(Integer.parseInt(String.valueOf(fcstResult.get(getResources().getString(R.string.temp_min_6)))));
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 10초 주기로 요청
        final int REQ_PERIOD = 1000 * 10;
        // 1M 거리
        final int REQ_DIST = 1;

        // 이벤트 리스너 설정
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, REQ_PERIOD, REQ_DIST, gpsLocationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, REQ_PERIOD, REQ_DIST, gpsLocationListener);
        }

        if (locationManager.getProviders(true).size() == 0) {
            Toast.makeText(this, getResources().getString(R.string.check_permission), Toast.LENGTH_LONG).show();
        }
    }
}