package com.alexey.reminder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alexey.reminder.model.DaoMaster;
import com.alexey.reminder.model.DaoSession;
import com.alexey.reminder.model.Note;
import com.alexey.reminder.model.NoteDao;
import com.alexey.reminder.model.TypeNoteEnum.TypeNote;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Alexey on 21.03.2016.
 */
public class NoteAdapter extends BaseAdapter {

    private AppCompatActivity activity;
    private List<Note> noteList;
    private NoteDao noteDao;
    private LayoutInflater inflater;

    private Comparator<Note> comparatorNote = new Comparator<Note>() {

        @Override
        public int compare(Note lhs, Note rhs) {
            if (lhs.getPriority().getValue() < rhs.getPriority().getValue()) {
                return 1;
            } else if (lhs.getPriority().getValue() > rhs.getPriority().getValue()) {
                return -1;
            } else {
                if (lhs.getTimeStamp().getTime() < rhs.getTimeStamp().getTime()) {
                    return 1;
                } else if (lhs.getTimeStamp().getTime() > rhs.getTimeStamp().getTime()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    };


    public NoteAdapter(AppCompatActivity activity) {
        this.activity = activity;
        this.inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.loadData();
    }

    private void loadData() {
        DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(activity, "Note", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession session = daoMaster.newSession();
        this.noteDao = session.getNoteDao();

        this.noteList = this.noteDao.loadAll();
        if (this.noteList.size() > 1) {
            Collections.sort(this.noteList, this.comparatorNote);
        }
        for (Note note : this.noteList) {
            Bitmap bitmap = null;
            if (note.getImageCut().length != 0) {
                bitmap = BitmapFactory.decodeByteArray(note.getImageCut(), 0, note.getImageCut().length);
            } 
            note.setBitmap(bitmap);
        }
    }

    static class ViewHolder {
        LinearLayout layoutItemListView;

        ImageView imageView;
        TextView textViewHeader;
        TextView textViewDescription;

        RelativeLayout layoutTypeNote;
        TextView textViewTypeNote;
    }

    @Override
    public int getCount() {
        return this.noteList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.noteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = this.inflater.inflate(R.layout.item_listview, parent, false);
            holder = new ViewHolder();
            holder.layoutItemListView = (LinearLayout) convertView.findViewById(R.id.containerItemListView);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageNote);
            holder.textViewHeader = (TextView) convertView.findViewById(R.id.textViewHeader);
            holder.textViewDescription = (TextView) convertView.findViewById(R.id.textViewDescription);
            holder.layoutTypeNote = (RelativeLayout) convertView.findViewById(R.id.layoutTypeNote);
            holder.textViewTypeNote = (TextView) convertView.findViewById(R.id.textViewTypeNote);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Note note = (Note) getItem(position);

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                initAlertDialog(note);
                return true;
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = new TextView(activity);
                holder.layoutItemListView.addView(textView);
                textView.setText("BLAHJGJHJKHKDJHKJDHKJDHDKJHDKJHDKHDKJHDKJDHKJDHKJDHKJDH");
            }
        });

        holder.textViewHeader.setText(note.getHeader());
        holder.textViewDescription.setText(note.getDescription());

        if (note.getBitmap() != null) {
            holder.imageView.setImageBitmap(note.getBitmap());
        }

        TypeNote noteType = note.getTypeNote();
        if (noteType == TypeNote.Todo) {
            holder.layoutTypeNote.setBackgroundColor(activity.getResources().getColor(R.color.colorTodoDark));
            holder.textViewTypeNote.setText(noteType.toString());
            holder.textViewTypeNote.setTextColor(activity.getResources().getColor(R.color.colorTodo));
        } else if (noteType == TypeNote.Birthday) {
            holder.layoutTypeNote.setBackgroundColor(activity.getResources().getColor(R.color.colorBirthdaysDark));
            holder.textViewTypeNote.setText(noteType.toString());
            holder.textViewTypeNote.setTextColor(activity.getResources().getColor(R.color.colorBirthdays));
        } else if (noteType == TypeNote.Idea) {
            holder.layoutTypeNote.setBackgroundColor(activity.getResources().getColor(R.color.colorIdeasDark));
            holder.textViewTypeNote.setText(noteType.toString());
            holder.textViewTypeNote.setTextColor(activity.getResources().getColor(R.color.colorIdeas));
        }


        if (note.getPerformed()) {
            holder.layoutItemListView.setAlpha(0.5f);
        } else {
            holder.layoutItemListView.setAlpha(1.0f);
        }

        return convertView;
    }

    private void initAlertDialog(final Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("What to do?");
        builder.setItems(new String[]{"Change note", "Delete note"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0) {
                    Intent intent = new Intent(activity, ChangeNoteActivity.class);
                    intent.putExtra("NoteUUID", note.getUuid());
                    activity.startActivityForResult(intent, 4);
                }else if(which==1){
                    noteDao.delete(note);
                    notifyDataSetChanged();
                }
            }
        });
        builder.show();
    }

    @Override
    public void notifyDataSetChanged() {
        this.loadData();
        super.notifyDataSetChanged();
    }
}
