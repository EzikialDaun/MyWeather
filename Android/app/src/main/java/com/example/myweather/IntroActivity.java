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
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
    private ProgressBar progressBar;
    private LocationManager locationManager = null;
    private ArrayList<String> flagKeyArray;
    private int completeCount = 0;
    private MainHandler mainHandler;

    // 스레드 동기화를 위한 핸들러
    private class MainHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            Bundle bundle = msg.getData();
            if (bundle.getBoolean(getString(R.string.key_address))) {
                completeCount++;
            }
            if (bundle.getBoolean(getString(R.string.key_finedust))) {
                completeCount++;
            }
            if (bundle.getBoolean(getString(R.string.key_0h_fcst))) {
                completeCount++;
            }
            if (bundle.getBoolean(getString(R.string.key_24h_fcst))) {
                completeCount++;
            }
            if (bundle.getBoolean(getString(R.string.key_range_fcst))) {
                completeCount++;
            }
            if (bundle.getBoolean(getString(R.string.key_3d_fcst))) {
                completeCount++;
            }
            if (bundle.getBoolean(getString(R.string.key_midterm))) {
                completeCount++;
            }
            if (bundle.getBoolean(getString(R.string.key_uv))) {
                completeCount++;
            }
            boolean isTimeout = bundle.getBoolean(getString(R.string.key_timeout));
            progressBar.setProgress(completeCount);
            if (completeCount == flagKeyArray.size()) {
                if (!isTimeout) {
                    notifyToast("로딩 완료!");
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                if (isTimeout) {
                    notifyToast("로딩 실패 :: timeout");
                    finish();
                }
            }
        }
    }

    // 토스트 출력
    private void notifyToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // GPS 핸들링
    private final LocationListener gpsLocationListener = new LocationListener() {
        // 위치 변동 감지 시
        public void onLocationChanged(Location location) {
            if (location != null) {
                // 1회만 정보 필요하기 때문에 리스너 삭제
                locationManager.removeUpdates(gpsLocationListener);
                ((AppManager) getApplication()).setLatitude(location.getLatitude());
                ((AppManager) getApplication()).setLongitude(location.getLongitude());
                // 테스트용 위경도(애뮬레이터에서 위치 정보를 잘 구하지 못하는 이유로 본 더미 데이터 사용)
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
                        Log.d("Loading", "리버스 지오코딩 완료");
                        setFlagMessage(getString(R.string.key_address));

                        new Thread(() -> {
                            try {
                                // 4 ~ 7일 최고 최저 기온(최근 중기예보)
                                DailyWeatherInfo[] afterThreeDaysWeatherInfo = getAfterThreeDaysWeatherInfo(AddressManager.addressToRegId(address), getResources().getString(R.string.service_key));
                                ((AppManager) getApplication()).setWeeklyTempInfo(afterThreeDaysWeatherInfo[0], 3);
                                ((AppManager) getApplication()).setWeeklyTempInfo(afterThreeDaysWeatherInfo[1], 4);
                                ((AppManager) getApplication()).setWeeklyTempInfo(afterThreeDaysWeatherInfo[2], 5);
                                ((AppManager) getApplication()).setWeeklyTempInfo(afterThreeDaysWeatherInfo[3], 6);
                                Log.d("Loading", "중기예보 로딩 완료");
                                setFlagMessage(getString(R.string.key_midterm));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();

                        new Thread(() -> {
                            try {
                                // 자외선 지수
                                int[] UV = getUVInfo(AddressManager.addressToAreaId(address), getResources().getString(R.string.service_key));
                                ((AppManager) getApplication()).setUV(UV);
                                Log.d("Loading", "자외선 로딩 완료");
                                setFlagMessage(getString(R.string.key_uv));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
                new Thread(() -> {
                    try {
                        TMCoord coord = AddressManager.transCoord(((AppManager) getApplication()).getLatitude(), ((AppManager) getApplication()).getLongitude());
                        JSONArray stationArray = WeatherManager.getStationArray(coord.tmX, coord.tmY, getResources().getString(R.string.service_key));
                        String nearStation = (String) ((JSONObject) stationArray.get(0)).get("stationName");

                        JSONArray finedustResultArray = WeatherManager.getFinedustInfo(nearStation, getResources().getString(R.string.service_key));
                        JSONObject findustResult = (JSONObject) finedustResultArray.get(0);
                        // 측정소 통신 불량 시 "-"
                        String pm10Result = (String) findustResult.get("pm10Value");
                        String pm25Result = (String) findustResult.get("pm25Value");

                        ((AppManager) getApplication()).setFinedustStation(nearStation);
                        ((AppManager) getApplication()).setPm10(pm10Result);
                        ((AppManager) getApplication()).setPm25(pm25Result);

                        setFlagMessage(getString(R.string.key_finedust));
                        Log.d("Loading", "미세먼지 로딩 완료");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
                new Thread(() -> {
                    try {
                        // 현재 날씨(초단기 예보)
                        HourlyWeatherInfo currentWeatherInfo = getCurrentWeatherInfo(grid.x, grid.y, getResources().getString(R.string.service_key));
                        ((AppManager) getApplication()).setCurrentWeatherInfo(currentWeatherInfo);

                        setFlagMessage(getString(R.string.key_0h_fcst));
                        Log.d("Loading", "현재 날씨 로딩 완료");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
                new Thread(() -> {
                    try {
                        // 최근 24시간 날씨(최근 단기예보)
                        HourlyWeatherInfo[] recent24HourWeatherInfo = get24HourWeatherInfo(grid.x, grid.y, getResources().getString(R.string.service_key));
                        ((AppManager) getApplication()).setHour24WeatherInfos(recent24HourWeatherInfo);

                        setFlagMessage(getString(R.string.key_24h_fcst));
                        Log.d("Loading", "24시간 날씨 로딩 완료");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
                new Thread(() -> {
                    try {
                        // 오늘 최고 최저 기온(전 날 마지막 단기예보)
                        DailyWeatherInfo todayWeatherInfo = getTodayTempRange(grid.x, grid.y, getResources().getString(R.string.service_key));
                        ((AppManager) getApplication()).setWeeklyTempInfo(todayWeatherInfo, 0);

                        setFlagMessage(getString(R.string.key_range_fcst));
                        Log.d("Loading", "오늘 최고/최저 온도 로딩 완료");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
                new Thread(() -> {
                    try {
                        // 2 ~ 3일 최고 최저 기온(최근 단기예보)
                        DailyWeatherInfo[] twoDaysWeatherInfo = getAfterTodayWeatherInfo(grid.x, grid.y, getResources().getString(R.string.service_key));
                        ((AppManager) getApplication()).setWeeklyTempInfo(twoDaysWeatherInfo[0], 1);
                        ((AppManager) getApplication()).setWeeklyTempInfo(twoDaysWeatherInfo[1], 2);

                        setFlagMessage(getString(R.string.key_3d_fcst));
                        Log.d("Loading", "3일 날씨 로딩 완료");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
                new Handler().postDelayed(() -> setFlagMessage(getString(R.string.key_timeout)), 1000 * 30);
            }
        }

        // 입력받은 키에 true 설정 후 핸들러 큐에 보냄
        private void setFlagMessage(String key) {
            Message message = mainHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putBoolean(key, true);
            message.setData(bundle);
            mainHandler.sendMessage(message);
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    // 최근 자외선 예보로 12시간 자외선 지수 구하기
    private int[] getUVInfo(String areaId, String serviceKey) throws IOException, ParseException {
        // 3시간 간격 발표
        final int FCST_INTERVAL = 3;
        // 3시간 * 4 = 12시간
        final int MAX_FCST_CNT = 4;
        int[] result = new int[MAX_FCST_CNT];
        LocalDateTime baseDate = DateManager.getRecentMidForecastTime();
        JSONArray fcstResult = WeatherManager.getUVFcst(areaId, baseDate, serviceKey);
        // 기상청에서 배열로 제공하지만 원소가 하나밖에 없음. 따라서 첫 원소만 인덱싱.
        JSONObject jsonObject = (JSONObject) fcstResult.get(0);
        for (int i = 0; i < MAX_FCST_CNT; i++) {
            result[i] = Integer.parseInt((String) jsonObject.get("h" + i * FCST_INTERVAL));
        }
        return result;
    }

    // 최근 단기예보로 24시간 날씨 구하기
    private HourlyWeatherInfo[] get24HourWeatherInfo(int nx, int ny, String serviceKey) throws IOException, ParseException {
        HourlyWeatherInfo[] HourlyInfo = new HourlyWeatherInfo[((AppManager) getApplication()).getHoursInDay()];
        LocalDateTime baseDate = DateManager.getRecentShortForecastTime();
        JSONArray srtFcstResult = WeatherManager.getshortForecast(nx, ny, baseDate, serviceKey, 0);
        ArrayList<String> tempArray = new ArrayList<>();
        ArrayList<String> skyArray = new ArrayList<>();
        ArrayList<Integer> popArray = new ArrayList<>();
        ArrayList<String> ptyArray = new ArrayList<>();
        try {
            for (int i = 0; i < srtFcstResult.size(); i++) {
                JSONObject jsonObject = (JSONObject) srtFcstResult.get(i);
                if (Objects.equals(jsonObject.get(getResources().getString(R.string.category)), getResources().getString(R.string.temperature))) {
                    String temp = (String) jsonObject.get(getResources().getString(R.string.forecast_value));
                    tempArray.add(temp);
                } else if (Objects.equals(jsonObject.get(getResources().getString(R.string.category)), getResources().getString(R.string.sky))) {
                    String sky = (String) jsonObject.get(getResources().getString(R.string.forecast_value));
                    skyArray.add(sky);
                } else if (Objects.equals(jsonObject.get(getResources().getString(R.string.category)), getResources().getString(R.string.pop))) {
                    int pop = Integer.parseInt((String) jsonObject.get(getResources().getString(R.string.forecast_value)));
                    popArray.add(pop);
                } else if (Objects.equals(jsonObject.get(getResources().getString(R.string.category)), getResources().getString(R.string.pty))) {
                    String pty = (String) jsonObject.get(getResources().getString(R.string.forecast_value));
                    ptyArray.add(pty);
                }
            }
            for (int i = 0; i < ((AppManager) getApplication()).getHoursInDay(); i++) {
                String temp = tempArray.get(i);
                HourlyInfo[i] = new HourlyWeatherInfo();
                HourlyInfo[i].setBaseDate(baseDate);
                HourlyInfo[i].setTargetDate(baseDate.plusHours(i));
                HourlyInfo[i].setTemperature(Double.parseDouble(temp));
                HourlyInfo[i].setSky(skyArray.get(i));
                HourlyInfo[i].setPrecipitation(ptyArray.get(i));
                HourlyInfo[i].setPop(popArray.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return HourlyInfo;
    }

    // 최근 단기예보로 2 ~ 3일 날씨 구하기
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

    // 초단기 예보로 현재 날씨 구하기
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
                String PROPERTY_WIND_DIRECTION = "VEC";
                String PROPERTY_HUMIDITY = "REH";
                String PROPERTY_WIND_SPEED = "WSD";
                String PROPERTY_RAIN_ONE_HOUR = "RN1";
                if (Objects.equals(dataLine.get(getResources().getString(R.string.category)), PROPERTY_TEMPERATURE_ONE_HOUR)) {
                    result.setTemperature(Double.parseDouble(dataLine.get(getResources().getString(R.string.forecast_value)).toString()));
                } else if (Objects.equals(dataLine.get(getResources().getString(R.string.category)), getResources().getString(R.string.pty))) {
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

    // 어제의 마지막 단기 예보로 오늘 최고/최저 온도 구하기
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

    // 중기 예보를 통해 3 ~ 7일의 날씨 정보 구하기
    private DailyWeatherInfo[] getAfterThreeDaysWeatherInfo(String regId, String serviceKey) throws IOException, ParseException {
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

    // 인트로 액티비티 초기화
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // 로딩 목록 초기화
        flagKeyArray = new ArrayList<>();
        flagKeyArray.add(getString(R.string.key_address));
        flagKeyArray.add(getString(R.string.key_finedust));
        flagKeyArray.add(getString(R.string.key_0h_fcst));
        flagKeyArray.add(getString(R.string.key_range_fcst));
        flagKeyArray.add(getString(R.string.key_24h_fcst));
        flagKeyArray.add(getString(R.string.key_3d_fcst));
        flagKeyArray.add(getString(R.string.key_uv));
        flagKeyArray.add(getString(R.string.key_midterm));

        // 로딩 바 초기화
        progressBar = findViewById(R.id.prg_intro);
        progressBar.setMax(flagKeyArray.size());
        progressBar.setProgress(0);

        // 위치 관리자 초기화
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 스레드 동기화를 위한 핸들러 초기화
        mainHandler = new MainHandler();

        // 이벤트 리스너 설정
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        } else {
            // 10초 주기로 요청
            final int REQ_PERIOD = 1000 * 10;
            // 1M 거리
            final int REQ_DIST = 1;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, REQ_PERIOD, REQ_DIST, gpsLocationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, REQ_PERIOD, REQ_DIST, gpsLocationListener);
        }

        // 동작하는 위치 제공자가 없으면
        if (locationManager.getProviders(true).size() == 0) {
            // 권한 확인 메세지 띄움
            Toast.makeText(this, getResources().getString(R.string.check_permission), Toast.LENGTH_LONG).show();
        }
    }
}