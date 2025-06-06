package com.example.vetty;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vetty.controllers.TareaController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import io.reactivex.schedulers.Schedulers;
import io.reactivex.android.schedulers.AndroidSchedulers;



import com.example.vetty.UserPreferencesManager;


public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EditText emailEditText = findViewById(R.id.emailEditText);

        UserPreferencesManager prefs = UserPreferencesManager.getInstance(getApplicationContext());
        prefs.getUserEmail()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(email -> {
                    if (email != null) {
                        emailEditText.setText(email);
                    }
                }, error -> {
                    Log.e("LoginActivity", "Error leyendo email guardado", error);
                });

        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);

        loginButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                if (user != null) {
                                    String userEmail = user.getEmail();
                                    if (userEmail != null) {
                                        UserPreferencesManager userPrefs = UserPreferencesManager.getInstance(getApplicationContext());
                                        userPrefs.saveUserEmail(userEmail);
                                        Log.d("DEBUG_EMAIL", "Guardando email: " + userEmail);
                                    }

                                    verificarUsuario(user.getUid());
                                }

                            }
                            else {
                                Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(LoginActivity.this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show();
            }
        });
        registerButton.setOnClickListener(view -> {
            Log.d("DEBUG", "Botón de registro presionado");

           startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void verificarUsuario(String userId) {
        Log.d("LoginActivity", "Verificando usuario en Firestore: " + userId);

        db.collection("usuarios").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d("LoginActivity", "Usuario encontrado en Firestore: " + documentSnapshot.getData());

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e("LoginActivity", "Usuario no encontrado en Firestore");
                        Toast.makeText(LoginActivity.this, "No se encontró el usuario en Firestore", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("LoginActivity", "Error al obtener datos de Firestore", e);
                    Toast.makeText(LoginActivity.this, "Error al obtener datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}