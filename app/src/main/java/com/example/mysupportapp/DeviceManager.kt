package com.example.mysupportapp

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

object DeviceManager {
    private const val TAG = "DeviceManager" // Para los logs

    fun postRegistrarDispositivoEnServidor(token: String?, context: Context?) {
        // Validar contexto y token

        if (context == null) {
            Log.e(TAG, "El contexto es null")
            return  // Si el contexto es null, no continuamos
        }

        if (token == null || token.isEmpty()) {
            Log.e(TAG, "El token es null o está vacío")
            return  // Si el token es null o vacío, no continuamos
        }

        // Validamos SharedPreferences
        if (context.getSharedPreferences(Constantes.SP_FILE, 0) == null) {
            Log.e(TAG, "SharedPreferences no se pudieron cargar")
            return  // Si SharedPreferences es null, no continuamos
        }

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(context)
        val url = Configuracion.URL_SERVIDOR

        // Request a string response from the provided URL.
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            object : Response.Listener<String?> {
                override fun onResponse(response: String?) {
                    if (response == null) {
                        Log.e(TAG, "Respuesta del servidor es null")
                        return  // Si la respuesta es null, no procesamos nada
                    }

                    try {
                        val respObj = JSONObject(response)

                        val code = respObj.getString("code")
                        val message = respObj.getString("message")
                        val id = respObj.getInt("id")

                        Log.d(
                            TAG,
                            "Respuesta del servidor: code=" + code + ", message=" + message + ", id=" + id
                        )

                        if ("OK" == code) {
                            // Validar que SharedPreferences esté disponible
                            if (context.getSharedPreferences(Constantes.SP_FILE, 0) != null) {
                                context.getSharedPreferences(Constantes.SP_FILE, 0).edit()
                                    .putString(Constantes.SP_KEY_DEVICEID, token).commit()
                                if (id != 0) {
                                    context.getSharedPreferences(Constantes.SP_FILE, 0)
                                        .edit().putInt(Constantes.SP_KEY_ID, id).commit()
                                }
                            } else {
                                Log.e(
                                    TAG,
                                    "Error al acceder a SharedPreferences para guardar datos."
                                )
                            }
                        } else {
                            Log.w(TAG, "Código no OK: " + code)
                        }
                    } catch (e: JSONException) {
                        Log.e(TAG, "Error al parsear la respuesta JSON", e)
                    }
                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError) {
                    Log.e(TAG, "Error en la solicitud: " + error.message)
                    Toast.makeText(
                        context,
                        "Error registrando token en servidor: " + error.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {
            override fun getParams(): MutableMap<String?, String?> {
                val params: MutableMap<String?, String?> = HashMap<String?, String?>()

                if (token != null && !token.isEmpty()) {
                    params.put("DEVICEID", token)
                } else {
                    Log.e(TAG, "Token es null o vacío en los parámetros")
                }

                // Validar SharedPreferences antes de acceder
                val id = context.getSharedPreferences(Constantes.SP_FILE, 0)
                    .getInt(Constantes.SP_KEY_ID, 0)
                if (id != 0) {
                    params.put("ID", id.toString())
                } else {
                    Log.w(TAG, "El ID de SharedPreferences es 0 o no está disponible")
                }

                return params
            }
        }

        // Add the request to the RequestQueue.
        queue.add<String?>(stringRequest)
    }
}
