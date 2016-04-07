package com.alexey.reminder.model.Migration;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Alexey on 01.04.2016.
 */
public class MigrationDatabase {
    public void startMigrationV1ToV2(SQLiteDatabase db) {
        final int SCHEMA_VERSION = 2;
        db.beginTransaction();
        try {
            Log.e("Migration", "Start");
            migration(db);
            db.setVersion(SCHEMA_VERSION);
            db.setTransactionSuccessful();
            Log.e("Migration", "Complete");
        } catch (Exception e) {
            Log.e("Migration", e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    private void migration(SQLiteDatabase db) {
        final String TABLE_NAME_NOTE = "NOTE";

        String query = "ALTER TABLE " + TABLE_NAME_NOTE + " ADD COLUMN REGULARLY INTEGER";
        db.rawQuery(query, null);

        query = "ALTER TABLE " + TABLE_NAME_NOTE + " ADD COLUMN DAYSOFWEEK INTEGER";
        db.rawQuery(query, null);
    }
}
