package my.edu.utem.ftmk.prayertimesapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PrayerTimeApi {
    @GET("methods")
    Call<CalculationMethodResponse> getCalculationMethods();

    @GET("calendarByCity")
    Call<PrayerTimeResponse> getPrayerTimes(
            @Query("country") String country,
            @Query("year") int year,
            @Query("month") int month,
            @Query("city") String city,
            @Query("method") int method // Change to int
    );
}