package my.edu.utem.ftmk.prayertimesapp;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PrayerTimeResponse {
    @SerializedName("data")
    private List<PrayerTimeData> data;

    public List<PrayerTimeData> getData() {
        return data;
    }

    public static class PrayerTimeData {
        @SerializedName("timings")
        private Timings timings;

        public Timings getTimings() {
            return timings;
        }
    }

    public static class Timings {
        @SerializedName("Fajr")
        private String fajr;
        @SerializedName("Dhuhr")
        private String dhuhr;
        @SerializedName("Asr")
        private String asr;
        @SerializedName("Maghrib")
        private String maghrib;
        @SerializedName("Isha")
        private String isha;

        public String getFajr() { return fajr; }
        public String getDhuhr() { return dhuhr; }
        public String getAsr() { return asr; }
        public String getMaghrib() { return maghrib; }
        public String getIsha() { return isha; }

        public void setFajr(String fajr) { this.fajr = fajr; }
        public void setDhuhr(String dhuhr) { this.dhuhr = dhuhr; }
        public void setAsr(String asr) { this.asr = asr; }
        public void setMaghrib(String maghrib) { this.maghrib = maghrib; }
        public void setIsha(String isha) { this.isha = isha; }
    }
}