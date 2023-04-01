import java.time.LocalDateTime;
import java.util.Scanner;

public class shortTime {
    /**
     * 최근 단기 예보 발표 시각을 리턴하는 모듈 개발
     * 단기예보 : 매일 2시 부터 3시간 간격으로 8회 발표(2, 5, 8 ... 23)
     **/

    public static void main(String[] args) {
        // 단기예보 시간값
        int[] st_term = {2,5,8,11,14,17,20,23};

        LocalDateTime now = LocalDateTime.now();
        System.out.println("현재시간 : " + now);

        // 현재 시간의 "시간"을 타켓
        int target = now.getHour();

        if(target == 00 || target == 01) {
            System.out.println(now.minusDays(1).withHour(23).withMinute(0));
        }
        else {
            // 타켓의 값이 배열 값과 같거나, 배열 값-2, 배열 값 -1 의 값과 같으면
            // 배열 값이 "시간"으로 이루어진 LocalDateTime 출력
            for (int i = 0; i < st_term.length; i++) {
                if (st_term[i] - 2 == target - 2 || st_term[i] - 1 == target - 2 || st_term[i] == target - 2) {
                    System.out.println("최근 단기 예보 시간 값 : " + st_term[i]);
                    now = now.withHour(st_term[i]).withMinute(0);
                    System.out.println("최근 단기 예보 시간 LocalDateTime으로 반환 : " + now);
                }
            }
        }

        // ------------------------------------------------------------

        System.out.println("여기서부터는 테스트");
        Scanner sc = new Scanner(System.in);
        System.out.print("현재 시간을 입력하세요 : ");
        int targetTest = sc.nextInt();
        if(targetTest == 00 || targetTest == 01) {
            System.out.println(now.minusDays(1).withHour(23).withMinute(0));
        }
        else {
            // 타켓의 값이 배열 값과 같거나, 배열 값-2, 배열 값 -1 의 값과 같으면
            // 배열 값이 "시간"으로 이루어진 LocalDateTime 출력
            for (int i = 0; i < st_term.length; i++) {
                if (st_term[i] - 2 == targetTest - 2 || st_term[i] - 1 == targetTest - 2 || st_term[i] == targetTest - 2) {
                    System.out.println("최근 단기 예보 시간 값 : " + st_term[i]);
                    now = now.withHour(st_term[i]).withMinute(0);
                    System.out.println("최근 단기 예보 시간 LocalDateTime으로 반환 : " + now);
                }
            }
        }
    }
}