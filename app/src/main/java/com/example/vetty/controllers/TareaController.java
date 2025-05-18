package com.example.vetty.controllers;

import android.content.Context;

import com.example.vetty.data.DatabaseHelper;
import com.example.vetty.models.tarea;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class TareaController {

    private final Context ctx;

    public TareaController(Context ctx) {
        this.ctx = ctx.getApplicationContext();
    }

    public Completable agregarTarea(String descripcion) {
        return Completable.fromAction(() ->
                DatabaseHelper.getInstance(ctx).tareaDao().insert(new tarea(descripcion))
        ).subscribeOn(Schedulers.io());
    }

    public Completable eliminarTarea(tarea t) {
        return Completable.fromAction(() ->
                DatabaseHelper.getInstance(ctx).tareaDao().delete(t)
        ).subscribeOn(Schedulers.io());
    }

    public Flowable<List<tarea>> obtenerTareas() {
        return DatabaseHelper.getInstance(ctx).tareaDao().getAll()
                .subscribeOn(Schedulers.io());
    }

    public Completable actualizarTarea(tarea tarea) {
        return Completable.fromAction(() ->
                DatabaseHelper.getInstance(ctx).tareaDao().update(tarea)
        ).subscribeOn(Schedulers.io());
    }

    public Completable sincronizarTareasConFirestore() {
        return obtenerTareas()
                .firstOrError()
                .flatMapCompletable(tareas -> {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    List<Completable> completables = new ArrayList<>();
                    for (tarea tarea : tareas) {
                        Map<String, Object> tareaMap = new HashMap<>();
                        tareaMap.put("descripcion", tarea.descripcion);
                        tareaMap.put("completada", tarea.completada);
                        tareaMap.put("id", tarea.id);

                        String tareaId = String.valueOf(tarea.id);

                        Completable c = Completable.create(emitter -> {
                            db.collection("usuarios")
                                    .document(uid)
                                    .collection("tareas")
                                    .document(tareaId)
                                    .set(tareaMap)
                                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                                    .addOnFailureListener(emitter::onError);
                        });

                        completables.add(c);
                    }

                    return Completable.merge(completables);
                });
    }

    public Completable descargarTareasDesdeFirestore() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        return Completable.create(emitter -> {
            db.collection("usuarios")
                    .document(uid)
                    .collection("tareas")
                    .get()
                    .addOnSuccessListener((QuerySnapshot queryDocumentSnapshots) -> {
                        List<Completable> inserciones = new ArrayList<>();

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            tarea tareaDescargada = doc.toObject(tarea.class);


                            inserciones.add(agregarOActualizarTarea(tareaDescargada));
                        }


                        Completable.merge(inserciones)
                                .subscribe(() -> emitter.onComplete(), emitter::onError);
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Completable agregarOActualizarTarea(tarea tarea) {
        return Completable.fromAction(() -> {

            DatabaseHelper.getInstance(ctx).tareaDao().insert(tarea);
        }).subscribeOn(Schedulers.io());
    }
}
