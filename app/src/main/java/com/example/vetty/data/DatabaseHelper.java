package com.example.vetty.data;

import android.content.Context;
import androidx.room.Room;

public final class DatabaseHelper {
    private static volatile AppDatabase INSTANCE;

    private DatabaseHelper() { }

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (DatabaseHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "vetty_db"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
