package com.example.weatherapplication

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import com.example.weatherapplication.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Tag
import java.sql.Timestamp
import java.util.Date
import java.util.Locale

//9619975dc8cc37f22951c03874de5858
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val searchView = findViewById<SearchView>(R.id.search_view)
        searchView.queryHint = "Search for a City"
        fetchWeatherData("Delhi")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })

    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/").build()
            .create(ApiInterface::class.java)

        val response =
            retrofit.getWeatherData(cityName, "9619975dc8cc37f22951c03874de5858", "metric")
        response.enqueue(object : Callback<weatherapplication> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<weatherapplication>,
                response: Response<weatherapplication>
            ) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunrise = responseBody.sys.sunrise.toLong()
                    val sunset = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    binding.temp.text = "$temperature °C"
                    binding.humidity.text = "$humidity %"
                    binding.weather.text = condition
                    binding.maxTemp.text = " Max Temp : $maxTemp °C"
                    binding.minTemp.text = "Min Temp: $minTemp °C"
                    binding.wind.text = "$windSpeed m/s"
                    binding.sunrise.text = "${time(sunrise)}"
                    binding.sunset.text = "${time(sunset)}"
                    binding.sea.text = "$seaLevel hPa"
                    binding.conditions.text = condition
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()
                    binding.cityName.text = "$cityName"

                    changeImages(condition)
                }
            }

            override fun onFailure(call: Call<weatherapplication>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun changeImages(condition: String) {
        when(condition) {
            "Clouds", "OverCast", "Mist", "Foggy","Haze" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Sunny", "Clear Sky", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "Light Rain", "Drizzle", "Showers", "Heavy Rain", "Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

        }
        binding.lottieAnimationView.playAnimation()
    }

    fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }

    fun time (timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))
    }

    fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
}