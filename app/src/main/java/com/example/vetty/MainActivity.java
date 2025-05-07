package com.example.vetty;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private UserPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar FirebaseAuth y UserPreferences
        auth = FirebaseAuth.getInstance();
        userPreferences = new UserPreferences(getApplicationContext());

        FirebaseUser usuarioActual = auth.getCurrentUser();

        if (usuarioActual == null) {
            // Usuario no autenticado en Firebase
            redirigirALogin();
        } else {
            // Usuario autenticado, verificar si la sesión sigue activa en DataStore
            userPreferences.isSessionActive().subscribe(isActive -> {
                if (isActive) {
                    redirigirAPerfil();
                } else {
                    redirigirALogin();
                }
            }, error -> {
                // En caso de error leyendo DataStore, asumir sesión inactiva
                redirigirALogin();
            });
        }
    }

    private void redirigirALogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void redirigirAPerfil() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}

//package com.example.vetty;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//public class MainActivity extends AppCompatActivity {
//
//    private FirebaseAuth auth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        auth = FirebaseAuth.getInstance();
//
//        FirebaseUser usuarioActual = auth.getCurrentUser();
//
//        if (usuarioActual == null) {
//            // Si el usuario no está autenticado, redirigir al LoginActivity
//            redirigirALogin();
//        } else {
//            // Si el usuario está autenticado, redirigir a PerfilActivity
//            redirigirAPerfil();
//        }
//    }
//
//    private void redirigirALogin() {
//        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//        startActivity(intent);
//        finish();
//    }
//
//    private void redirigirAPerfil() {
//        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
//        startActivity(intent);
//        finish();
//    }
//}
