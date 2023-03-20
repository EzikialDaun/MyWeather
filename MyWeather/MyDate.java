package MyWeather;

import java.time.LocalDateTime;

public class MyDate {
    public static LocalDateTime getRecentMicroForecastTime() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();
        LocalDateTime newDateTime = LocalDateTime.now().withMinute(30);
        if (minute < 30) {
            if (hour == 0) {
                newDateTime = newDateTime.minusDays(1); // 하루 전 예보 발표 날짜로 변경
                newDateTime = newDateTime.withHour(23); // 23시로 설정
            } else {
                newDateTime = newDateTime.withHour(hour - 1);
            }
        }
        return newDateTime;
    }

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
        if (hour < 2 || hour == 23) { // 02시 이전 예보 발표 시간일 경우
            result = result.minusDays(1).withHour(23); // 하루 전 예보 발표 날짜로 변경
        } else {
            int newHour = (((hour + 1) / 3) * 3) - 1;
            result = result.withHour(newHour);
        }
        return result;
    }

    public static LocalDateTime getYesterdayShortForecastTime() {
        LocalDateTime newDateTime = LocalDateTime.now().minusDays(1).withHour(23).withMinute(0);
        return newDateTime;
    }

    public static LocalDateTime getRecentMidForecastTime() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        LocalDateTime newDateTime = LocalDateTime.now().withMinute(0);
        if (hour < 6) {
            newDateTime = newDateTime.minusDays(1).withHour(18);
        } else if (hour < 18) {
            newDateTime = newDateTime.withHour(6);
        } else {
            newDateTime = newDateTime.withHour(18);
        }
        return newDateTime;
    }
}
