package MyWeather;

import java.time.LocalDateTime;

public class DateManager {
  // 최근 초단기예보 발표시각 구하기
  public static LocalDateTime getRecentMicroForecastTime() {
    return getMicroForecastTime(0);
  }

  // 직전 초단기예보 발표시각 구하기
  public static LocalDateTime getLastMicroForecastTime() {
    return getMicroForecastTime(-1);
  }

  // 초단기예보 발표시각 구하기
  public static LocalDateTime getMicroForecastTime(int hourIndex) {
    LocalDateTime result = LocalDateTime.now().plusHours(hourIndex);
    int hour = result.getHour();
    int minute = result.getMinute();
    final int REF_MINUTE = 30;
    if (minute >= REF_MINUTE) {
      result = result.withMinute(REF_MINUTE);
    } else {
      if (hour == 0) {
        result = result.minusDays(1).withHour(23).withMinute(REF_MINUTE);
      } else {
        result = result.minusHours(1).withMinute(REF_MINUTE);
      }
    }
    return result;
  }

  // 최근 단기예보 발표시각 구하기
  public static LocalDateTime getRecentShortForecastTime() {
    LocalDateTime result = LocalDateTime.now().withMinute(0);
    // 예보 발표 시간 계산
    // 23 0 1   -> 2300
    // 2 3 4    -> 0200
    // 5 6 7    -> 0500
    // 8 9 10   -> 0800
    // 11 12 13 -> 1100
    // 14 15 16 -> 1400
    // 17 18 19 -> 1700
    // 20 21 22 -> 2000
    int hour = result.getHour();
    if (hour < 2) { // 02시 이전 예보 발표 시간일 경우
      result = result.minusDays(1).withHour(23); // 하루 전 예보 발표 날짜로 변경
    } else {
      int newHour = (((hour + 1) / 3) * 3) - 1;
      result = result.withHour(newHour);
    }
    return result;
  }

  // 어제의 마지막 단기예보 발표시각 구하기
  public static LocalDateTime getYesterdayShortForecastTime() {
    return LocalDateTime.now().minusDays(1).withHour(23).withMinute(0);
  }

  // 최근 중기예보 발표시각 구하기
  public static LocalDateTime getRecentMidForecastTime() {
    LocalDateTime result = LocalDateTime.now().withMinute(0);
    int hour = result.getHour();
    if (hour < 6) {
      result = result.minusDays(1).withHour(18);
    } else if (hour < 18) {
      result = result.withHour(6);
    } else {
      result = result.withHour(18);
    }
    return result;
  }
}
