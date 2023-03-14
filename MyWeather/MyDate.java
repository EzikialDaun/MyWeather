package MyWeather;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyDate {
    public static String getSimpleDate() {
        String result = new SimpleDateFormat("yyyyMMdd").format(new Date());
        return result;
    }

    public static String getSimpleDate(int index) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, index);
        String result = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
        return result;
    }

    public static String getSimpleTime() {
        String result = new SimpleDateFormat("hhmm").format(new Date());
        return result;
    }
}
