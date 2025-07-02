package com.example.mysupportapp

import com.example.mysupportapp.models.ActualizarBD
import com.example.mysupportapp.models.ApiResponse
import com.example.mysupportapp.models.ComparativaRequest
import com.example.mysupportapp.models.ComparativaResponse
import com.example.mysupportapp.models.FolioRequest
import com.example.mysupportapp.models.Folios
import com.example.mysupportapp.models.LoginResponse
import com.example.mysupportapp.models.LogoutResponse
import com.example.mysupportapp.models.ONTCOBRE
import com.example.mysupportapp.models.Option
import com.example.mysupportapp.models.SessionResponse
import com.example.mysupportapp.models.TacResponse
import com.example.mysupportapp.models.materiales
import com.example.mysupportapp.models.pasos
import com.example.mysupportapp.models.requestpasos
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interfaz que define los métodos de comunicación con el backend
 * a través de Retrofit. Estos métodos están destinados a realizar
 * diversas operaciones relacionadas con la gestión de sesiones,
 * comparativas, autenticación y actualización de datos del técnico.
 */
interface ApiService {

    @GET("login-coordiapp/verificar-sesion")
    fun sessionCheck(): Call<SessionResponse>

    /**
     * Verifica la versión de la aplicación instalada, útil cuando se instala a través de un APK.
     *
     * @return Una llamada Retrofit que devuelve un objeto JSON con la versión de la app.
     */
    @GET("appVersion")
    fun checkVersion(): Call<JsonObject?>?

    /**
     * Inicia sesión en la aplicación utilizando el nombre de usuario proporcionado.
     *
     * @param usuarioApp Nombre del usuario que inicia sesión.
     * @return Una llamada Retrofit que devuelve la respuesta con los detalles del login.
     */
    @GET("login-coordiapp/iniciar-sesion/{username}")
    @Headers("Accept: application/json", "Content-Type: application/json")
    fun login(
        @Path("username") usuarioApp: String,
    ): Call<LoginResponse>

    /**
     * Cierra la sesión actual del usuario.
     *
     * @return Una llamada Retrofit que devuelve la respuesta de cierre de sesión.
     */
    @GET("login-coordiapp/cerrar-sesion")
    fun logout(): Call<LogoutResponse>

}
