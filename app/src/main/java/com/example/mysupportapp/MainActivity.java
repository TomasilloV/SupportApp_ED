package com.example.mysupportapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;


public class MainActivity extends AppCompatActivity {

    // private AppBarConfiguration appBarConfiguration;
    private static final int NOTIFICATION_PERMISSION_CODE = 1001;

    private static final String TAG = "MainActivity"; // Para los logs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registrarDispositivo();
        pedirPermisoNotificaciones();
        /*var fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SoporteMenu.class);
                startActivity(intent);
            }
        });*/
    }

    private void registrarDispositivo() {
        Log.d(TAG, "Iniciando registro de dispositivo");

        // Obtener el token de Firebase
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task == null) {
                            Log.e(TAG, "El task de Firebase es null");
                            return; // Si el task es null, no continuamos
                        }

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fallo al obtener el token FCM", task.getException());
                            return;
                        }

                        String token = task.getResult();
                        Log.d(TAG, "Token recibido: " + token);

                        // Validar que el token no sea null
                        if (token == null || token.isEmpty()) {
                            Log.e(TAG, "El token es null o vacío");
                            Toast.makeText(MainActivity.this, "Error: Token FCM vacío", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Obtener token guardado de SharedPreferences
                        String tokenGuardado = getSharedPreferences(Constantes.SP_FILE, 0)
                                .getString(Constantes.SP_KEY_DEVICEID, null);

                        if (tokenGuardado == null) {
                            Log.d(TAG, "Token guardado es null");
                        } else {
                            Log.d(TAG, "Token guardado: " + tokenGuardado);
                        }

                        // Validar si el token recibido es diferente al guardado
                        if (!token.equals(tokenGuardado)) {
                            Log.d(TAG, "El token ha cambiado, se procederá a registrar en el servidor");
                            DeviceManager.postRegistrarDispositivoEnServidor(token, MainActivity.this);
                        } else {
                            Log.d(TAG, "El token no ha cambiado, no es necesario registrar");
                        }

                        // Mostrar el token en un Toast
                        //Toast.makeText(MainActivity.this, "Token: " + token, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void pedirPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            } else {
                Log.d("Permiso", "Ya tienes permiso pa' notificaciones");
            }
        } else {
            Log.d("Permiso", "No hace falta pedir permiso en esta versión de Android");
        }
    }

    // Opcional: manejar la respuesta del usuario
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permiso", "¡Permiso concedido!");
            } else {
                Log.d("Permiso", "Permiso denegado...");
            }
        }
    }
}
