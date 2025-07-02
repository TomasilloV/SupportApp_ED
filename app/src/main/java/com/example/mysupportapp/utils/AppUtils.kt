package com.example.mysupportapp.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.mysupportapp.ApiService
import com.example.mysupportapp.Login
import com.example.mysupportapp.PreferencesManager
import com.example.mysupportapp.models.SessionResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Obtiene una instancia de [PreferencesManager].
 *
 * @param context Contexto de la aplicación.
 * @return Una instancia de PreferencesManager.
 */
fun getPreferencesManager(context: Context): PreferencesManager {
    return PreferencesManager(context.applicationContext)
}

/**
 * Cambia la visibilidad de un [FrameLayout].
 *
 * @param visible `true` para hacer visible, `false` para ocultar.
 */
fun FrameLayout.setLoadingVisibility(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

/**
 * Muestra un mensaje tipo Toast.
 *
 * @param message El mensaje a mostrar.
 */
fun Context.showToast(message: String) {
    Toast.makeText(this.applicationContext, message, Toast.LENGTH_SHORT).show()
}

/**
 * Oculta el teclado en pantalla.
 *
 * @param view La vista actual.
 */
fun Context.hideKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

/**
 * Oculta el teclado al tocar fuera de un EditText.
 *
 * @param event El evento de toque en pantalla.
 */
fun Activity.hideKeyboardOnOutsideTouch(event: MotionEvent) {
    val view = currentFocus
    if (view is View) {
        val outRect = android.graphics.Rect()
        view.getGlobalVisibleRect(outRect)
        if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
            view.clearFocus()
            hideKeyboard(view)
        }
    }
}

/**
 * Verifica si los permisos especificados están otorgados y, de no estarlo, los solicita.
 *
 * @param permissions Lista de permisos a verificar.
 * @param requestCode Código de solicitud para los permisos.
 */
fun Activity.checkPermission(
    permissions: List<String>,
    requestCode: Int
) {
    val permissionsToRequest = permissions.filter {
        ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
    }

    if (permissionsToRequest.isNotEmpty()) {
        ActivityCompat.requestPermissions(
            this,
            permissionsToRequest.toTypedArray(),
            requestCode
        )
    }
}

/**
 * Inicia una nueva actividad y finaliza la actividad actual.
 *
 * @param T Tipo de actividad de destino.
 * @param target Clase de la actividad a iniciar.
 */
fun <T> Context.startNewActivity(target: Class<T>) {
    val intent = Intent(this, target).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
    }
    startActivity(intent)
    if (this is Activity) {
        this.finish()
    }
}

/**
 * Inicia una nueva actividad desde un fragmento y finaliza la actividad actual.
 *
 * @param T Tipo de actividad de destino.
 * @param target Clase de la actividad a iniciar.
 */
fun <T> Fragment.startNewActivity(target: Class<T>) {
    val intent = Intent(requireContext(), target).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
    }
    startActivity(intent)
    activity?.finish()
}

/**
 * Revisa si la sesión del usuario es válida usando el [ApiService].
 *
 * @param T Clase de la actividad a iniciar en caso de éxito.
 * @param apiService Instancia de ApiService para verificar la sesión.
 * @param context Contexto actual.
 * @param successActivity Clase de la actividad a iniciar si la sesión es válida.
 */
fun <T> checkSession(apiService: ApiService, context: Context, successActivity: Class<T>?) {
    val call = apiService.sessionCheck()
    call.enqueue(object : Callback<SessionResponse> {
        override fun onResponse(
            call: Call<SessionResponse>,
            response: Response<SessionResponse>
        ) {
            if (response.isSuccessful) {
                val respuesta = response.body()

                if (respuesta != null && respuesta.mensaje == "Sesión válida") {
                    if (successActivity != null) {
                        context.startNewActivity(successActivity)
                    }
                } else {
                    context.startNewActivity(Login::class.java)
                    context.showToast("Inicia sesión para continuar")
                }
            } else {
                Log.d("SesionDebug", "Código HTTP: ${response.code()}")
                Log.d("SesionDebug", "Es exitoso: ${response.isSuccessful}")
                Log.d("SesionDebug", "Mensaje: ${response.message()}")
                Log.d("SesionDebug", "Raw body: ${response.errorBody()?.string()}")
                context.startNewActivity(Login::class.java)
            }
        }

        override fun onFailure(call: Call<SessionResponse>, t: Throwable) {
            context.startNewActivity(Login::class.java)
            context.showToast("Error, intenta de nuevo en unos momentos")
        }
    })
}
