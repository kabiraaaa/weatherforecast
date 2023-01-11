package com.example.weatherforcast

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private val loc_rq = 101
    var fusedLocationClient: FusedLocationProviderClient? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        permissionsCheck()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun permissionsCheck() {
        val permission = ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
            getLocation()
        }
        else{
            getLocation()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION),
            loc_rq)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient?.lastLocation?.addOnCompleteListener(this) { task ->
            var location: Location? = task.result
            val lat = location?.latitude.toString()
            val long = location?.longitude.toString()
            weatherUpdate(lat, long)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun weatherUpdate(latt: String, longg: String) {
        try {
            val apikey="37ef7830eba8d096386d610666ed8a05"
            val queue = Volley.newRequestQueue(this)
            val url ="https://api.openweathermap.org/data/2.5/weather?lat=${latt}&lon=${longg}&appid=${apikey}&units=metric"
            val jsonRequest = JsonObjectRequest(
                Request.Method.GET, url,null,
                { response ->
                    setValues(response)
                },
                {
                    Toast.makeText(this,"ERROR",Toast.LENGTH_LONG).show() })
            queue.add(jsonRequest)
        }
        catch(e:Exception){
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show()
        }
    }
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setValues(response: JSONObject){
        city.text=response.getString("name")

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        val current = LocalDateTime.now().format(formatter)
        updatedat.text = "Updated at ${current}"

        condition.text=response.getJSONArray("weather").getJSONObject(0).getString("main")

        var tempr=response.getJSONObject("main").getString("temp")
        temp.text= tempr
        var mintemp=response.getJSONObject("main").getString("temp_min")
        mintempans.text=mintemp+"°C"
        var maxtemp=response.getJSONObject("main").getString("temp_max")
        maxtempans.text=maxtemp+"°C"

        pressureans.text=response.getJSONObject("main").getString("pressure")+" hPa"
        humidityans.text=response.getJSONObject("main").getString("humidity")+"%"
        pertempans.text=response.getJSONObject("main").getString("feels_like")+"°C"
        windans.text=response.getJSONObject("wind").getString("speed")
        visibilityans.text=response.getString("visibility")+"%"
        }
}