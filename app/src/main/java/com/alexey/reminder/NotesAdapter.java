package com.alexey.reminder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.alexey.reminder.model.DaoMaster;
import com.alexey.reminder.model.DaoSession;
import com.alexey.reminder.model.Note;
import com.alexey.reminder.model.NoteDao;
import com.alexey.reminder.model.PriorityEnum.Priority;
import com.alexey.reminder.model.TypeNoteEnum.TypeNote;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by Alexey on 21.03.2016.
 */
public class NotesAdapter extends BaseAdapter {
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

/*    private AppCompatActivity activity;
    private ProgressBar progressBar;
    private List<Note> noteList;
    private NoteDao noteDao;
    private LayoutInflater inflater;
    private boolean update;

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


    public NotesAdapter(AppCompatActivity activity) {
        this.activity = activity;
        this.inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.progressBar = (ProgressBar) activity.findViewById(R.id.progress);
        this.update = false;
        this.loadData();
    }

    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(activity, "Note", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        helper.onUpgrade(db, db.getVersion(), DaoMaster.SCHEMA_VERSION);
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession session = daoMaster.newSession();
        this.noteDao = session.getNoteDao();

        this.noteList = this.noteDao.loadAll();
        if (this.noteList.size() > 1) {
            Collections.sort(this.noteList, this.comparatorNote);
        }
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        noteList.add(new Note(UUID.randomUUID().toString(), "dw", "dd", "dw", new Date(), new Date(), false, Byte.valueOf("0"), false, 0l, new byte[0], new byte[0], Priority.None, TypeNote.Todo));
        for (Note note : this.noteList) {
            Bitmap bitmap = null;
            if (note.getImageCut().length != 0) {
                bitmap = BitmapFactory.decodeByteArray(note.getImageCut(), 0, note.getImageCut().length);
            }
            note.setBitmap(bitmap);
            note.setShowInfo(false);
        }

        progressBar.setVisibility(View.GONE);
    }

    static class ViewHolder {
        LinearLayout layoutItemListView;

        ImageView imageView;
        TextView textViewHeader;
        TextView textViewDescription;

        RelativeLayout layoutTypeNote;
        TextView textViewTypeNote;

        View viewLine;
        TextView textViewBody;
        RelativeLayout layoutDate;
        TextView textViewDate;
        LinearLayout layoutDayOfWeek;
        RelativeLayout layoutTime;
        TextView textViewTime;
        RelativeLayout layoutRemindOf;
        TextView textViewRemindOf;
        AppCompatRatingBar ratingBar;

        int closed;
        int opened;
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
            holder = getViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Note note = (Note) getItem(position);

        if (update) {
            update = false;
            note.setShowInfo(false);
        }

        if (note.isShowInfo()) {
            initViewDown(holder, note);
        } else {
            removeViewInItem(holder);
        }

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
                if (note.isShowInfo()) {
                    removeViewInItem(holder);
                    note.setShowInfo(false);
                } else {
                    initViewDown(holder, note);
                    note.setShowInfo(true);
                }
            }
        });

        holder.textViewHeader.setText(note.getHeader());
        holder.textViewDescription.setText(note.getDescription());

        if (note.getBitmap() == null) {
            holder.imageView.setImageBitmap(((BitmapDrawable) activity.getResources().getDrawable(R.drawable.account_circle)).getBitmap());
        } else {
            holder.imageView.setImageBitmap(note.getBitmap());
        }

        initTypeNote(holder, note);
        initAlphaView(holder, note);

        return convertView;
    }

    private void initViewDown(ViewHolder holder, Note note) {
        try {
            holder.layoutItemListView.addView(holder.viewLine, holder.layoutItemListView.getChildCount());
            if (!note.getBody().isEmpty()) {
                holder.layoutItemListView.addView(holder.textViewBody, holder.layoutItemListView.getChildCount());
                holder.textViewBody.setText(note.getBody());
            }

            if (note.getTypeNote() == TypeNote.Birthday) {
                holder.layoutItemListView.addView(holder.layoutDate);
                holder.layoutItemListView.addView(holder.layoutTime);
                DateFormat formatDate = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
                DateFormat formatTime = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
                holder.textViewDate.setText(formatDate.format(note.getFireDate()));
                holder.textViewTime.setText(formatTime.format(note.getFireDate()));
            } else if (note.getTypeNote() == TypeNote.Todo) {
                if (note.getRegularly()) {
                    holder.layoutItemListView.addView(holder.layoutDayOfWeek);
                    ToggleButton[] daysOfWeek = new ToggleButton[7];

                    daysOfWeek[0] = (ToggleButton) holder.layoutDayOfWeek.findViewById(R.id.dayFirst);
                    daysOfWeek[1] = (ToggleButton) holder.layoutDayOfWeek.findViewById(R.id.daySecond);
                    daysOfWeek[2] = (ToggleButton) holder.layoutDayOfWeek.findViewById(R.id.dayThird);
                    daysOfWeek[3] = (ToggleButton) holder.layoutDayOfWeek.findViewById(R.id.dayFourth);
                    daysOfWeek[4] = (ToggleButton) holder.layoutDayOfWeek.findViewById(R.id.dayFifth);
                    daysOfWeek[5] = (ToggleButton) holder.layoutDayOfWeek.findViewById(R.id.daySixth);
                    daysOfWeek[6] = (ToggleButton) holder.layoutDayOfWeek.findViewById(R.id.daySeventh);

                    GregorianCalendar calendar = new GregorianCalendar();
                    final int firstDay = calendar.getFirstDayOfWeek();

                    for (int i = 0; i < 7; i++) {
                        daysOfWeek[i].setText(getShortDayName(i + firstDay));
                        daysOfWeek[i].setTextOn(getShortDayName(i + firstDay));
                        daysOfWeek[i].setTextOff(getShortDayName(i + firstDay));
                        byte day = note.getDaysOfWeek();
                        if (firstDay == 2) {
                            day = (byte) ((byte) (day << (i + 1)) >> 7);
                        } else {
                            if (i == 0) {
                                day = (byte) ((byte) (day << 7) >> 7);
                            } else {
                                day = (byte) ((byte) (day << i) >> 7);
                            }
                        }
                        if (day == -1) {
                            daysOfWeek[i].setChecked(true);
                        }
                        daysOfWeek[i].setEnabled(false);
                    }
                } else {
                    holder.layoutItemListView.addView(holder.layoutDate);
                    DateFormat formatDate = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
                    holder.textViewDate.setText(formatDate.format(note.getFireDate()));
                }
                holder.layoutItemListView.addView(holder.layoutTime);
                DateFormat formatTime = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
                holder.textViewTime.setText(formatTime.format(note.getFireDate()));
            }
            holder.layoutItemListView.addView(holder.ratingBar, holder.layoutItemListView.getChildCount());
            holder.ratingBar.setRating(note.getPriority().getValue());
            holder.opened = holder.layoutItemListView.getHeight();
            holder.layoutItemListView.setMinimumHeight(400);
        } catch (Exception e) {
            Log.v(getClass().getName(), e.getMessage());
        }
    }

    private void initAlphaView(ViewHolder holder, Note note) {
        if (note.getPerformed()) {
            holder.layoutItemListView.setAlpha(0.5f);
        } else {
            holder.layoutItemListView.setAlpha(1.0f);
        }
    }

    private void initTypeNote(ViewHolder holder, Note note) {
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
    }

    private void removeViewInItem(final ViewHolder holder) {
        holder.layoutItemListView.removeView(holder.viewLine);
        holder.layoutItemListView.removeView(holder.textViewBody);
        holder.layoutItemListView.removeView(holder.layoutDate);
        holder.layoutItemListView.removeView(holder.layoutDayOfWeek);
        holder.layoutItemListView.removeView(holder.layoutTime);
        holder.layoutItemListView.removeView(holder.layoutRemindOf);
        holder.layoutItemListView.removeView(holder.ratingBar);
        holder.layoutItemListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                holder.layoutItemListView.setMinimumHeight(holder.closed);
            }
        },500);
    }

    @NonNull
    private ViewHolder getViewHolder(View convertView) {
        ViewHolder holder;
        View viewDown = this.inflater.inflate(R.layout.item_listview_down, null);
        holder = new ViewHolder();
        holder.layoutItemListView = (LinearLayout) convertView.findViewById(R.id.containerItemListView);

        holder.imageView = (ImageView) convertView.findViewById(R.id.imageNote);
        holder.textViewHeader = (TextView) convertView.findViewById(R.id.textViewHeader);
        holder.textViewDescription = (TextView) convertView.findViewById(R.id.textViewDescription);
        holder.layoutTypeNote = (RelativeLayout) convertView.findViewById(R.id.layoutTypeNote);
        holder.textViewTypeNote = (TextView) convertView.findViewById(R.id.textViewTypeNote);
        holder.closed = convertView.getHeight();

        LinearLayout layoutDown = (LinearLayout) viewDown.findViewById(R.id.layoutDown);
        holder.viewLine = viewDown.findViewById(R.id.line);
        layoutDown.removeView(holder.viewLine);
        holder.textViewBody = (TextView) viewDown.findViewById(R.id.textViewBody);
        layoutDown.removeView(holder.textViewBody);
        holder.layoutDate = (RelativeLayout) viewDown.findViewById(R.id.layoutDate);
        layoutDown.removeView(holder.layoutDate);
        holder.textViewDate = (TextView) holder.layoutDate.findViewById(R.id.textViewDate);
        holder.layoutDayOfWeek = (LinearLayout) viewDown.findViewById(R.id.layoutDayOfWeek);
        layoutDown.removeView(holder.layoutDayOfWeek);
        holder.layoutTime = (RelativeLayout) viewDown.findViewById(R.id.layoutTime);
        layoutDown.removeView(holder.layoutTime);
        holder.textViewTime = (TextView) holder.layoutTime.findViewById(R.id.textViewTime);
        holder.layoutRemindOf = (RelativeLayout) viewDown.findViewById(R.id.layoutRemindOf);
        layoutDown.removeView(holder.layoutRemindOf);
        holder.textViewRemindOf = (TextView) holder.layoutRemindOf.findViewById(R.id.textViewRemindOf);
        holder.ratingBar = (AppCompatRatingBar) viewDown.findViewById(R.id.smallRatingBar);
        layoutDown.removeView(holder.ratingBar);
        return holder;
    }

    private void initAlertDialog(final Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("What to do?");
        builder.setItems(new String[]{"Change note", "Delete note"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent intent = new Intent(activity, ChangeNoteActivity.class);
                    intent.putExtra("NoteUUID", note.getUuid());
                    activity.startActivityForResult(intent, 4);
                } else if (which == 1) {
                    noteDao.delete(note);
                    notifyDataSetChanged();
                }
            }
        });
        builder.show();
    }

    public void updateList() {
        this.loadData();
        this.update = true;
        this.notifyDataSetChanged();
    }

    private static String getShortDayName(int day) {
        Calendar c = Calendar.getInstance();
        c.set(2015, 7, 1, 0, 0, 0);
        c.add(Calendar.DAY_OF_MONTH, day);
        String format = String.format("%ta", c);
        if (format.length() > 3) {
            format = format.substring(0, 3);
        }
        return format;
    }*/
}
