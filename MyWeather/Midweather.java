package Midweather;

import java.time.LocalDateTime;


public class Midweather {
    public static LocalDateTime getRecentMidForecastTime() {
        LocalDateTime result = LocalDateTime.now().withMinute(0);
        int hour = result.getHour();

        if (hour < 6) {
            result = result.minusHours(24).withHour(18);
        }
        else if(hour <= 6 && hour < 18) {
            result = result.withHour(6);
        }else{
            result = result.withHour(18);
        }
        return result;

    }
}