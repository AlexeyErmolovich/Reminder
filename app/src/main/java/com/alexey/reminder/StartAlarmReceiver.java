package com.alexey.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.alexey.reminder.model.DaoMaster;
import com.alexey.reminder.model.DaoSession;
import com.alexey.reminder.model.Note;
import com.alexey.reminder.model.NoteDao;
import com.alexey.reminder.model.TypeNoteEnum.TypeNote;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Alexey on 05.04.2016.
 */
public class StartAlarmReceiver extends BroadcastReceiver {

    private List<Note> loadData(Context context) {
        DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context, "Note", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        helper.onUpgrade(db, db.getVersion(), DaoMaster.SCHEMA_VERSION);
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession session = daoMaster.newSession();
        NoteDao noteDao = session.getNoteDao();
        List<Note> notes = noteDao.loadAll();
        return notes;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        List<Note> notes = loadData(context);

        for (Note note : notes) {
            if (!note.getPerformed()) {
                if (note.getTypeNote() == TypeNote.Birthday) {
                    Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                    alarmIntent.putExtra("NoteUUID", note.getUuid());

                    final int delay = 60000;
                    long triggerAtMillis = note.getFireDate().getTime() - note.getRemindOf() - delay;
                    if (triggerAtMillis < System.currentTimeMillis()) {
                        triggerAtMillis = note.getFireDate().getTime();
                        alarmIntent.putExtra("remind", false);
                    } else {
                        triggerAtMillis = note.getFireDate().getTime() - note.getRemindOf();
                        alarmIntent.putExtra("remind", true);
                    }

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, note.getUuid().hashCode(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
                } else if (note.getTypeNote() == TypeNote.Todo) {

                    Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                    alarmIntent.putExtra("NoteUUID", note.getUuid());

                    final int delay = 60000;
                    long triggerAtMillis;
                    if (note.getRegularly()) {
                        boolean[] daysOfWeekCheck = new boolean[7];
                        GregorianCalendar calendar = new GregorianCalendar();
                        calendar.set(Calendar.HOUR_OF_DAY, note.getFireDate().getHours());
                        calendar.set(Calendar.MINUTE, note.getFireDate().getMinutes());
                        calendar.set(Calendar.SECOND, note.getFireDate().getSeconds());
                        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 2;
                        if (currentDayOfWeek < 0) {
                            currentDayOfWeek = 6;
                        }
                        for (int i = 0; i < 7; i++) {
                            byte day = note.getDaysOfWeek();
                            day = (byte) ((byte) (day << (i + 1)) >> 7);
                            if (day == -1) {
                                daysOfWeekCheck[i] = true;
                            }
                            if (i >= currentDayOfWeek) {
                                if (daysOfWeekCheck[i] && System.currentTimeMillis() < calendar.getTimeInMillis()) {
                                    break;
                                } else {
                                    calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
                                }
                            }
                            if (i == 6) {
                                for (int j = 0; j < currentDayOfWeek; j++) {
                                    if (daysOfWeekCheck[j]) {
                                        break;
                                    } else {
                                        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
                                    }
                                }
                            }
                        }

                        triggerAtMillis = calendar.getTimeInMillis() - note.getRemindOf() - delay;
                        if (triggerAtMillis < System.currentTimeMillis()) {
                            triggerAtMillis = calendar.getTimeInMillis();
                            alarmIntent.putExtra("remind", false);
                        } else {
                            triggerAtMillis = calendar.getTimeInMillis() - note.getRemindOf();
                            alarmIntent.putExtra("remind", true);
                        }

                    } else {
                        triggerAtMillis = note.getFireDate().getTime() - note.getRemindOf() - delay;
                        if (triggerAtMillis < System.currentTimeMillis()) {
                            triggerAtMillis = note.getFireDate().getTime();
                            alarmIntent.putExtra("remind", false);
                        } else {
                            triggerAtMillis = note.getFireDate().getTime() - note.getRemindOf();
                            alarmIntent.putExtra("remind", true);
                        }
                    }
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, note.getUuid().hashCode(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
                }
            }
        }
    }
}
