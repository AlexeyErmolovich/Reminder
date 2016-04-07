package com.alexey.reminder;

import android.animation.ValueAnimator;
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
import com.alexey.reminder.model.TypeNoteEnum.TypeNote;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * Created by Alexey on 21.03.2016.
 */
public class NoteAdapter extends BaseAdapter {

    private AppCompatActivity activity;
    private List<Note> noteList;
    private NoteDao noteDao;
    private LayoutInflater inflater;
    private boolean update;

    private long[] remindOf = {0, 60000, 300000, 600000, 900000, 1200000, 1500000, 1800000, 2700000, 3600000,
            7200000, 10800000, 14400000, 18000000, 36000000, 540000000, 72000000, 86400000};

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
        this.update = false;
        this.loadData();
    }

    private void loadData() {
        LoadDataAsyncTask loadData = new LoadDataAsyncTask(this.activity);
        loadData.execute();
        try {
            this.noteList = loadData.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        initSortNotes();
    }

    private void initSortNotes() {
        if (this.noteList.size() > 1) {
            Collections.sort(this.noteList, this.comparatorNote);
        }
    }

    static class ViewHolder {
        LinearLayout layoutItemListView;

        ImageView imageView;
        TextView textViewHeader;
        TextView textViewDescription;

        RelativeLayout layoutTypeNote;
        TextView textViewTypeNote;

        View currentView;
        View itemDownBody;
        View itemDownDate;
        View itemDownRegularlyDate;

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
                    holder.opened = holder.currentView.getHeight() - 12;
                    holder.layoutItemListView.setMinimumHeight(holder.opened);
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

    private void initViewDown(final ViewHolder holder, Note note) {
        try {
            if (note.getTypeNote() == TypeNote.Idea) {
                initViewDownBody(holder, note);
            } else if (note.getTypeNote() == TypeNote.Birthday) {
                initViewDownDate(holder, note);
            } else if (note.getTypeNote() == TypeNote.Todo) {
                if (note.getRegularly()) {
                    initViewDownRegularlyDate(holder, note);
                } else {
                    initViewDownDate(holder, note);
                }
            }
        } catch (Exception e) {
            Log.v(getClass().getName(), e.getMessage());
        }
    }

    private void initViewDownRegularlyDate(ViewHolder holder, Note note) {
        LinearLayout layoutDayOfWeek = (LinearLayout) holder.itemDownRegularlyDate.findViewById(R.id.layoutDayOfWeek);
        ToggleButton[] daysOfWeek = new ToggleButton[7];

        daysOfWeek[0] = (ToggleButton) layoutDayOfWeek.findViewById(R.id.dayFirst);
        daysOfWeek[1] = (ToggleButton) layoutDayOfWeek.findViewById(R.id.daySecond);
        daysOfWeek[2] = (ToggleButton) layoutDayOfWeek.findViewById(R.id.dayThird);
        daysOfWeek[3] = (ToggleButton) layoutDayOfWeek.findViewById(R.id.dayFourth);
        daysOfWeek[4] = (ToggleButton) layoutDayOfWeek.findViewById(R.id.dayFifth);
        daysOfWeek[5] = (ToggleButton) layoutDayOfWeek.findViewById(R.id.daySixth);
        daysOfWeek[6] = (ToggleButton) layoutDayOfWeek.findViewById(R.id.daySeventh);

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

        TextView textViewBody = (TextView) holder.itemDownRegularlyDate.findViewById(R.id.textViewBody);
        TextView textViewTime = (TextView) holder.itemDownRegularlyDate.findViewById(R.id.textViewTime);
        TextView textViewRemindOf = (TextView) holder.itemDownRegularlyDate.findViewById(R.id.textViewRemindOf);
        AppCompatRatingBar ratingBar = (AppCompatRatingBar) holder.itemDownRegularlyDate.findViewById(R.id.smallRatingBar);

        DateFormat formatTime = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());

        textViewBody.setText(note.getBody());
        textViewTime.setText(formatTime.format(note.getFireDate()));
        textViewRemindOf.setText(activity.getResources().getStringArray(R.array.remind_of)[remindOfPosition(note.getRemindOf())]);
        ratingBar.setRating(note.getPriority().getValue());
        holder.layoutItemListView.addView(holder.itemDownRegularlyDate);
    }

    private void initViewDownDate(ViewHolder holder, Note note) {
        TextView textViewBody = (TextView) holder.itemDownDate.findViewById(R.id.textViewBody);
        TextView textViewDate = (TextView) holder.itemDownDate.findViewById(R.id.textViewDate);
        TextView textViewTime = (TextView) holder.itemDownDate.findViewById(R.id.textViewTime);
        TextView textViewRemindOf = (TextView) holder.itemDownDate.findViewById(R.id.textViewRemindOf);
        AppCompatRatingBar ratingBar = (AppCompatRatingBar) holder.itemDownDate.findViewById(R.id.smallRatingBar);

        DateFormat formatDate = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        DateFormat formatTime = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());

        textViewBody.setText(note.getBody());
        textViewDate.setText(formatDate.format(note.getFireDate()));
        textViewTime.setText(formatTime.format(note.getFireDate()));
        textViewRemindOf.setText(activity.getResources().getStringArray(R.array.remind_of)[remindOfPosition(note.getRemindOf())]);
        ratingBar.setRating(note.getPriority().getValue());
        holder.layoutItemListView.addView(holder.itemDownDate);
    }

    private void initViewDownBody(ViewHolder holder, Note note) {
        TextView textViewBody = (TextView) holder.itemDownBody.findViewById(R.id.textViewBody);
        AppCompatRatingBar ratingBar = (AppCompatRatingBar) holder.itemDownBody.findViewById(R.id.smallRatingBar);

        textViewBody.setText(note.getBody());
        ratingBar.setRating(note.getPriority().getValue());

        holder.layoutItemListView.addView(holder.itemDownBody);
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
        holder.layoutItemListView.removeView(holder.itemDownBody);
        holder.layoutItemListView.removeView(holder.itemDownDate);
        holder.layoutItemListView.removeView(holder.itemDownRegularlyDate);

        ValueAnimator va = new ValueAnimator().ofInt(holder.opened, 0);
        va.setDuration(700);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                holder.layoutItemListView.setMinimumHeight(value);
                holder.layoutItemListView.requestLayout();
            }
        });
        va.start();

    }

    @NonNull
    private ViewHolder getViewHolder(View convertView) {
        ViewHolder holder;
        holder = new ViewHolder();
        holder.layoutItemListView = (LinearLayout) convertView.findViewById(R.id.containerItemListView);
        holder.imageView = (ImageView) convertView.findViewById(R.id.imageNote);
        holder.textViewHeader = (TextView) convertView.findViewById(R.id.textViewHeader);
        holder.textViewDescription = (TextView) convertView.findViewById(R.id.textViewDescription);
        holder.layoutTypeNote = (RelativeLayout) convertView.findViewById(R.id.layoutTypeNote);
        holder.textViewTypeNote = (TextView) convertView.findViewById(R.id.textViewTypeNote);
        holder.closed = convertView.getHeight();

        holder.currentView = convertView;
        holder.itemDownBody = this.inflater.inflate(R.layout.item_down_body, null);
        holder.itemDownDate = this.inflater.inflate(R.layout.item_down_date, null);
        holder.itemDownRegularlyDate = this.inflater.inflate(R.layout.item_down_regularly_date, null);

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
    }

    private int remindOfPosition(long remindOf) {
        for (int i = 0; i < this.remindOf.length; i++) {
            if (remindOf == this.remindOf[i]) {
                return i;
            }
        }
        return 0;
    }

    public void searchItems(String query) {
        SearchNotesAsyncTask searchNotes = new SearchNotesAsyncTask(activity);
        searchNotes.execute(query);
        try {
            this.noteList = searchNotes.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        initSortNotes();
        this.update = true;
        this.notifyDataSetChanged();
    }
}
