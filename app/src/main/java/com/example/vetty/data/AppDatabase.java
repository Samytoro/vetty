package com.example.vetty.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.vetty.models.tarea;

@Database(entities = {tarea.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TareaDao tareaDao();
}
