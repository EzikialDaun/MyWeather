package com.example.myweather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private TextView txtAddress;
    private TextView txtDatetime;
    private TextView txtCurrentTemp;
    private TextView txtTodayRange;
    private TextView txtComment;
    private ImageView imageView;

    private final int NUMBER_PER_DATE_ULTRA_SHORT_FORECAST = 10;
    private final int HOURS_ULTRA_LIMIT = 6;

    // 기상청에서 제공받은 서비스 키
    private final String SERVICE_KEY = "A6z4dofotOGc96pa7%2FDl3WgE2cW1orNWcou7slmHGBqY9G1Z9LMaVoO1dETagYiNdaPI%2Fe6vGYftFK9e2EjEZA%3D%3D";

    private final String FORMAT_SIMPLE_TIME = "HHmm";

    private final String PROPERTY_FORECAST_TIME = "fcstTime";
    private final String PROPERTY_FORECAST_VALUE = "fcstValue";
    private final String PROPERTY_CATEGORY = "category";
    private final String PROPERTY_TEMPERATURE_ONE_HOUR = "T1H";
    private final String PROPERTY_PRECIPITATION = "PTY";
    private final String PROPERTY_SKY = "SKY";
    private final String PROPERTY_HUMIDITY = "REH";
    private final String PROPERTY_WIND_DIRECTION = "VEC";
    private final String PROPERTY_WIND_SPEED = "WSD";
    private final String PROPERTY_RAIN_ONE_HOUR = "RN1";
    private final String PROPERTY_TEMPERATURE = "TMP";

    private final String TARGET_ADDRESS = "TARGET_ADDRESS";
    private final String TARGET_TEMP_RANGE = "TARGET_TEMP_RANGE";
    private final String TARGET_CURRENT_INFO = "TARGET_CURRENT_INFO";

    private final String KEY_TARGET = "TARGET";
    private final String KEY_ADDRESS = "ADDRESS";
    private final String KEY_CURRENT_TEMP = "CURRENT_TEMP";
    private final String KEY_PRECIPITATION = "PRECIPITATION";
    private final String KEY_SKY = "SKY";
    private final String KEY_HUMIDITY = "HUMIDITY";
    private final String KEY_WIND_SPEED = "WIND_SPEED";
    private final String KEY_TEMP_MAX_TODAY = "TEMP_MAX_TODAY";
    private final String KEY_TEMP_MIN_TODAY = "TEMP_MIN_TODAY";

    private final MyHandler handler = new MyHandler(this);
    private LocationManager locationManager = null;

    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> weakReference;

        public MyHandler(MainActivity activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = weakReference.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }

    private void handleMessage(Message msg) {
        Bundle bundle = msg.getData();

        String address = bundle.getString(KEY_ADDRESS);
        if (address != null) {
            txtAddress.setText(address);
        }

        int currentTemp = (int) bundle.getDouble(KEY_CURRENT_TEMP);
        txtCurrentTemp.setText(currentTemp + "°C");

        int maxTempToday = bundle.getInt(KEY_TEMP_MAX_TODAY);
        int minTempToday = bundle.getInt(KEY_TEMP_MIN_TODAY);
        txtTodayRange.setText("최고 " + maxTempToday + "°C | 최저 " + minTempToday + "°C");

        boolean isLargeDiff = false;
        if (maxTempToday - minTempToday >= 10) {
            isLargeDiff = true;
        }

        if (currentTemp >= 33) {
            if (isLargeDiff) {
                imageView.setImageResource(R.drawable.img_1_d);
                txtComment.setText("낮에는 HOT, 저녁은 COOL한 날씨 \n" + "가디건을 챙기자\n" + "#여름가디건 #바람막이");
            } else {
                imageView.setImageResource(R.drawable.img_1);
                txtComment.setText("파격적인 날씨! 최대한 얇게 입자\n" + "#나시 #원피스 #기능성티");
            }
        } else if (currentTemp >= 28 && currentTemp <= 32) {
            if (isLargeDiff) {
                imageView.setImageResource(R.drawable.img_2_d);
                txtComment.setText("분명 여름인데 아직 조금쌀쌀한가?\n" + "#여름가디건 #여름볼레로");
            } else {
                imageView.setImageResource(R.drawable.img_2);
                txtComment.setText("너무 더워.. 이제 진짜 여름이다\n" + "#반팔 #크롭티");
            }
        } else if (currentTemp >= 23 && currentTemp <= 27) {
            if (isLargeDiff) {
                imageView.setImageResource(R.drawable.img_3_d);
                txtComment.setText("초여름이라 아직 쌀쌀하네 \n" + "#블레이저 #나시");
            } else {
                imageView.setImageResource(R.drawable.img_3);
                txtComment.setText("초여름인가 ? 곧 더워질테니 여름옷 개시 ! \n" + "#반팔티 #셔츠");
            }
        } else if (currentTemp >= 20 && currentTemp <= 22) {
            if (isLargeDiff) {
                imageView.setImageResource(R.drawable.img_4_d);
                txtComment.setText("따뜻해도 아침 저녁으론 쌀쌀해 \n" + "#얇은외투");
            } else {
                imageView.setImageResource(R.drawable.img_4);
                txtComment.setText("따뜻해 나들이가기 딱 좋은 날씨 :) \n" + "#피크닉");
            }
        } else if (currentTemp >= 17 && currentTemp <= 19) {
            if (isLargeDiff) {
                imageView.setImageResource(R.drawable.img_5_d);
                txtComment.setText("아침 저녁으로 쌀쌀할거야!\n" + "#자켓");
            } else {
                imageView.setImageResource(R.drawable.img_5);
                txtComment.setText("따뜻하기고 선선하기도 그저그런 날씨 :)\n" + "#단독 #맨투맨");
            }
        } else if (currentTemp >= 12 && currentTemp <= 16) {
            if (isLargeDiff) {
                imageView.setImageResource(R.drawable.img_6_d);
                txtComment.setText("아침 저녁으로 추워 벌써 겨울 아니야? \n" + "#감기조심");
            } else {
                imageView.setImageResource(R.drawable.img_6);
                txtComment.setText("조금 쌀쌀하네~ \n" + "#자켓 #외투필수");
            }
        } else if (currentTemp >= 9 && currentTemp <= 11) {
            if (isLargeDiff) {
                txtComment.setText("아침 저녁으로 일교차가 심하니까 좀 껴입자!\n" + "#트렌치코트 #니트 #비니 #모자");
            } else {
                txtComment.setText("날씨가 사알짝 춥고 쌀쌀해 ~\n" + "#트렌치코트 #니트 #청바지");
            }
        } else if (currentTemp >= 5 && currentTemp <= 8) {
            if (isLargeDiff) {

            }
        } else {
            if (isLargeDiff) {
                imageView.setImageResource(R.drawable.img_9_d);
                txtComment.setText("원래도 추운데.. 아침 저녁에는 더~ 추워\n" + "#목도리 #비니 #히트텍 #롱패딩");
            } else {
                imageView.setImageResource(R.drawable.img_9);
                txtComment.setText("이게 바로 겨울이지... 두껍게 껴입자!\n" + "#히트텍 #기모 #패딩");
            }
        }
    }

    private HourlyWeatherInfo getCurrentWeatherInfo(int nx, int ny, String serviceKey) throws IOException, ParseException {
        HourlyWeatherInfo result = new HourlyWeatherInfo();
        LocalDateTime lastUltraSrtFcstTime = DateManager.getLastUltraSrtFcstTime();
        JSONArray lastUltraSrtFcstResult = WeatherManager.getUltraSrtFcst(nx, ny, lastUltraSrtFcstTime, serviceKey);
        LocalDateTime currentDate = lastUltraSrtFcstTime.plusHours(1).withMinute(0);
        String currentHour = currentDate.format(DateTimeFormatter.ofPattern(FORMAT_SIMPLE_TIME));
        if (lastUltraSrtFcstResult.size() == NUMBER_PER_DATE_ULTRA_SHORT_FORECAST * HOURS_ULTRA_LIMIT) {
            for (Object o : lastUltraSrtFcstResult) {
                JSONObject dataLine = (JSONObject) o;
                if (dataLine.get(PROPERTY_FORECAST_TIME).equals(currentHour)) {
                    if (dataLine.get(PROPERTY_CATEGORY).equals(PROPERTY_TEMPERATURE_ONE_HOUR)) {
                        result.setTemperature(Double.parseDouble(dataLine.get(PROPERTY_FORECAST_VALUE).toString()));
                    } else if (dataLine.get(PROPERTY_CATEGORY).equals(PROPERTY_PRECIPITATION)) {
                        result.setPrecipitation(dataLine.get(PROPERTY_FORECAST_VALUE).toString());
                    } else if (dataLine.get(PROPERTY_CATEGORY).equals(PROPERTY_SKY)) {
                        result.setSky(dataLine.get(PROPERTY_FORECAST_VALUE).toString());
                    } else if (dataLine.get(PROPERTY_CATEGORY).equals(PROPERTY_HUMIDITY)) {
                        result.setHumidity(Integer.parseInt(dataLine.get(PROPERTY_FORECAST_VALUE).toString()));
                    } else if (dataLine.get(PROPERTY_CATEGORY).equals(PROPERTY_WIND_DIRECTION)) {
                        result.setWindDirection(Integer.parseInt(dataLine.get(PROPERTY_FORECAST_VALUE).toString()));
                    } else if (dataLine.get(PROPERTY_CATEGORY).equals(PROPERTY_WIND_SPEED)) {
                        result.setWindSpeed(Double.parseDouble(dataLine.get(PROPERTY_FORECAST_VALUE).toString()));
                    } else if (dataLine.get(PROPERTY_CATEGORY).equals(PROPERTY_RAIN_ONE_HOUR)) {
                        result.setRainScale(dataLine.get(PROPERTY_FORECAST_VALUE).toString());
                    }
                }
            }
        }
        return result;
    }

    private DailyWeatherInfo getTodayTempRange(int nx, int ny, String serviceKey) throws IOException, ParseException {
        DailyWeatherInfo result;
        JSONArray yesterdaySrtFcstResult;

        final int HOUR_OF_DAY = 24;
        final int NUMBER_PER_DATE_SHORT_FORECAST = 12;
        final int NUMBER_REQUEST = HOUR_OF_DAY * NUMBER_PER_DATE_SHORT_FORECAST;

        // 어제 마지막 단기예보 발표시각(하루 전 2300) 구하기
        LocalDateTime yesterday = DateManager.getYesterdayShortForecastTime();
        // 단기예보 요청하기
        yesterdaySrtFcstResult = WeatherManager.getVillageFcst(nx, ny, yesterday, serviceKey, NUMBER_REQUEST);
        if (yesterdaySrtFcstResult.size() == NUMBER_REQUEST) {
            ArrayList<Integer> tempList = new ArrayList<>();
            for (int i = 0; i < NUMBER_REQUEST; i++) {
                JSONObject dataLine = (JSONObject) yesterdaySrtFcstResult.get(i);
                if (dataLine.get(PROPERTY_CATEGORY).equals(PROPERTY_TEMPERATURE)) {
                    tempList.add(Integer.parseInt(dataLine.get(PROPERTY_FORECAST_VALUE).toString()));
                }
            }
            // 데이터 길이 체크
            if (tempList.size() == HOUR_OF_DAY) {
                result = new DailyWeatherInfo(yesterday);
                result.setTempMax(Collections.max(tempList));
                result.setTempMin(Collections.min(tempList));
                // 리스트를 반으로 나누어 각각의 최고, 최저 기온 구하기
                final int HALF_DAY = HOUR_OF_DAY / 2;
                ArrayList<Integer> tempListAm = new ArrayList<>(tempList.subList(0, HALF_DAY));
                ArrayList<Integer> tempListPm = new ArrayList<>(tempList.subList(HALF_DAY, HOUR_OF_DAY));
                result.setTempMaxAm(Collections.max(tempListAm));
                result.setTempMinAm(Collections.min(tempListAm));
                result.setTempMaxPm(Collections.max(tempListPm));
                result.setTempMinPm(Collections.min(tempListPm));
                LocalDateTime todayDate = yesterday.plusDays(1);
                result.setForecastDate(todayDate);
            } else {
                throw new NullPointerException();
            }
        } else {
            throw new NullPointerException();
        }
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 타이틀 바 제거
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        txtAddress = findViewById(R.id.textview_address);
        txtDatetime = findViewById(R.id.textview_datetime);
        txtCurrentTemp = findViewById(R.id.textview_ctemp);
        txtTodayRange = findViewById(R.id.textview_today_range);
        txtComment = findViewById(R.id.textview_comment);
        imageView = findViewById(R.id.imageView);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        final int MILSEC_TO_SEC = 1000;
        final int SEC_TO_MINUTE = 60;
        // 1분 주기로 요청
        final int REQ_PERIOD = MILSEC_TO_SEC * SEC_TO_MINUTE;
        // 1M 거리
        final int REQ_DIST = 1;

        // 이벤트 리스너 설정
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, REQ_PERIOD, REQ_DIST, gpsLocationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, REQ_PERIOD, REQ_DIST, gpsLocationListener);
        }
    }

    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            if (location != null) {
                locationManager.removeUpdates(gpsLocationListener);
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatXLngY grid = AddressManager.convertGRID_GPS(latitude, longitude);
                LocalDateTime dateTime = LocalDateTime.now();
                txtDatetime.setText(dateTime.getHour() + getString(R.string.main_hour));
                new Thread(() -> {
                    // 카카오 API 리버스 지오코딩
                    try {
                        Bundle bundle = new Bundle();
                        Message message = handler.obtainMessage();

                        String address = AddressManager.getAddress(latitude, longitude);
                        bundle.putString(KEY_ADDRESS, address);

                        HourlyWeatherInfo currentWeatherInfo = getCurrentWeatherInfo(grid.x, grid.y, SERVICE_KEY);
                        bundle.putDouble(KEY_CURRENT_TEMP, currentWeatherInfo.getTemperature());
                        bundle.putString(KEY_PRECIPITATION, currentWeatherInfo.getPrecipitationString());
                        bundle.putString(KEY_SKY, currentWeatherInfo.getSkyString());
                        bundle.putInt(KEY_HUMIDITY, currentWeatherInfo.getHumidity());
                        bundle.putDouble(KEY_WIND_SPEED, currentWeatherInfo.getWindSpeed());

                        DailyWeatherInfo todayWeatherInfo = getTodayTempRange(grid.x, grid.y, SERVICE_KEY);
                        bundle.putInt(KEY_TEMP_MAX_TODAY, todayWeatherInfo.getTempMax());
                        bundle.putInt(KEY_TEMP_MIN_TODAY, todayWeatherInfo.getTempMin());

                        message.setData(bundle);
                        handler.sendMessage(message);
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
}