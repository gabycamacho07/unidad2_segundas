package com.example.dmi_clima_20


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

enum class ProviderType {
    BASIC,
    GOOGLE,
    FACEBOOK
}

class HomeActivity : AppCompatActivity() {
    private lateinit var emailTextView: TextView
    private lateinit var providerTextView: TextView
    private lateinit var logOutButton: Button
    private lateinit var editTextCity: EditText
    private lateinit var buttonGetWeather: Button
    private lateinit var textViewTemperature: TextView
    private lateinit var textViewDescription: TextView

    //private val apiKey = "004713410aab1233b5c5e609fe50a1eb"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //val apiKey = "004713410aab1233b5c5e609fe50a1eb"
        editTextCity = findViewById(R.id.editTextCity)
        buttonGetWeather = findViewById(R.id.buttonGetWeather)
        textViewTemperature = findViewById(R.id.textViewTemperature)
        textViewDescription = findViewById(R.id.textViewDescription)

        buttonGetWeather.setOnClickListener {
            val cityName = editTextCity.text.toString().trim()
            if (cityName.isNotEmpty()) {
                getWeather(cityName)
            }
        }

        // Inicializar Vistas
        emailTextView = findViewById(R.id.emailTextView)
        providerTextView = findViewById(R.id.providerTextView)
        logOutButton = findViewById(R.id.logOutButton)

        // Setup
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        setup(email ?: "", provider ?: "")

        // Guardado de datos
        val prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()


    }

    private fun getWeather(cityName: String) {
        val apiKey = "004713410aab1233b5c5e609fe50a1eb"
        val call = WeatherService.weatherAPI.getWeather(cityName, apiKey)
        call.enqueue(object : Callback<Weather> {
            override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                if (response.isSuccessful) {
                    val weather = response.body()
                    weather?.let {
                        val temperature = it.main.temp
                        val description = it.weather.first().description

                        textViewTemperature.text = "Temperatura: $temperature °C"
                        textViewDescription.text = "Descripción: $description"
                    }
                } else {
                    // Manejar error de la solicitud (por ejemplo, ciudad no encontrada)
                    val errorMessage = "Error: ${response.code()} ${response.message()}"
                    textViewTemperature.text = errorMessage
                    textViewDescription.text = ""
                }
            }

            override fun onFailure(call: Call<Weather>, t: Throwable) {
                // Manejar fallo de la solicitud
                textViewTemperature.text = "Error. No sé pudieron obtener datos meteorológicos"
                textViewDescription.text = ""
            }
        })
    }





    private fun setup(email: String, provider: String) {
        title = "Inicio"
        emailTextView.text = email
        providerTextView.text = provider

        logOutButton.setOnClickListener {

            //Borrado de datos
            val prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            if (provider == ProviderType.FACEBOOK.name) {
                LoginManager.getInstance().logOut()
            }

            FirebaseAuth.getInstance().signOut()

            finish()
        }
    }


}