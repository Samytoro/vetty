package com.example.vetty.data;

import androidx.room.*;
import io.reactivex.Flowable;
import java.util.List;
import com.example.vetty.models.tarea;

@Dao
public interface TareaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(tarea tarea);

    @Delete void delete(tarea tarea);
    @Query("SELECT * FROM tareas ORDER BY id DESC")
    Flowable<List<tarea>> getAll();

    @Update
    void update(tarea tarea);



}





