package com.example.vetty;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import java.util.HashMap;
import java.util.Map;

public class PerfilActivity extends AppCompatActivity {

    private EditText nombreEditText, direccionEditText, telefonoEditText, correoEditText;
    private Button saveProfileButton, logoutButton, mascotasButton;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseUser user;

    private UserPreferences userPreferences;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Primero inicializa tus preferencias
        userPreferences = new UserPreferences(this);

        // Luego conecta tus TextViews
        TextView nombreTextView = findViewById(R.id.nombreEditText); // ← Corrige este id si era distinto
        TextView telefonoTextView = findViewById(R.id.telefonoEditText);
        TextView emailTextView = findViewById(R.id.correoEditText);

        // Y ahora sí puedes usar userPreferences
        userPreferences.getUserName().subscribe(nombre -> {
            nombreTextView.setText(nombre);
        });

        userPreferences.getUserPhone().subscribe(telefono -> {
            telefonoTextView.setText(telefono);
        });

        userPreferences.getUserEmail().subscribe(email -> {
            emailTextView.setText(email);
        });
        nombreEditText = findViewById(R.id.nombreEditText);
        direccionEditText = findViewById(R.id.direccionEditText);
        telefonoEditText = findViewById(R.id.telefonoEditText);
        correoEditText = findViewById(R.id.correoEditText);

        saveProfileButton = findViewById(R.id.saveProfileButton);
        logoutButton = findViewById(R.id.logoutButton);
        mascotasButton = findViewById(R.id.mascotasButton);

        userPreferences = new UserPreferences(this);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            cargarDatosUsuario();
        }

        saveProfileButton.setOnClickListener(view -> guardarPerfil());
        logoutButton.setOnClickListener(view -> cerrarSesion());
        mascotasButton.setOnClickListener(view -> abrirMascotasActivity());
    }

    private void cargarDatosUsuario() {
        String userId = user.getUid();
        db.collection("usuarios").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                nombreEditText.setText(documentSnapshot.getString("nombre"));
                correoEditText.setText(documentSnapshot.getString("correo"));
                telefonoEditText.setText(documentSnapshot.getString("telefono"));
                direccionEditText.setText(documentSnapshot.getString("direccion"));
            }
        });
    }

    private void guardarPerfil() {
        String nombre = nombreEditText.getText().toString().trim();
        String direccion = direccionEditText.getText().toString().trim();
        String telefono = telefonoEditText.getText().toString().trim();
        String correo = correoEditText.getText().toString().trim();

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(direccion)) {
            Toast.makeText(PerfilActivity.this, "Nombre y dirección son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(correo) && TextUtils.isEmpty(telefono)) {
            Toast.makeText(PerfilActivity.this, "Debes ingresar al menos un correo o teléfono", Toast.LENGTH_SHORT).show();
            return;
        }

        if (user != null) {
            String userId = user.getUid();
            Map<String, Object> usuarioData = new HashMap<>();
            usuarioData.put("nombre", nombre);
            usuarioData.put("correo", correo);
            usuarioData.put("telefono", telefono);
            usuarioData.put("direccion", direccion);

            db.collection("usuarios").document(userId)
                    .set(usuarioData)
                    .addOnSuccessListener(aVoid -> Toast.makeText(PerfilActivity.this, "Perfil actualizado", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(PerfilActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show());
        }

        // Guardar también en UserPreferences
        userPreferences.updateUserName(nombre)
                .subscribe(success -> {}, error -> {});
        userPreferences.updateUserPhone(telefono)
                .subscribe(success -> {}, error -> {});

        // Determinar si el perfil está completo
        boolean perfilCompleto = !TextUtils.isEmpty(nombre) &&
                (!TextUtils.isEmpty(correo) || !TextUtils.isEmpty(telefono));

        userPreferences.setProfileCompleted(perfilCompleto)
                .subscribe(success -> {}, error -> {});

    }

    private void cerrarSesion() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(PerfilActivity.this, LoginActivity.class));
        finish();
    }

    private void abrirMascotasActivity() {
        startActivity(new Intent(PerfilActivity.this, MascotasActivity.class));
    }
}
