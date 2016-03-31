package com.alexey.reminder;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;

import com.alexey.reminder.model.DaoMaster;
import com.alexey.reminder.model.DaoSession;
import com.alexey.reminder.model.Note;
import com.alexey.reminder.model.NoteDao;
import com.alexey.reminder.model.TypeNoteEnum.TypeNote;

import java.text.DateFormat;
import java.util.Locale;

/**
 * Created by Alexey on 31.03.2016.
 */
public class AlarmReceiver extends BroadcastReceiver {
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
        String typeNote = null;
        String textTicket = null;

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intentJump = new Intent(context, MainActivity.class);
        intentJump.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentJump, Intent.FILL_IN_ACTION);


        if (note.getTypeNote() == TypeNote.Birthday) {
            typeNote = context.getString(R.string.birthday);
            textTicket = context.getString(R.string.notification_birthday);
        }
        if (note.getTypeNote() == TypeNote.Todo) {
            typeNote = context.getString(R.string.todo);
            textTicket = context.getString(R.string.notification_todo);
        }

        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_application)
                .setLargeIcon(BitmapFactory.decodeByteArray(note.getImage(), 0, note.getImage().length))
                .setWhen(note.getFireDate().getTime())
                .setAutoCancel(true)
                .setTicker(textTicket)
                .setContentInfo(typeNote)
                .setContentTitle(note.getHeader());


        if (remindOf) {
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
            DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
            builder.setContentText(dateFormat.format(note.getFireDate()) +
                            " " + timeFormat.format(note.getFireDate()));
        } else {
            builder.setContentText(note.getDescription());
        }

        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
        manager.notify(note.getUuid().hashCode(), notification);

        if (remindOf) {
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            alarmIntent.putExtra("NoteUUID", note.getUuid());
            alarmIntent.putExtra("remind", false);
            PendingIntent pendingAlarm = PendingIntent.getBroadcast(context, note.getUuid().hashCode(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, note.getFireDate().getTime(), pendingAlarm);
        } else {
            note.setPerformed(true);
            noteDao.update(note);
        }
    }
}
