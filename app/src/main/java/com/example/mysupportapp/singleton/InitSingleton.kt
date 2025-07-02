package com.example.mysupportapp.singleton

import android.app.Application
import android.content.Context
import com.example.mysupportapp.createRetrofitService
import com.example.mysupportapp.ApiService
import com.example.mysupportapp.PreferencesManager

/**
 * Clase principal de la aplicación que se ejecuta al iniciar.
 * Se utiliza para inicializar los objetos singleton [ApiServiceHelper] y [PreferencesHelper].
 */
class InitSingleton : Application() {
    override fun onCreate() {
        super.onCreate()
        ApiServiceHelper.init(this) // Inicializa el ApiServiceHelper
        PreferencesHelper.init(this) // Inicializa el PreferencesHelper
    }
}

/**
 * Objeto singleton para gestionar las preferencias de usuario.
 */
object PreferencesHelper {

    private lateinit var preferencesManager: PreferencesManager

    /**
     * Inicializa el [PreferencesManager] con el contexto de la aplicación.
     *
     * @param context Contexto de la aplicación.
     */
    fun init(context: Context) {
        preferencesManager = PreferencesManager(context.applicationContext) // Instancia PreferencesManager
    }

    /**
     * Obtiene la instancia de [PreferencesManager].
     *
     * @return Instancia de [PreferencesManager].
     * @throws IllegalStateException Si no se ha inicializado [PreferencesHelper].
     */
    fun getPreferencesManager(): PreferencesManager {
        if (!PreferencesHelper::preferencesManager.isInitialized) { // Verifica si está inicializado
            throw IllegalStateException("PreferencesHelper no ha sido inicializado. Llama a init() primero.")
        }
        return preferencesManager
    }
}

/**
 * Objeto singleton para gestionar el acceso a la API mediante Retrofit.
 */
object ApiServiceHelper {

    private lateinit var apiService: ApiService

    /**
     * Inicializa el [ApiService] utilizando Retrofit.
     *
     * @param context Contexto de la aplicación.
     */
    fun init(context: Context) {
        apiService = createRetrofitService(context) // Crea una instancia de ApiService usando Retrofit
    }

    /**
     * Obtiene la instancia de [ApiService].
     *
     * @return Instancia de [ApiService].
     * @throws IllegalStateException Si no se ha inicializado [ApiServiceHelper].
     */
    fun getApiService(): ApiService {
        if (!::apiService.isInitialized) { // Verifica si está inicializado
            throw IllegalStateException("ApiServiceHelper no ha sido inicializado. Llama a init() primero.")
        }
        return apiService
    }
}
