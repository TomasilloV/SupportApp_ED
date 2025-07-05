package com.example.mysupportapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.mysupportapp.models.LoginResponse
import com.example.mysupportapp.singleton.PreferencesHelper
import com.example.mysupportapp.utils.checkPermission
import com.example.mysupportapp.utils.setLoadingVisibility
import com.example.mysupportapp.utils.showToast
import com.example.mysupportapp.utils.hideKeyboardOnOutsideTouch
import com.example.mysupportapp.utils.startNewActivity
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.CookieStore
import java.util.concurrent.TimeUnit

/**
 * Actividad de inicio de sesión para la aplicación.
 * Permite al usuario autenticarse y acceder al menú principal de la aplicación.
 */
class Login : AppCompatActivity() {

    /** Gestor de preferencias compartidas para almacenar datos del usuario */
    private val preferencesManager = PreferencesHelper.getPreferencesManager()

    /** Código de solicitud para permisos */
    private val requestPERMISSION = 1

    /** Vista para mostrar el estado de carga */
    private lateinit var loadingLayout: FrameLayout

    /**
     * Método llamado al crear la actividad.
     * Configura permisos, la interfaz y los listeners para el inicio de sesión.
     *
     * @param savedInstanceState Estado anterior de la actividad si está disponible.
     */
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        pedirPermisoNotificaciones()

        val sharedPrefs = getSharedPreferences(Constantes.SP_FILE, Context.MODE_PRIVATE)
        val idGuardado = sharedPrefs.getInt(Constantes.SP_KEY_ID, 0)
        Log.d("idss","este es el id: "+idGuardado)

        // Configura el botón de login
        val loginButton = findViewById<Button>(R.id.loginButton)
        val userEditText = findViewById<EditText>(R.id.user)
        loadingLayout = findViewById(R.id.loading_layout)

        loginButton.setOnClickListener {
            val username = userEditText.text.toString()
            if (username.isEmpty()) {
                showToast("Por favor ingrese un nombre de usuario")
                return@setOnClickListener
            }

            // Inicia la llamada de login en segundo plano
            lifecycleScope.launch {
                loadingLayout.setLoadingVisibility(true)
                val apiService = createRetrofitService(this@Login)
                val call = apiService.login(username,idGuardado)
                Log.d("LoginDebug", "Intentando login con usuario: '$username'")
                Log.d("LoginDebug", "URL: ${call.request().url}")
                Log.d("LoginDebug", "Headers: ${call.request().headers}")

                call.enqueue(object : Callback<LoginResponse> {


                    /**
                     * Maneja la respuesta exitosa o con error de la llamada de inicio de sesión.
                     *
                     * @param call Instancia de la llamada Retrofit.
                     * @param response Respuesta HTTP recibida del servidor.
                     */
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        Log.d("LoginDebug", "Código HTTP: ${response.code()}")
                        Log.d("LoginDebug", "Es exitoso: ${response.isSuccessful}")
                        Log.d("LoginDebug", "Mensaje: ${response.message()}")
                        Log.d("LoginDebug", "Raw body: ${response.errorBody()?.string()}")
                        handleLoginResponse(response)
                    }

                    /**
                     * Maneja el fallo en la conexión durante la llamada de inicio de sesión.
                     *
                     * @param call Instancia de la llamada Retrofit.
                     * @param t Excepción que describe el fallo.
                     */
                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        loadingLayout.setLoadingVisibility(false)
                        Log.e("LoginDebug", "Fallo de conexión: ${t.localizedMessage}", t)
                        showToast("Error. Asegúrate de tener una conexión a internet estable")
                    }
                })
            }
        }
    }

    /**
     * Procesa la respuesta de inicio de sesión.
     * Si la autenticación es exitosa, guarda los datos del usuario y redirige al menú principal.
     *
     * @param response Respuesta recibida del servidor.
     */
    private fun handleLoginResponse(response: Response<LoginResponse>) {
        loadingLayout.setLoadingVisibility(false)
        Log.d("LoginDebugd", "Código HTTP: ${response.code()}")
        Log.d("LoginDebugd", "Es exitoso: ${response.isSuccessful}")
        Log.d("LoginDebugd", "Mensaje: ${response.message()}")
        Log.d("LoginDebugd", "Raw body: ${response.errorBody()?.string()}")
        if (response.isSuccessful) {
            val loginResponse = response.body()
            if (loginResponse?.mensaje == "Datos Correctos") {
                loginResponse.usuario.let { item ->
                    preferencesManager.saveString("id_tecnico", item.idTecnico.toString())
                    val tecnico = "${item.Nombre_T} ${item.Apellidos_T}"
                    preferencesManager.saveString("tecnico", tecnico)
                    startNewActivity(SoporteMenu::class.java)
                }
            } else {
                showToast("Usuario o datos incorrectos")
            }
        } else {
            showToast("Error en la respuesta del servidor")
        }
    }


    /**
     * Detecta eventos táctiles para ocultar el teclado al tocar fuera de los campos de texto.
     *
     * @param ev Evento de toque detectado.
     * @return `true` si el evento fue manejado correctamente.
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        hideKeyboardOnOutsideTouch(ev)
        return super.dispatchTouchEvent(ev)
    }

    private fun pedirPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf<String>(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_CODE
                )
            } else {
                Log.d("Permiso", "Ya tienes permiso pa' notificaciones")
            }
        } else {
            Log.d("Permiso", "No hace falta pedir permiso en esta versión de Android")
        }
    }

    // Opcional: manejar la respuesta del usuario
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permiso", "¡Permiso concedido!")
            } else {
                Log.d("Permiso", "Permiso denegado...")
            }
        }
    }

    companion object {
        // private AppBarConfiguration appBarConfiguration;
        private const val NOTIFICATION_PERMISSION_CODE = 1001

        private const val TAG = "MainActivity" // Para los logs
    }
}
