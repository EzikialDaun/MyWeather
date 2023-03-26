package MyWeather;

public class ShortForecastResult {
    // 예보 날짜(yyyyMMdd)
    private String forecastDate;
    // 예보 시간(HHmm)
    private String forecastTime;
    // 기온(℃)
    private int tmp;
    // 풍향(deg)
    private int vec;
    // 풍속(m/s)
    private double wsd;
    // 하늘 코드
    private int sky;
    // 강수 코드
    private int pty;
    // 강수 확률(%)
    private int pop;
    // 1시간 강수량(mm)
    private int pcp;
    // 습도(%)
    private int reh;
    // 1시간 적설량(cm)
    private int sno;

    public ShortForecastResult(String forecastDate, String forecastTime, int tmp, int vec, double wsd, int sky, int pty, int pop, int pcp, int reh, int sno) {
        this.forecastDate = forecastDate;
        this.forecastTime = forecastTime;
        this.tmp = tmp;
        this.vec = vec;
        this.wsd = wsd;
        this.sky = sky;
        this.pty = pty;
        this.pop = pop;
        this.pcp = pcp;
        this.reh = reh;
        this.sno = sno;
    }

    public String getForecastDate() {
        return forecastDate;
    }

    public String getForecastTime() {
        return forecastTime;
    }

    public int getTmp() {
        return tmp;
    }

    public int getVec() {
        return vec;
    }

    public double getWsd() {
        return wsd;
    }

    public int getSky() {
        return sky;
    }

    public int getPty() {
        return pty;
    }

    public int getPop() {
        return pop;
    }

    public int getPcp() {
        return pcp;
    }

    public int getReh() {
        return reh;
    }

    public int getSno() {
        return sno;
    }
}