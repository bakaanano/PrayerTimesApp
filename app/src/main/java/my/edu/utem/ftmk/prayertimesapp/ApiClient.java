package my.edu.utem.ftmk.prayertimesapp;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://api.aladhan.com/v1/";
    private static Retrofit retrofit;

    // Singleton pattern to ensure only one Retrofit instance
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            // Add a logging interceptor for debugging
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Build OkHttpClient with the logging interceptor
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            // Build Retrofit instance
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client) // Attach the OkHttpClient
                    .addConverterFactory(GsonConverterFactory.create()) // Use Gson for JSON parsing
                    .build();
        }
        return retrofit;
    }
}
