package my.edu.utem.ftmk.prayertimesapp;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class CalculationMethodResponse {
    @SerializedName("data")
    private Map<String, MethodDetail> data;

    public Map<String, MethodDetail> getData() {
        return data;
    }

    public static class MethodDetail {

        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
