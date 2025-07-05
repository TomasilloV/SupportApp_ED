package com.example.mysupportapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registrarDispositivo()
        startActivity(Intent(this, Login::class.java))

        /*var fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SoporteMenu.class);
                startActivity(intent);
            }
        });*/
    }

    private fun registrarDispositivo() {
        Log.d(TAG, "Iniciando registro de dispositivo")

        // Obtener el token de Firebase
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(object : OnCompleteListener<String?> {
                override fun onComplete(task: Task<String?>) {
                    if (task == null) {
                        Log.e(TAG, "El task de Firebase es null")
                        return  // Si el task es null, no continuamos
                    }

                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fallo al obtener el token FCM", task.getException())
                        return
                    }

                    val token = task.getResult()
                    Log.d(TAG, "Token recibido: " + token)

                    // Validar que el token no sea null
                    if (token == null || token.isEmpty()) {
                        Log.e(TAG, "El token es null o vacío")
                        Toast.makeText(
                            this@MainActivity,
                            "Error: Token FCM vacío",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    // Obtener token guardado de SharedPreferences
                    val tokenGuardado = getSharedPreferences(Constantes.SP_FILE, 0)
                        .getString(Constantes.SP_KEY_DEVICEID, null)

                    if (tokenGuardado == null) {
                        Log.d(TAG, "Token guardado es null")
                    } else {
                        Log.d(TAG, "Token guardado: " + tokenGuardado)
                    }

                    // Validar si el token recibido es diferente al guardado
                    if (token != tokenGuardado) {
                        Log.d(TAG, "El token ha cambiado, se procederá a registrar en el servidor")
                        DeviceManager.postRegistrarDispositivoEnServidor(token, this@MainActivity)
                    } else {
                        Log.d(TAG, "El token no ha cambiado, no es necesario registrar")
                    }

                    // Mostrar el token en un Toast
                    //Toast.makeText(MainActivity.this, "Token: " + token, Toast.LENGTH_SHORT).show();}
                    Log.d("Token",""+token)
                }
            })
    }

    companion object {
        // private AppBarConfiguration appBarConfiguration;
        private const val NOTIFICATION_PERMISSION_CODE = 1001

        private const val TAG = "MainActivity" // Para los logs
    }
}
