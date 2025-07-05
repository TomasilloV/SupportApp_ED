package com.example.mysupportapp

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.mysupportapp.singleton.PreferencesHelper

class SoporteMenu : AppCompatActivity() {

    private val preferencesManager = PreferencesHelper.getPreferencesManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.soporte_menu)
    }
    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("¿Salir de la aplicación?")
            .setMessage("¿Estás seguro de que deseas cerrar la app?")
            .setCancelable(false)
            .setPositiveButton("Sí") { _, _ ->
                finishAffinity() // Cierra todas las actividades
                super.onBackPressed() // Salir de la aplicación
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }


}

