package com.example.vetty;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MascotasActivity extends AppCompatActivity {
    private EditText especieEditText, nombreMascotaEditText, edadEditText, razaEditText, sexoEditText;
    private Button agregarMascotaButton, guardarMascotaEditadaButton;
    private RecyclerView mascotasRecyclerView;
    private MascotaAdapter mascotaAdapter;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private List<Map<String, Object>> listaMascotas = new ArrayList<>();
    private int mascotaSeleccionada = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mascotas);

        especieEditText = findViewById(R.id.especieEditText);
        nombreMascotaEditText = findViewById(R.id.nombreMascotaEditText);
        edadEditText = findViewById(R.id.edadMascotaEditText);
        razaEditText = findViewById(R.id.razaMascotaEditText);
        sexoEditText = findViewById(R.id.sexoMascotaEditText);

        agregarMascotaButton = findViewById(R.id.agregarMascotaButton);
        guardarMascotaEditadaButton = findViewById(R.id.guardarMascotaEditadaButton);

        mascotasRecyclerView = findViewById(R.id.mascotasRecyclerView);
        mascotasRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        mascotaAdapter = new MascotaAdapter(listaMascotas, new MascotaAdapter.OnMascotaClickListener() {
            @Override
            public void onEditar(int position) {
                Map<String, Object> mascota = listaMascotas.get(position);
                especieEditText.setText((String) mascota.get("especie"));
                nombreMascotaEditText.setText((String) mascota.get("nombre"));
                edadEditText.setText((String) mascota.get("edad"));
                razaEditText.setText((String) mascota.get("raza"));
                sexoEditText.setText((String) mascota.get("sexo"));
                mascotaSeleccionada = position;
                guardarMascotaEditadaButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onEliminar(int position) {
                listaMascotas.remove(position);
                mascotaAdapter.notifyDataSetChanged();
                actualizarMascotasEnFirebase();
            }
        });

        mascotasRecyclerView.setAdapter(mascotaAdapter);
        if (user != null) cargarMascotas();

        agregarMascotaButton.setOnClickListener(view -> agregarMascota());
        guardarMascotaEditadaButton.setOnClickListener(view -> guardarMascotaEditada());
    }

    private void cargarMascotas() {
        String userId = user.getUid();
        db.collection("usuarios").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.contains("mascotas")) {
                listaMascotas.clear();
                listaMascotas.addAll((List<Map<String, Object>>) documentSnapshot.get("mascotas"));
                mascotaAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Error al cargar mascotas", Toast.LENGTH_SHORT).show());
    }

    private void agregarMascota() {
        String especie = especieEditText.getText().toString().trim();
        if (TextUtils.isEmpty(especie)) {
            Toast.makeText(this, "La especie es obligatoria", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> mascotaData = new HashMap<>();
        mascotaData.put("especie", especie);
        mascotaData.put("nombre", nombreMascotaEditText.getText().toString().trim());
        mascotaData.put("edad", edadEditText.getText().toString().trim());
        mascotaData.put("raza", razaEditText.getText().toString().trim());
        mascotaData.put("sexo", sexoEditText.getText().toString().trim());

        listaMascotas.add(mascotaData);
        mascotaAdapter.notifyDataSetChanged();

        if (user != null) {
            String userId = user.getUid();
            db.collection("usuarios").document(userId)
                    .update("mascotas", FieldValue.arrayUnion(mascotaData))
                    .addOnSuccessListener(aVoid -> limpiarCamposMascota())
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al agregar mascota", Toast.LENGTH_SHORT).show());
        }
    }

    private void guardarMascotaEditada() {
        if (mascotaSeleccionada == -1) {
            Toast.makeText(this, "No hay mascota seleccionada", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> mascota = listaMascotas.get(mascotaSeleccionada);
        mascota.put("especie", especieEditText.getText().toString().trim());
        mascota.put("nombre", nombreMascotaEditText.getText().toString().trim());
        mascota.put("edad", edadEditText.getText().toString().trim());
        mascota.put("raza", razaEditText.getText().toString().trim());
        mascota.put("sexo", sexoEditText.getText().toString().trim());

        mascotaAdapter.notifyDataSetChanged();
        actualizarMascotasEnFirebase();
        limpiarCamposMascota();
        guardarMascotaEditadaButton.setVisibility(View.GONE);
    }

    private void actualizarMascotasEnFirebase() {
        String userId = user.getUid();
        db.collection("usuarios").document(userId).update("mascotas", listaMascotas)
                .addOnSuccessListener(aVoid -> mascotaAdapter.notifyDataSetChanged())
                .addOnFailureListener(e -> Toast.makeText(this, "Error al actualizar mascotas", Toast.LENGTH_SHORT).show());
    }

    private void limpiarCamposMascota() {
        especieEditText.setText("");
        nombreMascotaEditText.setText("");
        edadEditText.setText("");
        razaEditText.setText("");
        sexoEditText.setText("");
    }
}