package com.example.vetty.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tareas")
public class tarea {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String descripcion;
    public boolean completada;
    public tarea() {
    }
    public tarea(String descripcion) {
        this.descripcion = descripcion;
        this.completada = false;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isCompletada() { return completada; }
    public void setCompletada(boolean completada) { this.completada = completada; }
}





