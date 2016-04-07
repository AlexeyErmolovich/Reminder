package com.alexey.reminder;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alexey.reminder.model.DaoMaster;
import com.alexey.reminder.model.DaoSession;
import com.alexey.reminder.model.Note;
import com.alexey.reminder.model.NoteDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexey on 07.04.2016.
 */
public class SearchNotesAsyncTask extends AsyncTask<String,Void, List<Note>> {
    private AppCompatActivity activity;
    private ProgressBar progressBar;
    private TextView textNotFoundData;

    public SearchNotesAsyncTask(AppCompatActivity activity) {
        this.activity = activity;
        this.progressBar = (ProgressBar) activity.findViewById(R.id.progress);
        this.textNotFoundData = (TextView) activity.findViewById(R.id.textNotFound);
    }

    @Override
    protected List<Note> doInBackground(String... params) {
        DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(activity, "Note", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        helper.onUpgrade(db, db.getVersion(), DaoMaster.SCHEMA_VERSION);
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession session = daoMaster.newSession();
        NoteDao noteDao = session.getNoteDao();
        List<Note> noteList = new ArrayList<>();

        for(String param:params) {
            noteList = Note.getNotesOnItsHeader(noteDao, param);
            List<Note> listDescription = Note.getNotesOnItsDescription(noteDao, param);
            for (Note noteDescription : listDescription) {
                boolean add = true;
                for (Note note : noteList) {
                    if (note.getUuid().equals(noteDescription.getUuid())) {
                        add = false;
                        break;
                    }
                }
                if (add) {
                    noteList.add(noteDescription);
                }
            }
            List<Note> listBody = Note.getNotesOnItsBody(noteDao, param);
            for (Note noteBody : listBody) {
                boolean add = true;
                for (Note note : noteList) {
                    if (note.getUuid().equals(noteBody.getUuid())) {
                        add = false;
                        break;
                    }
                }
                if (add) {
                    noteList.add(noteBody);
                }
            }
        }
        for (Note note : noteList) {
            Bitmap bitmap = null;
            if (note.getImageCut().length != 0) {
                bitmap = BitmapFactory.decodeByteArray(note.getImageCut(), 0, note.getImageCut().length);
            }
            note.setBitmap(bitmap);
            note.setShowInfo(false);
        }
        return noteList;
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
        textNotFoundData.setVisibility(View.GONE);
    }

    @Override
    protected void onPostExecute(List<Note> notes) {
        if (notes.isEmpty()) {
            textNotFoundData.setText(R.string.exception_search_not_found);
            textNotFoundData.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);
    }
}
