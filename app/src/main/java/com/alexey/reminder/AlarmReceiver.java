package com.alexey.reminder;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.alexey.reminder.model.DaoMaster;
import com.alexey.reminder.model.DaoSession;
import com.alexey.reminder.model.Note;
import com.alexey.reminder.model.NoteDao;
import com.alexey.reminder.model.TypeNoteEnum.TypeNote;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Alexey on 31.03.2016.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private TextToSpeech tts;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        String uuid = intent.getStringExtra("NoteUUID");
        Boolean remindOf = intent.getBooleanExtra("remind", false);

        DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context, "Note", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession session = daoMaster.newSession();
        NoteDao noteDao = session.getNoteDao();

        Note note = noteDao.load(uuid);
        if (note.getPerformed()) {
            return;
        }
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intentJump = new Intent(context, ChangeNoteActivity.class);
        intentJump.putExtra("NoteUUID", note.getUuid());
        intentJump.putExtra("edit", false);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, note.getUuid().hashCode(), intentJump,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_application)
                .setLargeIcon(BitmapFactory.decodeByteArray(note.getImage(), 0, note.getImage().length))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(note.getHeader());

        if (remindOf) {
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
            DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
            builder.setContentText(dateFormat.format(note.getFireDate()) +
                    " " + timeFormat.format(note.getFireDate()));
        } else {
            builder.setContentText(note.getDescription());
        }

        if (note.getTypeNote() == TypeNote.Birthday) {
            builder.setTicker(context.getString(R.string.notification_birthday))
                    .setContentInfo(context.getString(R.string.birthday));

            if (remindOf) {
                initAlarmManagerForNote(context, note, note.getFireDate().getTime(), false);
            } else {
                note.setPerformed(true);
                noteDao.update(note);
            }

        } else if (note.getTypeNote() == TypeNote.Todo) {
            if (note.getRegularly()) {

                builder.setTicker(context.getString(R.string.notification_todo))
                        .setContentInfo(context.getString(R.string.todo));

                final int delay = 60000;
                boolean remind;
                long triggerAtMillis;
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
                    remind = false;
                } else {
                    triggerAtMillis = calendar.getTimeInMillis() - note.getRemindOf();
                    remind = true;
                }

                if (remindOf) {
                    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
                    DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
                    builder.setContentText(dateFormat.format(calendar.getTime()) +
                            " " + timeFormat.format(calendar.getTime()));
                }
                if (remindOf) {
                    initAlarmManagerForNote(context, note, triggerAtMillis, remind);
                } else {
                    initAlarmManagerForNote(context, note, triggerAtMillis, remind);
                }
            } else {

                builder.setTicker(context.getString(R.string.notification_todo))
                        .setContentInfo(context.getString(R.string.todo));

                if (remindOf) {
                    initAlarmManagerForNote(context, note, note.getFireDate().getTime(), false);
                } else {
                    note.setPerformed(true);
                    noteDao.update(note);
                }
            }
        }

        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_VIBRATE;

        Intent textToSpeech = new Intent(context, TextToSpeechService.class);
        textToSpeech.putExtra("text", note.getHeader());
        context.startService(textToSpeech);
        manager.notify(note.getUuid().hashCode(), notification);
    }

    private void initAlarmManagerForNote(Context context, Note note, long time, boolean remind) {
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.putExtra("NoteUUID", note.getUuid());
        alarmIntent.putExtra("remind", remind);
        PendingIntent pendingAlarm = PendingIntent.getBroadcast(context, note.getUuid().hashCode(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingAlarm);
    }
}
