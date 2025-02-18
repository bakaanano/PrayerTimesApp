package my.edu.utem.ftmk.prayertimesapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView prayerTimesTextView;
    private Spinner methodSpinner;
    private Button fetchButton;
    private EditText cityEditText;
    private EditText countryEditText;
    private TextView currentDateTextView;
    private final Map<String, Integer> methodMap = new HashMap<>();
    private int selectedMethodId = 20; // Default method ID
    private int currentYear;
    private int currentMonth;
    private int currentDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prayerTimesTextView = findViewById(R.id.prayerTimesTextView);
        methodSpinner = findViewById(R.id.methodSpinner);
        fetchButton = findViewById(R.id.fetchButton);
        cityEditText = findViewById(R.id.cityEditText);
        countryEditText = findViewById(R.id.countryEditText);
        currentDateTextView = findViewById(R.id.currentDateTextView);

        // Get current year, month, and day
        Calendar calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH) + 1; // Month is zero-based in Calendar
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        // Set the current date in the TextView
        currentDateTextView.setText(String.format("Current Date: %02d/%02d/%04d", currentDay, currentMonth, currentYear));

        fetchCalculationMethod(); // Fetch calculation methods from API
        fetchButton.setOnClickListener(v -> fetchPrayerTimes());
    }

    private void fetchCalculationMethod() {
        PrayerTimeApi api = ApiClient.getRetrofitInstance().create(PrayerTimeApi.class);
        Call<CalculationMethodResponse> call = api.getCalculationMethods();
        call.enqueue(new Callback<CalculationMethodResponse>() {
            @Override
            public void onResponse(Call<CalculationMethodResponse> call, Response<CalculationMethodResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    methodMap.clear();
                    List<String> methodNames = new ArrayList<>();
                    for (Map.Entry<String, CalculationMethodResponse.MethodDetail> entry : response.body().getData().entrySet()) {
                        CalculationMethodResponse.MethodDetail methodDetail = entry.getValue();
                        String methodName = methodDetail.getName();
                        int methodId = methodDetail.getId();
                        if (methodName != null) {
                            methodMap.put(methodName, methodId);
                            methodNames.add(methodName);
                        } else {
                            Log.w(TAG, "Skipping method with null name, ID: " + methodId);
                        }
                    }
                    // Log the method names and IDs for debugging
                    for (String methodName : methodNames) {
                        Log.d(TAG, "Method Name: " + methodName + ", ID: " + methodMap.get(methodName));
                    }
                    // Set up the Spinner
                    if (!methodNames.isEmpty()) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, methodNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        methodSpinner.setAdapter(adapter);
                        methodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position >= 0 && position < methodNames.size()) {
                                    String selectedMethod = methodNames.get(position);
                                    Integer methodId = methodMap.get(selectedMethod);
                                    if (methodId != null) {
                                        selectedMethodId = methodId;
                                        Log.d(TAG, "Selected method: " + selectedMethod + ", ID: " + selectedMethodId);
                                    } else {
                                        Log.e(TAG, "Selected method ID is null for method: " + selectedMethod);
                                        selectedMethodId = 20; // Fallback to default method ID
                                        prayerTimesTextView.setText("Selected method ID is null. Using default method.");
                                    }
                                } else {
                                    Log.e(TAG, "Invalid position: " + position);
                                    selectedMethodId = 20; // Fallback to default method ID
                                    prayerTimesTextView.setText("Invalid position. Using default method.");
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                Log.d(TAG, "Nothing selected in Spinner");
                            }
                        });
                    } else {
                        Log.e(TAG, "Method names list is empty.");
                        prayerTimesTextView.setText("Failed to load methods.");
                    }
                } else {
                    Log.e(TAG, "Failed to get methods: " + response.message());
                    prayerTimesTextView.setText("Failed to load methods.");
                }
            }

            @Override
            public void onFailure(Call<CalculationMethodResponse> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                prayerTimesTextView.setText("API call failed.");
            }
        });
    }

    private void fetchPrayerTimes() {
        String city = cityEditText.getText().toString().trim();
        String country = countryEditText.getText().toString().trim();

        if (city.isEmpty() || country.isEmpty()) {
            prayerTimesTextView.setText("Please fill in city and country.");
            return;
        }

        PrayerTimeApi api = ApiClient.getRetrofitInstance().create(PrayerTimeApi.class);
        Call<PrayerTimeResponse> call = api.getPrayerTimes(country, currentYear, currentMonth, city, selectedMethodId); // Pass selectedMethodId as an int
        call.enqueue(new Callback<PrayerTimeResponse>() {
            @Override
            public void onResponse(Call<PrayerTimeResponse> call, Response<PrayerTimeResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().getData().isEmpty()) {
                    PrayerTimeResponse.PrayerTimeData prayerData = response.body().getData().get(0);
                    PrayerTimeResponse.Timings timings = prayerData.getTimings();
                    if (timings != null) {
                        // Adjust prayer times if method ID is 17
                        if (selectedMethodId == 17) {
                            timings = adjustTimings(timings);
                        }

                        // Display prayer times
                        String prayerTimes = "Fajr: " + timings.getFajr() +
                                "\nDhuhr: " + timings.getDhuhr() +
                                "\nAsr: " + timings.getAsr() +
                                "\nMaghrib: " + timings.getMaghrib() +
                                "\nIsha: " + timings.getIsha();
                        prayerTimesTextView.setText(prayerTimes);
                    } else {
                        Log.e(TAG, "Timings are null");
                        prayerTimesTextView.setText("Timings data is null.");
                    }
                } else {
                    Log.e(TAG, "Failed to fetch prayer times!");
                    prayerTimesTextView.setText("Error fetching data.");
                }
            }

            @Override
            public void onFailure(Call<PrayerTimeResponse> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                prayerTimesTextView.setText("API call failed.");
            }
        });
    }

    private PrayerTimeResponse.Timings adjustTimings(PrayerTimeResponse.Timings timings) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            Date fajrDate = sdf.parse(timings.getFajr());
            Date dhuhrDate = sdf.parse(timings.getDhuhr());
            Date asrDate = sdf.parse(timings.getAsr());
            Date maghribDate = sdf.parse(timings.getMaghrib());
            Date ishaDate = sdf.parse(timings.getIsha());

            fajrDate = addMinutes(fajrDate, 11);
            dhuhrDate = addMinutes(dhuhrDate, 4);
            asrDate = addMinutes(asrDate, 3);
            maghribDate = addMinutes(maghribDate, 2);
            ishaDate = addMinutes(ishaDate, 2);

            timings.setFajr(sdf.format(fajrDate));
            timings.setDhuhr(sdf.format(dhuhrDate));
            timings.setAsr(sdf.format(asrDate));
            timings.setMaghrib(sdf.format(maghribDate));
            timings.setIsha(sdf.format(ishaDate));
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing prayer times: " + e.getMessage());
        }
        return timings;
    }

    private Date addMinutes(Date date, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }
}