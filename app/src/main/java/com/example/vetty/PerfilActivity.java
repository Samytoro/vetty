package com.example.vetty;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetty.controllers.TareaController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import java.util.HashMap;
import java.util.Map;
import com.example.vetty.UserPreferencesManager;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.android.schedulers.AndroidSchedulers;



public class PerfilActivity extends AppCompatActivity {

    private EditText nombreEditText, direccionEditText, telefonoEditText, correoEditText;
    private Button saveProfileButton, logoutButton, mascotasButton;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private EditText nuevaTareaEditText;
    private Button btnAgregarTarea;
    private RecyclerView recyclerViewTareas;
    private TareaAdapter tareaAdapter;
    private TareaController tareaController;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        nombreEditText = findViewById(R.id.nombreEditText);
        direccionEditText = findViewById(R.id.direccionEditText);
        telefonoEditText = findViewById(R.id.telefonoEditText);
        correoEditText = findViewById(R.id.correoEditText);

        saveProfileButton = findViewById(R.id.saveProfileButton);
        logoutButton = findViewById(R.id.logoutButton);
        mascotasButton = findViewById(R.id.mascotasButton);
        tareaController = new TareaController(getApplicationContext());
        nuevaTareaEditText = findViewById(R.id.nuevaTareaEditText);
        btnAgregarTarea   = findViewById(R.id.btnAgregarTarea);
        recyclerViewTareas= findViewById(R.id.recyclerViewTareas);

        tareaAdapter = new TareaAdapter(tareaController);
        recyclerViewTareas.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTareas.setAdapter(tareaAdapter);

        recyclerViewTareas.setNestedScrollingEnabled(false);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            cargarDatosUsuario();

            tareaController.sincronizarTareasConFirestore()
                    .andThen(tareaController.descargarTareasDesdeFirestore())
                    .subscribe(() -> {
                        runOnUiThread(() -> Toast.makeText(this, "Tareas sincronizadas con Firestore", Toast.LENGTH_SHORT).show());
                    }, throwable -> {
                        throwable.printStackTrace();
                    });

            tareaController.obtenerTareas()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(tareaAdapter::setData, Throwable::printStackTrace);

            btnAgregarTarea.setOnClickListener(v -> {
                String desc = nuevaTareaEditText.getText().toString().trim();
                if(!desc.isEmpty()){
                    tareaController.agregarTarea(desc)
                            .andThen(tareaController.sincronizarTareasConFirestore())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> nuevaTareaEditText.setText(""),
                                    Throwable::printStackTrace);
                }
            });


        }

        UserPreferencesManager prefs = UserPreferencesManager.getInstance(getApplicationContext());

        prefs.getUserName()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(name -> {
                    if (name != null) nombreEditText.setText(name);
                }, error -> {
                    error.printStackTrace();
                });

        prefs.getUserAddress()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(address -> {
                    if (address != null) direccionEditText.setText(address);
                }, error -> {
                    error.printStackTrace();
                });


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