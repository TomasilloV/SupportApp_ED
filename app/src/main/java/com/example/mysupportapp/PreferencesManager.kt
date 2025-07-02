package com.example.mysupportapp

import android.content.Context
import android.content.SharedPreferences

/**
 * Clase que gestiona las preferencias compartidas de la aplicación.
 * Permite guardar, recuperar y limpiar datos persistentes utilizando `SharedPreferences`.
 *
 * @param context Contexto de la aplicación necesario para inicializar las preferencias.
 */
class PreferencesManager(context: Context) {

    /** Instancia de SharedPreferences para almacenar los datos */
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    /**
     * Guarda un valor de tipo String en las preferencias compartidas.
     *
     * @param name La clave bajo la cual se guardará el valor.
     * @param key El valor a guardar.
     */
    fun saveString(name: String, key: String) {
        sharedPreferences.edit().putString(name, key).apply()
    }

    fun saveInt(name: String, key: Int)
    {
        sharedPreferences.edit().putInt(name,key).apply()
    }

    fun getInt(name: String): Int? {
        return sharedPreferences.getInt(name, 0)
    }

    /**
     * Recupera un valor de tipo String desde las preferencias compartidas.
     *
     * @param name La clave bajo la cual se encuentra el valor.
     * @return El valor asociado a la clave, o `null` si no existe.
     */
    fun getString(name: String): String? {
        return sharedPreferences.getString(name, null)
    }

    /**
     * Elimina todos los datos almacenados en las preferencias compartidas.
     */
    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }
}
