import java.time.LocalDateTime;
import java.util.Scanner;

public class shortTime {
    /**
     * 최근 단기 예보 발표 시각을 리턴하는 모듈 개발
     * 단기예보 : 매일 2시 부터 3시간 간격으로 8회 발표(2, 5, 8 ... 23)
     **/

    public static LocalDateTime getRecentShortForecastTime() {
        // 단기예보 시간값
        int[] st_term = {2, 5, 8, 11, 14, 17, 20, 23};
        LocalDateTime result = LocalDateTime.now();
        int target = result.getHour();

        //00시와 01시의 경우 전날 23시 값 리턴
        if (target == 00 || target == 01) {
            result = result.minusDays(1).withHour(23).withMinute(0);
        } else {
            // 타켓의 값이 단기예보 시간값과 같거나, 단기예보 시간값-2, 단기예보 시간값-1과 같으면 리턴
            for (int i = 0; i < st_term.length; i++) {
                if (st_term[i] - 2 == target - 2 || st_term[i] - 1 == target - 2 || st_term[i] == target - 2) {
                    result = result.withHour(st_term[i]).withMinute(0);
                }
            }
        }
        return result;
    }
}