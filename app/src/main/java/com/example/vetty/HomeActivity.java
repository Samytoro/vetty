package com.example.vetty;


import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.Gravity;
import android.graphics.Color;
import com.google.firebase.auth.FirebaseAuth;


public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageView btnPerfil = findViewById(R.id.btnPerfil);
        ImageView btnMascotas = findViewById(R.id.btnMascotas);
        ImageView btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, PerfilActivity.class);
            startActivity(intent);
        });

        btnMascotas.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MascotasActivity.class);
            startActivity(intent);
        });

        btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });



        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(Color.WHITE);


        TextView vettyText = new TextView(this);
        vettyText.setText("VETTY");
        vettyText.setTextSize(40);
        vettyText.setTextColor(Color.BLACK);
        vettyText.setGravity(Gravity.CENTER);

        // texto al layout
        layout.addView(vettyText);



    }
}
