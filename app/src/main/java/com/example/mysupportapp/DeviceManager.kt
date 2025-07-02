package com.example.mysupportapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DeviceManager {

    private static final String TAG = "DeviceManager"; // Para los logs

    public static void postRegistrarDispositivoEnServidor(String token, Context context){

        // Validar contexto y token
        if (context == null) {
            Log.e(TAG, "El contexto es null");
            return; // Si el contexto es null, no continuamos
        }

        if (token == null || token.isEmpty()) {
            Log.e(TAG, "El token es null o está vacío");
            return; // Si el token es null o vacío, no continuamos
        }

        // Validamos SharedPreferences
        if (context.getSharedPreferences(Constantes.SP_FILE, 0) == null) {
            Log.e(TAG, "SharedPreferences no se pudieron cargar");
            return; // Si SharedPreferences es null, no continuamos
        }

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Configuracion.URL_SERVIDOR;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response == null) {
                            Log.e(TAG, "Respuesta del servidor es null");
                            return; // Si la respuesta es null, no procesamos nada
                        }

                        try {
                            JSONObject respObj = new JSONObject(response);

                            String code = respObj.getString("code");
                            String message = respObj.getString("message");
                            Integer id = respObj.getInt("id");

                            Log.d(TAG, "Respuesta del servidor: code=" + code + ", message=" + message + ", id=" + id);

                            if ("OK".equals(code)) {
                                // Validar que SharedPreferences esté disponible
                                if (context.getSharedPreferences(Constantes.SP_FILE, 0) != null) {
                                    context.getSharedPreferences(Constantes.SP_FILE, 0).edit()
                                            .putString(Constantes.SP_KEY_DEVICEID, token).commit();
                                    if (id != 0) {
                                        context.getSharedPreferences(Constantes.SP_FILE, 0)
                                                .edit().putInt(Constantes.SP_KEY_ID, id).commit();
                                    }
                                } else {
                                    Log.e(TAG, "Error al acceder a SharedPreferences para guardar datos.");
                                }
                            } else {
                                Log.w(TAG, "Código no OK: " + code);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error al parsear la respuesta JSON", e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error en la solicitud: " + error.getMessage());
                Toast.makeText(context, "Error registrando token en servidor: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                if (token != null && !token.isEmpty()) {
                    params.put("DEVICEID", token);
                } else {
                    Log.e(TAG, "Token es null o vacío en los parámetros");
                }

                // Validar SharedPreferences antes de acceder
                int id = context.getSharedPreferences(Constantes.SP_FILE, 0).getInt(Constantes.SP_KEY_ID, 0);
                if (id != 0) {
                    params.put("ID", String.valueOf(id));
                } else {
                    Log.w(TAG, "El ID de SharedPreferences es 0 o no está disponible");
                }

                return params;
            };
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
