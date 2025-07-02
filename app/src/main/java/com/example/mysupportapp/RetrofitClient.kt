package com.example.mysupportapp

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Crea e inicializa una instancia de Retrofit para la comunicación con una API.
 *
 * @param context Contexto necesario para gestionar cookies.
 * @return Una implementación de la interfaz [ApiService] lista para usar.
 */
fun createRetrofitService(context: Context): ApiService {
    val client = createCustomOkHttpClient(context)

    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.ed-intra.com/") // URL base de la API
        .client(client) // Cliente personalizado
        .addConverterFactory(GsonConverterFactory.create()) // Conversor para JSON
        .build()

    return retrofit.create(ApiService::class.java)
}

/**
 * Configura un cliente OkHttp personalizado con tiempos de espera,
 * manejo de cookies y registro de tráfico HTTP.
 *
 * @param context Contexto necesario para inicializar el sistema de manejo de cookies.
 * @return Una instancia configurada de [OkHttpClient].
 */
private fun createCustomOkHttpClient(context: Context): OkHttpClient {
    return OkHttpClient.Builder()
        .connectTimeout(120, TimeUnit.SECONDS) // Tiempo de espera para la conexión
        .readTimeout(120, TimeUnit.SECONDS) // Tiempo de espera para lectura
        .writeTimeout(120, TimeUnit.SECONDS) // Tiempo de espera para escritura
        .cookieJar(MyCookieJar(context)) // Sistema personalizado de manejo de cookies
        .addInterceptor(loggingInterceptor()) // Interceptor para logs HTTP
        .build()
}

/**
 * Configura un interceptor para registrar las solicitudes y respuestas HTTP.
 *
 * @return Una instancia de [HttpLoggingInterceptor] con el nivel de log configurado en BODY.
 */
private fun loggingInterceptor(): Interceptor {
    return HttpLoggingInterceptor { message ->
        Log.d("HTTP", message) // Log de cada mensaje HTTP
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY // Nivel de log detallado
    }
}
