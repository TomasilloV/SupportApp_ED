package com.example.mysupportapp

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import android.util.Log
import com.example.mysupportapp.models.SerializableCookie
import java.io.*
import java.util.*

/**
 * Clase personalizada para manejar cookies con OkHttp y almacenarlas de forma persistente
 * en SharedPreferences.
 *
 * @param context Contexto de la aplicación para acceder a SharedPreferences.
 */
class MyCookieJar(context: Context) : CookieJar {

    // SharedPreferences para persistencia
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("CookiePrefs", Context.MODE_PRIVATE)

    // Almacén en memoria de cookies
    private val cookieStore = mutableMapOf<String, MutableList<SerializableCookie>>()

    // Inicializa cargando las cookies desde SharedPreferences
    init {
        loadCookiesFromPreferences()
    }

    /**
     * Guarda las cookies de una respuesta HTTP en el almacén local y en SharedPreferences.
     *
     * @param url URL asociada a las cookies.
     * @param cookies Lista de cookies recibidas.
     */
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val key = "https://api.ed-intra.com/"

        val list = cookieStore.getOrPut(key) { mutableListOf() }

        cookies.forEach { cookie ->
            list.removeAll { it.name == cookie.name &&
                    it.domain == cookie.domain &&
                    it.path == cookie.path }

            list.add(
                SerializableCookie(
                    name      = cookie.name,
                    value     = cookie.value,
                    expiresAt = cookie.expiresAt,
                    domain    = cookie.domain,
                    path      = cookie.path,
                    secure    = cookie.secure,
                    httpOnly  = cookie.httpOnly
                )
            )
        }

        saveCookiesToPreferences()
    }

    /**
     * Carga cookies almacenadas para una solicitud HTTP.
     *
     * @param url URL para la cual se solicitan las cookies.
     * @return Lista de cookies asociadas a la URL.
     */
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val key = "https://api.ed-intra.com/"
        val serializableCookies = cookieStore[key]?.toList() ?: emptyList()

        Log.d("MyCookieJar", "Cargando cookies para $key: $serializableCookies")

        // Convierte las cookies serializables a objetos Cookie de OkHttp
        return serializableCookies.map {
            val cookieBuilder = Cookie.Builder()
                .name(it.name)
                .value(it.value)
                .expiresAt(it.expiresAt)
                .domain(it.domain)
                .path(it.path)

            if (it.secure) {
                cookieBuilder.secure()
            }

            if (it.httpOnly) {
                cookieBuilder.httpOnly()
            }

            cookieBuilder.build()
        }
    }

    /**
     * Guarda todas las cookies actuales del almacén en SharedPreferences.
     */
    private fun saveCookiesToPreferences() {
        val editor = sharedPreferences.edit()
        for ((key, cookies) in cookieStore) {
            val serializedCookies = cookies.map { serializeCookie(it) }
            editor.putStringSet(key, serializedCookies.toSet())
        }
        editor.apply()
    }

    fun clear() {
        cookieStore.clear()
        sharedPreferences.edit().clear().apply()
    }

    /**
     * Carga cookies persistidas desde SharedPreferences al almacén local.
     */
    private fun loadCookiesFromPreferences() {
        val allEntries = sharedPreferences.all
        for ((key, value) in allEntries) {
            if (value is Set<*>) {
                val cookies = value.mapNotNull { deserializeCookie(it as String) }
                cookieStore[key] = cookies.toMutableList()
            }
        }
    }

    /**
     * Serializa un objeto [SerializableCookie] a un String para almacenarlo.
     *
     * @param cookie Cookie serializable a convertir.
     * @return String base64 representando el objeto serializado.
     */
    private fun serializeCookie(cookie: SerializableCookie): String {
        return try {
            val byteArrayOutputStream = ByteArrayOutputStream()
            val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
            objectOutputStream.writeObject(cookie)
            objectOutputStream.close()
            Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())
        } catch (e: IOException) {
            Log.e("MyCookieJar", "Error serializando cookie", e)
            ""
        }
    }

    /**
     * Deserializa un String almacenado a un objeto [SerializableCookie].
     *
     * @param cookieString String base64 con la representación serializada de una cookie.
     * @return Objeto [SerializableCookie] o null si falla la deserialización.
     */
    private fun deserializeCookie(cookieString: String): SerializableCookie? {
        return try {
            val bytes = Base64.getDecoder().decode(cookieString)
            val byteArrayInputStream = ByteArrayInputStream(bytes)
            val objectInputStream = ObjectInputStream(byteArrayInputStream)
            objectInputStream.readObject() as SerializableCookie
        } catch (e: Exception) {
            Log.e("MyCookieJar", "Error deserializando cookie", e)
            null
        }
    }
}
