package com.example.myweather;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private TextView txtCurrentTemp;
    private TextView txtRange;
    private TextView txtComment;
    private ImageView imgModel;
    private Button btnToInfo;
    private long backPressedTime = 0;

    private void renderUI() {
        try {
            HourlyWeatherInfo currentWeatherInfo = ((AppManager) getApplication()).getCurrentWeatherInfo();
            int currentTemp = (int) currentWeatherInfo.getTemperature();
            txtCurrentTemp.setText(currentTemp + "°C");

            DailyWeatherInfo todayWeatherInfo = ((AppManager) getApplication()).getWeeklyTempInfo(0);
            int maxTempToday = todayWeatherInfo.getTempMax();
            int minTempToday = todayWeatherInfo.getTempMin();
            txtRange.setText("최고 " + maxTempToday + "°C | 최저 " + minTempToday + "°C");

            // 일교차
            final int LARGE_DIFF_REF = 10;
            boolean isLargeDiff = maxTempToday - minTempToday >= LARGE_DIFF_REF;

            boolean isRainy;
            String ptyString = currentWeatherInfo.getPrecipitationString();
            if (ptyString.equals("비") || ptyString.equals("빗방울")) {
                isRainy = true;
            } else {
                isRainy = false;
            }

            boolean isHighFineDust = false;

            boolean isHighUV;
            int UV = ((AppManager) getApplication()).getUV()[((AppManager) getApplication()).getUVTargetIndex()];
            final int HIGH_UV_THRESHOLD = 7;
            if (UV >= HIGH_UV_THRESHOLD) {
                isHighUV = true;
            } else {
                isHighUV = false;
            }

            // 위 조건에 따라 코디 및 멘트 추천
            if (currentTemp >= 33) {
                if (isLargeDiff) {
                    if (!isRainy && isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_1_d_u);
                        txtComment.setText("분명 더웠는데 조금 쌀쌀하네... 눈까지 부셔\n" + "#여름가디건 #선글라스");
                    } else if (!isRainy && !isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_1_d_f);
                        txtComment.setText("아침저녁은 살짝 쌀쌀한데 미세먼지 뭐야!\n" + "#여름가디건 #마스크");
                    } else if (isRainy && !isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_1_d_r);
                        txtComment.setText("비가오고 저녁은 추워... 으으\n" + "#여름가디건 #우산");
                    } else {
                        imgModel.setImageResource(R.drawable.img_1_d);
                        txtComment.setText("낮에는 HOT, 저녁은 COOL한 날씨\n" + "가디건을 챙기자\n" + "#여름가디건 #바람막이");
                    }
                } else {
                    if (!isRainy && isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_1_u);
                        txtComment.setText("더운데 눈부셔!\n" + "#나시 #선글라스");
                    } else if (!isRainy && !isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_1_f);
                        txtComment.setText("더워! 근데 미세먼지뭐야...\n" + "#나시 #반팔 #마스크");
                    } else if (isRainy && !isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_1_r);
                        txtComment.setText("더운데 비까지... 찝찝해\n" + "#반바지 #우산 #우비");
                    } else if (!isRainy && isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_1_f_u);
                        txtComment.setText("덥고... 눈부셔... 미세먼지는 또 뭐야!\n" + "#나시 #선글라스 #마스크");
                    } else if (isRainy && isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_1_r_u);
                        txtComment.setText("눈은 부시고 비는 내리고 이거 맞아?\n" + "#반바지 #선글라스 #우산");
                    } else if (isRainy && !isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_1_f_r);
                        txtComment.setText("더운데 비도 오고 공기도 안좋아 불쾌해\n" + "#반바지 #우산 #마스크");
                    } else {
                        imgModel.setImageResource(R.drawable.img_1);
                        txtComment.setText("파격적인 날씨! 최대한 얇게 입자\n" + "#나시 #원피스 #기능성티");
                    }
                }
            } else if (currentTemp >= 28) {
                if (isLargeDiff) {
                    if (!isRainy && isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_2_d_u);
                        txtComment.setText("여름 아니야? 아직 아침저녁은 좀 춥고 자외선 너무 세!\n" + "#여름볼레로 #선크림");
                    } else if (!isRainy && !isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_2_d_f);
                        txtComment.setText("일교차 무슨 일? 공기도 안좋네...\n" + "#여름가디건 #마스크");
                    } else if (isRainy && !isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_2_d_r);
                        txtComment.setText("비와서 추운가?\n" + "#바람막이 #우산");
                    } else {
                        imgModel.setImageResource(R.drawable.img_2_d);
                        txtComment.setText("분명 여름인데 아직 조금 쌀쌀한가?\n" + "#여름가디건 #여름볼레로");
                    }
                } else {
                    if (!isRainy && isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_2_u);
                        txtComment.setText("덥고 눈도 못뜨겠어...\n" + "#반팔원피스 #선글라스");
                    } else if (!isRainy && !isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_2_f);
                        txtComment.setText("더운데 마스크도 써야돼?\n" + "#반팔 #마스크");
                    } else if (isRainy && !isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_2_r);
                        txtComment.setText("이제... 여름비인가? \n" + "#피케원피스 #우산 ");
                    } else if (!isRainy && isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_2_f_u);
                        txtComment.setText("자외선에 공기도 안좋아! 오늘 무슨일\n" + "내 얼굴 절대 지켜\n" + "#면바지 #선글라스 #마스크");
                    } else if (isRainy && isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_2_r_u);
                        txtComment.setText("비 + 자외선 = OMG\n" + "#반바지 #선글라스");
                    } else if (isRainy && !isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_2_f_r);
                        txtComment.setText("비는 내리고 공기는 안좋고 실내에만 있을까\n" + "#반팔셔츠 #우산 #마스크");
                    } else {
                        imgModel.setImageResource(R.drawable.img_2);
                        txtComment.setText("너무 더워... 이제 진짜 여름이다\n" + "#반팔 #크롭티");
                    }
                }
            } else if (currentTemp >= 23) {
                if (isLargeDiff) {
                    if (!isRainy && isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_3_d_u);
                        txtComment.setText("아침저녁으로 쌀쌀하고 피부의 적, 자외선이 심하니까\n" + "가디건 하나 챙길까?\n" + "#여름가디건 #선글라스");
                    } else if (!isRainy && !isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_3_d_f);
                        txtComment.setText("일교차 커서 추운데 하늘은 미세먼지때문에 뿌예...\n" + "#바람막이 #마스크");
                    } else if (isRainy && !isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_3_d_r);
                        txtComment.setText("비도 오고 일교차 크니까 아우터 하나 챙기자!\n" + "#바람막이 #우산");
                    } else {
                        imgModel.setImageResource(R.drawable.img_3_d);
                        txtComment.setText("초여름이라 아직 쌀쌀하네\n" + "#블레이저 #나시");
                    }
                } else {
                    if (!isRainy && isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_3_u);
                        txtComment.setText("피부의 적, 자외선!\n" + "#선크림 #반팔");
                    } else if (!isRainy && !isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_3_f);
                        txtComment.setText("미세먼지 싫어용\n" + "#셔츠 #마스크");
                    } else if (isRainy && !isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_3_r);
                        txtComment.setText("초여름에 내리는 비 운치있어!\n" + "#셔츠 #슬랙스 #우산");
                    } else if (!isRainy && isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_3_f_u);
                        txtComment.setText("눈부시고... 공기는 안좋고...\n" + "선글라스랑 마스크 해야겠다\n" + "#반팔 #청바지");
                    } else if (isRainy && isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_3_r_u);
                        txtComment.setText("비는 내리는데 자외선지수는 높네...\n" + "#슬랙스코디 #선크림 #우산");
                    } else if (isRainy && !isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_3_f_r);
                        txtComment.setText("비도오고 미세먼지도 심해서 날씨가 너무 안좋아\n" + "#반팔셔츠 #마스크 #우산");
                    } else {
                        imgModel.setImageResource(R.drawable.img_3);
                        txtComment.setText("초여름인가? 곧 더워질테니 여름옷 개시!\n" + "#반팔티 #셔츠");
                    }
                }
            } else if (currentTemp >= 20) {
                if (isLargeDiff) {
                    if (!isRainy && isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_4_d_u);
                        txtComment.setText("일교차도 자외선도 심하다 심해\n" + "#겉옷 #선크림 #선글라스");
                    } else if (!isRainy && !isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_4_d_f);
                        txtComment.setText("일교차 심하고 미세먼지도 있어 :(\n" + "#겉옷 #마스크");
                    } else if (isRainy && !isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_4_d_r);
                        txtComment.setText("오늘 좀 추울지도? 아직 따뜻하게\n" + "#겉옷 #우산");
                    } else {
                        imgModel.setImageResource(R.drawable.img_4_d);
                        txtComment.setText("따뜻해도 아침 저녁으론 쌀쌀해 \n" + "#얇은외투");
                    }
                } else {
                    if (!isRainy && isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_4_u);
                        txtComment.setText("덥지는 않은데 햇빛이 뜨거워!\n" + "#선크림 #선글라스");
                    } else if (!isRainy && !isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_4_f);
                        txtComment.setText("미먼 심하네 마스크 써야지 :(\n" + "#마스크");
                    } else if (isRainy && !isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_4_r);
                        txtComment.setText("비? 오히려 좋아!\n" + "#추적추적");
                    } else if (!isRainy && isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_4_f_u);
                        txtComment.setText("오늘은 소중한 내 몸 지켜\n" + "#선크림 #선글라스 #마스크");
                    } else if (isRainy && isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_4_r_u);
                        txtComment.setText("이상한날! 비도 오고 자외선 지수도 높아\n" + "#선크림 #선글라스 #우산");
                    } else if (isRainy && !isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_4_f_r);
                        txtComment.setText("오늘 우산 필수 비 맞기 금지!\n" + "#미세먼지 #비 #우산");
                    } else {
                        imgModel.setImageResource(R.drawable.img_4);
                        txtComment.setText("따뜻해! 나들이가기 딱 좋은 날씨 :) \n" + "#피크닉");
                    }
                }
            } else if (currentTemp >= 17) {
                if (isLargeDiff) {
                    if (!isRainy && isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_5_d_u);
                        txtComment.setText("일교차도 자외선도 심하다 심해\n" + "#겉옷 #선크림 #선글라스");
                    } else if (!isRainy && !isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_5_d_f);
                        txtComment.setText("일교차 심하고 미세먼지도 있어 :(\n" + "#겉옷 #마스크");
                    } else if (isRainy && !isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_5_d_r);
                        txtComment.setText("오늘 좀 추울지도? 아직 따뜻하게\n" + "#겉옷 #우산");
                    } else {
                        imgModel.setImageResource(R.drawable.img_5_d);
                        txtComment.setText("아침 저녁으로 쌀쌀할거야!\n" + "#자켓");
                    }
                } else {
                    if (!isRainy && isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_5_u);
                        txtComment.setText("자외선지수 높은 날... 노화방지!\n" + "#선크림 #선글라스");
                    } else if (!isRainy && !isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_5_f);
                        txtComment.setText("미세먼지로부터 나를 지켜요\n" + "#마스크");
                    } else if (isRainy && !isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_5_r);
                        txtComment.setText("비오니까 우산은 필수...\n" + "#맨투맨 #우산");
                    } else if (!isRainy && isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_5_f_u);
                        txtComment.setText("오늘은 소중한 내 몸 지켜\n" + "#선크림 #선글라스 #마스크");
                    } else if (isRainy && isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_5_r_u);
                        txtComment.setText("이상한날! 비도 오고 자외선 지수도 높아\n" + "#선크림 #선글라스 #우산");
                    } else if (isRainy && !isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_5_f_r);
                        txtComment.setText("오늘 우산 필수 비 맞기 금지!\n" + "#미세먼지 #비 #우산");
                    } else {
                        imgModel.setImageResource(R.drawable.img_5);
                        txtComment.setText("따뜻하기도 선선하기도 그저그런 날씨 :)\n" + "#단독 #맨투맨");
                    }
                }
            } else if (currentTemp >= 12) {
                if (isLargeDiff) {
                    if (!isRainy && isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_6_d_u);
                        txtComment.setText("일교차도 자외선도 심하다 심해\n" + "#겉옷 #선크림 #선글라스");
                    } else if (!isRainy && !isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_6_d_f);
                        txtComment.setText("일교차 심하고 미세먼지도 있어 :(\n" + "#겉옷 #마스크");
                    } else if (isRainy && !isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_6_d_r);
                        txtComment.setText("일교차에 비까지 감기 조심해\n" + "#자켓 #우산");
                    } else {
                        imgModel.setImageResource(R.drawable.img_6_d);
                        txtComment.setText("아침 저녁으로 추워 벌써 겨울 아니야?\n" + "#감기조심");
                    }
                } else {
                    if (!isRainy && isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_6_u);
                        txtComment.setText("이런 날씨가 제일 많이 탄대\n" + "#선크림 #선글라스");
                    } else if (!isRainy && !isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_6_f);
                        txtComment.setText("오늘 미세먼지 조심!\n" + "#마스크");
                    } else if (isRainy && !isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_6_r);
                        txtComment.setText("오들오들 비온다 :(\n" + "#자켓 #우산");
                    } else if (!isRainy && isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_6_f_u);
                        txtComment.setText("오늘은 소중한 내 몸 지켜\n" + "#선크림 #선글라스 #마스크");
                    } else if (isRainy && isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_6_r_u);
                        txtComment.setText("이상한날! 비도 오고 자외선 지수도 높아\n" + "#선크림 #선글라스 #우산");
                    } else if (isRainy && !isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_6_f_r);
                        txtComment.setText("오늘 우산 필수 비 맞기 금지!\n" + "#미세먼지 #비 #우산");
                    } else {
                        imgModel.setImageResource(R.drawable.img_6);
                        txtComment.setText("조금 쌀쌀하네~\n" + "#자켓 #외투필수");
                    }
                }
            } else if (currentTemp >= 9) {
                if (isLargeDiff) {
                    if (!isRainy && isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_7_d_u);
                        txtComment.setText("일교차가 심하고 자외선 지수가 높아 !\n" + "#모자 #선크림");
                    } else if (!isRainy && !isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_7_d_f);
                        txtComment.setText("일교차가 심하고 미세먼지도 심해~\n" + "#두툼하게 #마스크");
                    } else if (isRainy && !isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_7_d_r);
                        txtComment.setText("일교차가 심하고 비가 와! 감기 조심!\n" + "#우산 #겉옷");
                    } else {
                        imgModel.setImageResource(R.drawable.img_7_d);
                        txtComment.setText("아침 저녁으로 일교차가 심하니까 좀 껴입자!\n" + "#트렌치코트 #니트 #비니 #모자");
                    }
                } else {
                    if (!isRainy && isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_7_u);
                        txtComment.setText("쌀쌀한데 자외선 지수가 높아!\n" + "#선크림 #모자");
                    } else if (!isRainy && !isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_7_f);
                        txtComment.setText("쌀쌀하고 미세먼지가 심하니 마스크 필수!\n" + "#마스크");
                    } else if (isRainy && !isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_7_r);
                        txtComment.setText("쌀쌀하면서 비가 와~ 감기조심!\n" + "#우산 #트렌치코트");
                    } else if (!isRainy && isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_7_f_u);
                        txtComment.setText("자외선에 미세먼지까지...\n" + "#선크림 #마스크");
                    } else if (isRainy && isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_7_r_u);
                        txtComment.setText("비가 온다고 자외선 지수를 무시하면 안돼!\n" + "#선크림 꼬옥 바르자 #우산");
                    } else if (isRainy && !isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_7_f_r);
                        txtComment.setText("미세먼지가 심하고 비가 와~\n" + "#마스크 #우산");
                    } else {
                        imgModel.setImageResource(R.drawable.img_7);
                        txtComment.setText("날씨가 사알짝 춥고 쌀쌀해~\n" + "#트렌치코트 #니트 #청바지");
                    }
                }
            } else if (currentTemp >= 5) {
                if (isLargeDiff) {
                    if (!isRainy && isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_8_d_u);
                        txtComment.setText("일교차도 심하고, 자외선 지수도 높아!\n" + "#선크림 #모자 #장갑 #목도리");
                    } else if (!isRainy && !isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_8_d_f);
                        txtComment.setText("일교차가 심하고 미세먼지 농도가 높아!\n" + "#코트 #목도리 #장갑 #모자 #마스크");
                    } else if (isRainy && !isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_8_d_r);
                        txtComment.setText("일교차가 심하고 비가 와!\n" + "#우산 #목도리 #장갑");
                    } else {
                        imgModel.setImageResource(R.drawable.img_8_d);
                        txtComment.setText("아침 저녁으로는 찬바람이 많이 시려워~\n" + "모자랑 목도리로 체온을 보호해주는건 어때?\n" + "#코트 #목도리 #비니 #레깅스");
                    }
                } else {
                    if (!isRainy && isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_8_u);
                        txtComment.setText("춥다고 자외선이 없는 건 아니지!\n" + "#선크림 #코트 #레깅스");
                    } else if (!isRainy && !isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_8_f);
                        txtComment.setText("삐삐- 미세먼지 농도 높음! 마스크 챙기자!\n" + "#마스크");
                    } else if (isRainy && !isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_8_r);
                        txtComment.setText("비가 와서 더 추워~ 우산 챙겨!\n" + "#코트 #우산");
                    } else if (!isRainy && isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_8_f_u);
                        txtComment.setText("자외선 지수가 높고 미세먼지가 심해!\n" + "#선크림 #마스크");
                    } else if (isRainy && isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_8_r_u);
                        txtComment.setText("자외선 지수가 높고 비가 와!\n" + "#선크림 #우산");
                    } else if (isRainy && !isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_8_f_r);
                        txtComment.setText("미세먼지가 심하고 비가 와!\n" + "#마스크 #우산");
                    } else {
                        imgModel.setImageResource(R.drawable.img_8);
                        txtComment.setText("찬 바람이 부는 날씨~ 따뜻하게 입자!\n" + "#코트 #가죽자켓 #레깅스");
                    }
                }
            } else {
                if (isLargeDiff) {
                    if (!isRainy && isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_9_d_u);
                        txtComment.setText("일교차가 심하고 자외선이 높아~\n" + "#패딩 #선크림");
                    } else if (!isRainy && !isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_9_d_f);
                        txtComment.setText("일교차가 심하고 미세먼지 농도가 높아!\n" + "#목도리 #장갑 #모자 #마스크");
                    } else if (isRainy && !isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_9_d_r);
                        txtComment.setText("일교차가 심하고 비가 와!\n" + "#우산 #목도리 #장갑");
                    } else {
                        imgModel.setImageResource(R.drawable.img_9_d);
                        txtComment.setText("원래도 추운데... 아침 저녁에는 더~ 추워\n" + "#목도리 #비니 #히트텍 #롱패딩");
                    }
                } else {
                    if (!isRainy && isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_9_u);
                        txtComment.setText("오늘은 춥고 자외선이 높아!\n" + "#선크림 #춥다고해가없는건아니다...");
                    } else if (!isRainy && !isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_9_f);
                        txtComment.setText("미세먼지 조심! 마스크 챙기자!\n" + "#마스크");
                    } else if (isRainy && !isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_9_r);
                        txtComment.setText("추운 겨울 비가 내리니까 감기조심!\n" + "#패딩 #우산");
                    } else if (!isRainy && isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_9_f_u);
                        txtComment.setText("자외선 지수가 높고 미세먼지가 심해!\n" + "#선크림 #마스크");
                    } else if (isRainy && isHighUV && !isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_9_r_u);
                        txtComment.setText("자외선 지수가 높고 비가 와!\n" + "#선크림 #우산");
                    } else if (isRainy && !isHighUV && isHighFineDust) {
                        imgModel.setImageResource(R.drawable.img_9_f_r);
                        txtComment.setText("미세먼지가 심하고 비가 와!\n" + "#마스크 #우산");
                    } else {
                        imgModel.setImageResource(R.drawable.img_9);
                        txtComment.setText("이게 바로 겨울이지... 두껍게 껴입자!\n" + "#히트텍 #기모 #패딩");
                    }
                }
            }
            btnToInfo.setEnabled(true);
            btnToInfo.setText(R.string.more_info);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        // 뷰
        txtCurrentTemp = findViewById(R.id.textview_ctemp);
        txtRange = findViewById(R.id.textview_range);
        txtComment = findViewById(R.id.textview_comment);
        imgModel = findViewById(R.id.imageview_model);
        btnToInfo = findViewById(R.id.button_to_info);

        // Info Activity로 이동
        btnToInfo.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
            startActivity(intent);
        });

        renderUI();
    }

    @Override
    public void onBackPressed() {
        // 대기 시간 2초, 2초 안에 백 버튼이 2번 눌리면 앱 종료
        final int timeout = 2000;
        if (System.currentTimeMillis() > backPressedTime + timeout) {
            backPressedTime = System.currentTimeMillis();
            Toast.makeText(this, getResources().getString(R.string.back_twice), Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() <= backPressedTime + timeout) {
            finish();
        }
    }
}