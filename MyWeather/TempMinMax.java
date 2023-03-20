package MyWeather;

public class TempMinMax {
    private int maxAm;
    private int minAm;
    private int maxPm;
    private int minPm;

    public TempMinMax(int maxAm, int minAm, int maxPm, int minPm) {
        this.maxAm = maxAm;
        this.minAm = minAm;
        this.maxPm = maxPm;
        this.minPm = minPm;
    }

    public int getMaxAm() {
        return maxAm;
    }

    public int getMinAm() {
        return minAm;
    }

    public int getMaxPm() {
        return maxPm;
    }

    public int getMinPm() {
        return minPm;
    }
}
