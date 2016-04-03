package com.alexey.reminder;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.alexey.reminder.model.DaoMaster;
import com.alexey.reminder.model.DaoSession;
import com.alexey.reminder.model.Note;
import com.alexey.reminder.model.NoteDao;
import com.alexey.reminder.model.PriorityEnum.Priority;
import com.alexey.reminder.model.TypeNoteEnum.TypeNote;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChangeNoteActivity extends AppCompatActivity {

    /**
     * Request code to launch the camera
     */
    private final int CAMERA_REQUEST = 0;
    /**
     * Request code to launch gallery
     */
    private final int GALLERY_REQUEST = 1;
    /**
     * Request code to launch cropping of images
     */
    private final int CHANGE_IMAGE_REQUEST = 2;

    /**
     * Contains information: change or create a new note
     */
    private boolean revise;
    /**
     * Note
     */
    private Note note;
    /**
     * Database connection
     */
    private NoteDao noteDao;
    /**
     * Calendar: contains fire date for notes
     */
    private GregorianCalendar calendar;
    /**
     * Current activity
     */
    private AppCompatActivity activity;
    /**
     * Standard image for notes
     */
    private Bitmap bitmapDefault;
    /**
     * Current type note
     */
    private int currentTypeNote;
    /**
     * Duration of animation for this activity
     */
    private int duration;
    /**
     * Each bit of this byte contains information, whether a note executed on the corresponding day of the week
     */
    private byte dayOfWeek;
    /**
     * Contains the full path of the image
     */
    private String mCurrentPhotoPath;

    private CircleImageView imageView;
    private Spinner spinnerTypeNote;
    private EditText editTextHeader;
    private EditText editTextDescription;
    private EditText editTextBody;
    private EditText editTextDate;
    private EditText editTextTime;
    private SwitchCompat switchRegularly;
    private LinearLayout layoutDayOfWeek;
    private Spinner spinnerRemindOf;
    private RatingBar ratingBarPriority;

    private LinearLayout layoutFireDate;
    private RelativeLayout layoutRemindOf;
    private RelativeLayout layoutPriority;
    private RelativeLayout layoutRegularly;

    /**
     * Time in milliseconds for how much should be reminded of the implementation notes
     */
    private long[] remindOf = {0, 60000, 300000, 600000, 900000, 12000000, 15000000, 18000000, 27000000, 36000000,
            7200000, 10800000, 14400000, 18000000, 36000000, 540000000, 72000000, 86400000};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_note);
        this.activity = this;

        String uuid = getIntent().getStringExtra("NoteUUID");
        DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(this, "Note", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession session = daoMaster.newSession();
        this.noteDao = session.getNoteDao();

        this.calendar = new GregorianCalendar();

        if (uuid.equals("")) {
            revise = false;
            this.note = new Note(UUID.randomUUID().toString(), "", "", "", null, calendar.getTime(), false, Byte.valueOf("0"), false, 0l,
                    new byte[0], new byte[0], Priority.None, TypeNote.Birthday);
        } else {
            revise = true;
            this.note = this.noteDao.load(uuid);
            this.calendar.setTime(this.note.getFireDate());
            this.dayOfWeek = this.note.getDaysOfWeek();
        }

        initToolBar();
        initToolBarButtonCancel();
        initToolBarButtonSave();
        initImage();
        initEditTextHeader();
        initEditTextDescription();
        initEditTextBody();
        initEditTextDate();
        initEditTextTime();
        initLayoutDayOfWeek();
        initSwitchRegularly();
        initSpinnerRemindOf();
        inirRatingeBarPriority();
        initLayouts();
        initSpinnerTypeNote();
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

    private static String getLongDayName(int day) {
        Calendar c = Calendar.getInstance();
        c.set(2015, 7, 1, 0, 0, 0);
        c.add(Calendar.DAY_OF_MONTH, day);
        return String.format("%tA", c);
    }

    private void initLayoutDayOfWeek() {
        this.layoutDayOfWeek = (LinearLayout) findViewById(R.id.layoutDayOfWeek);
        final TextView[] daysOfWeek = new TextView[7];
        final boolean[] daysOfWeekCheck = new boolean[7];
        final String[] daysOfWeekName = new String[7];

        daysOfWeek[0] = (TextView) findViewById(R.id.dayFirst);
        daysOfWeek[1] = (TextView) findViewById(R.id.daySecond);
        daysOfWeek[2] = (TextView) findViewById(R.id.dayThird);
        daysOfWeek[3] = (TextView) findViewById(R.id.dayFourth);
        daysOfWeek[4] = (TextView) findViewById(R.id.dayFifth);
        daysOfWeek[5] = (TextView) findViewById(R.id.daySixth);
        daysOfWeek[6] = (TextView) findViewById(R.id.daySeventh);

        int firstDay = calendar.getFirstDayOfWeek();

        for (int i = 0; i < 7; i++) {
            daysOfWeek[i].setText(getShortDayName(i + firstDay));
            daysOfWeekName[i] = getLongDayName(i + firstDay);
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
                daysOfWeek[i].setBackgroundColor(getResources().getColor(R.color.colorAccent));
                daysOfWeekCheck[i] = true;
            }
        }

        this.layoutDayOfWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMultiChoiceItems(daysOfWeekName, daysOfWeekCheck, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        daysOfWeekCheck[which] = isChecked;
                        if (isChecked) {
                            daysOfWeek[which].setBackgroundColor(activity.getResources().getColor(R.color.colorAccent));
                        } else {
                            daysOfWeek[which].setBackgroundColor(activity.getResources().getColor(R.color.colorWhite));
                        }
                    }
                });
                builder.setPositiveButton(getString(R.string.text_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        byte days = 0;
                        int firstDay = calendar.getFirstDayOfWeek();
                        for (int i = 0; i < 7; i++) {
                            if (daysOfWeekCheck[i]) {
                                if (firstDay == 2) {
                                    days = (byte) (days + Math.pow(2, 6 - i));
                                } else {
                                    if (i == 0) {
                                        days = (byte) (days + Math.pow(2, 0));
                                    } else {
                                        days = (byte) (days + Math.pow(2, 5 - i));
                                    }
                                }
                            }
                        }
                        note.setDaysOfWeek(days);
                    }
                });
                builder.show();
            }
        });
    }

    private void initSwitchRegularly() {
        this.switchRegularly = (SwitchCompat) findViewById(R.id.switchRegularly);
        this.switchRegularly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editTextDate.setEnabled(false);
                    editTextDate.setAlpha(0.5f);
                    layoutDayOfWeek.setEnabled(true);
                    layoutDayOfWeek.setAlpha(1f);
                } else {
                    editTextDate.setEnabled(true);
                    editTextDate.setAlpha(1f);
                    layoutDayOfWeek.setEnabled(false);
                    layoutDayOfWeek.setAlpha(0.5f);
                }
                note.setRegularly(isChecked);
            }
        });
        this.switchRegularly.setChecked(this.note.getRegularly());
        if (this.note.getRegularly()) {
            editTextDate.setEnabled(false);
            editTextDate.setAlpha(0.5f);
        } else {
            layoutDayOfWeek.setEnabled(false);
            layoutDayOfWeek.setAlpha(0.5f);
        }
    }

    private void initLayouts() {
        this.layoutFireDate = (LinearLayout) findViewById(R.id.layoutFireDate);
        this.layoutRemindOf = (RelativeLayout) findViewById(R.id.layoutRemindOf);
        this.layoutPriority = (RelativeLayout) findViewById(R.id.layoutPriority);
        this.layoutRegularly = (RelativeLayout) findViewById(R.id.layoutRegularly);
    }

    private void inirRatingeBarPriority() {
        this.ratingBarPriority = (RatingBar) findViewById(R.id.ratingBarPriority);
        this.ratingBarPriority.setRating(this.note.getPriority().getValue());
    }

    private int remindOfPosition(long remindOf) {
        for (int i = 0; i < this.remindOf.length; i++) {
            if (remindOf == this.remindOf[i]) {
                return i;
            }
        }
        return 0;
    }

    private void initSpinnerRemindOf() {
        this.spinnerRemindOf = (Spinner) findViewById(R.id.spinerRemindOf);
        this.spinnerRemindOf.setSelection(remindOfPosition(this.note.getRemindOf()));
    }

    private void initEditTextTime() {
        this.editTextTime = (EditText) findViewById(R.id.editTextFireTime);
        final DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
        if (revise) {
            this.editTextTime.setText(format.format(this.note.getFireDate()));
        }
        final TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                editTextTime.setText(format.format(calendar.getTime()));
                editTextTime.clearFocus();
            }
        };
        this.editTextTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Locale aDefault = Locale.getDefault();
                    TimePickerDialog pickerDialog;
                    if (aDefault.getCountry().equals("RU")) {
                        pickerDialog = new TimePickerDialog(activity, timeSetListener,
                                12, 0, true);
                    } else {
                        pickerDialog = new TimePickerDialog(activity, timeSetListener,
                                12, 0, false);
                    }
                    pickerDialog.show();
                }
            }
        });
        this.editTextTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    editTextTime.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void initEditTextDate() {
        this.editTextDate = (EditText) findViewById(R.id.editTextFireDate);
        final DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        if (revise) {
            this.editTextDate.setText(format.format(this.note.getFireDate()));
        }
        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(year, monthOfYear, dayOfMonth);

                editTextDate.setText(format.format(calendar.getTime()));
                editTextDate.clearFocus();
            }
        };
        this.editTextDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DatePickerDialog pickerDialog = new DatePickerDialog(activity, dateSetListener,
                            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) + 1);
                    pickerDialog.show();
                }
            }
        });
        this.editTextDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    editTextDate.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initEditTextBody() {
        this.editTextBody = (EditText) findViewById(R.id.editTextBody);
        this.editTextBody.setText(this.note.getBody());
        this.editTextBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    editTextBody.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initEditTextDescription() {
        this.editTextDescription = (EditText) findViewById(R.id.editTextDescription);
        this.editTextDescription.setText(this.note.getDescription());
        this.editTextDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    editTextDescription.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initEditTextHeader() {
        this.editTextHeader = (EditText) findViewById(R.id.editTextHeader);
        this.editTextHeader.setText(this.note.getHeader());
        this.editTextHeader.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    editTextHeader.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initSpinnerTypeNote() {
        this.spinnerTypeNote = (Spinner) findViewById(R.id.spinerTypeNote);

        final Display defaultDisplay = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        this.currentTypeNote = -1;
        this.spinnerTypeNote.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == TypeNote.Birthday.getValue()) {
                    if (currentTypeNote == TypeNote.Todo.getValue()) {
                        ObjectAnimator animatorRegularlyHide = ObjectAnimator.ofFloat(layoutRegularly, "translationX", 0f, defaultDisplay.getWidth());

                        animatorRegularlyHide.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                ObjectAnimator animatorPriorityUp = ObjectAnimator.ofFloat(layoutPriority, "translationY", 0f, -110f);
                                ObjectAnimator animatorRemindOfUp = ObjectAnimator.ofFloat(layoutRemindOf, "translationY", 0f, -110f);
                                ObjectAnimator animatorFireDateUp = ObjectAnimator.ofFloat(layoutFireDate, "translationY", 0f, -110f);

                                animatorPriorityUp.setDuration(duration);
                                animatorRemindOfUp.setDuration(duration);
                                animatorFireDateUp.setDuration(duration);

                                animatorPriorityUp.start();
                                animatorRemindOfUp.start();
                                animatorFireDateUp.start();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });

                        animatorRegularlyHide.setDuration(duration);

                        animatorRegularlyHide.start();

                        if (note.getRegularly()) {
                            editTextDate.setEnabled(true);
                            editTextDate.setAlpha(1f);
                        }
                    } else if (currentTypeNote == TypeNote.Idea.getValue()) {
                        ObjectAnimator animatorPriorityDown = ObjectAnimator.ofFloat(layoutPriority, "translationY", -330f, -110f);
                        ObjectAnimator animatorFireDateShow = ObjectAnimator.ofFloat(layoutFireDate, "translationY", 0f, -110f);
                        ObjectAnimator animatorRemindOfShow = ObjectAnimator.ofFloat(layoutRemindOf, "translationY", 0f, -110f);

                        animatorFireDateShow.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                ObjectAnimator animatorFireDateShow = ObjectAnimator.ofFloat(layoutFireDate, "translationX", defaultDisplay.getWidth(), 0);
                                ObjectAnimator animatorRemindOfShow = ObjectAnimator.ofFloat(layoutRemindOf, "translationX", defaultDisplay.getWidth(), 0);

                                animatorFireDateShow.setDuration(duration);
                                animatorRemindOfShow.setDuration(duration);

                                animatorFireDateShow.start();
                                animatorRemindOfShow.start();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });

                        animatorPriorityDown.setDuration(duration);
                        animatorFireDateShow.setDuration(duration);
                        animatorRemindOfShow.setDuration(duration);

                        animatorFireDateShow.start();
                        animatorRemindOfShow.start();
                        animatorPriorityDown.start();

                        if (note.getRegularly()) {
                            editTextDate.setEnabled(true);
                            editTextDate.setAlpha(1f);
                        }
                    }
                } else if (position == TypeNote.Todo.getValue()) {
                    if (currentTypeNote == TypeNote.Birthday.getValue()) {
                        ObjectAnimator animatorFireDateDown = ObjectAnimator.ofFloat(layoutFireDate, "translationY", -110f, 0f);
                        ObjectAnimator animatorRemindOfDown = ObjectAnimator.ofFloat(layoutRemindOf, "translationY", -110f, 0f);
                        ObjectAnimator animatorPriorityDown = ObjectAnimator.ofFloat(layoutPriority, "translationY", -110f, 0f);

                        animatorFireDateDown.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                ObjectAnimator animatorRegularlyShow = ObjectAnimator.ofFloat(layoutRegularly, "translationX", defaultDisplay.getWidth(), 0f);
                                animatorRegularlyShow.setDuration(duration);
                                animatorRegularlyShow.start();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });

                        animatorFireDateDown.setDuration(duration);
                        animatorRemindOfDown.setDuration(duration);
                        animatorPriorityDown.setDuration(duration);

                        animatorFireDateDown.start();
                        animatorRemindOfDown.start();
                        animatorPriorityDown.start();

                        if (note.getRegularly()) {
                            editTextDate.setEnabled(false);
                            editTextDate.setAlpha(0.5f);
                        }

                    } else if (currentTypeNote == TypeNote.Idea.getValue()) {
                        ObjectAnimator animatorPriorityDown = ObjectAnimator.ofFloat(layoutPriority, "translationY", -330f, 0f);
                        ObjectAnimator animatorFireDateDown = ObjectAnimator.ofFloat(layoutFireDate, "translationY", -110f, 0);
                        ObjectAnimator animatorRemindOfDown = ObjectAnimator.ofFloat(layoutRemindOf, "translationY", -110f, 0);

                        animatorFireDateDown.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                ObjectAnimator animatorRegularlyShow = ObjectAnimator.ofFloat(layoutRegularly, "translationX", defaultDisplay.getWidth(), 0f);
                                ObjectAnimator animatorFireDateShow = ObjectAnimator.ofFloat(layoutFireDate, "translationX", defaultDisplay.getWidth(), 0);
                                ObjectAnimator animatorRemindOfShow = ObjectAnimator.ofFloat(layoutRemindOf, "translationX", defaultDisplay.getWidth(), 0);

                                animatorRegularlyShow.setDuration(duration);
                                animatorFireDateShow.setDuration(duration);
                                animatorRemindOfShow.setDuration(duration);

                                animatorFireDateShow.start();
                                animatorRemindOfShow.start();
                                animatorRegularlyShow.start();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });

                        animatorPriorityDown.setDuration(duration);
                        animatorFireDateDown.setDuration(duration);
                        animatorRemindOfDown.setDuration(duration);

                        animatorPriorityDown.start();
                        animatorFireDateDown.start();
                        animatorRemindOfDown.start();

                        if (note.getRegularly()) {
                            editTextDate.setEnabled(false);
                            editTextDate.setAlpha(0.5f);
                        }
                    }
                } else if (position == TypeNote.Idea.getValue()) {
                    if (currentTypeNote == TypeNote.Birthday.getValue()) {
                        ObjectAnimator animatorFireDateHide = ObjectAnimator.ofFloat(layoutFireDate, "translationX", 0f, defaultDisplay.getWidth());
                        ObjectAnimator animatorRemindOfHide = ObjectAnimator.ofFloat(layoutRemindOf, "translationX", 0f, defaultDisplay.getWidth());

                        animatorFireDateHide.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                ObjectAnimator animatorPriorityUp = ObjectAnimator.ofFloat(layoutPriority, "translationY", -110f, -330f);

                                animatorPriorityUp.setDuration(duration);

                                animatorPriorityUp.start();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });

                        animatorFireDateHide.setDuration(duration);
                        animatorRemindOfHide.setDuration(duration);

                        animatorFireDateHide.start();
                        animatorRemindOfHide.start();
                    } else if (currentTypeNote == TypeNote.Todo.getValue()) {
                        ObjectAnimator animatorFireDateHide = ObjectAnimator.ofFloat(layoutFireDate, "translationX", 0f, defaultDisplay.getWidth());
                        ObjectAnimator animatorRemindOfHide = ObjectAnimator.ofFloat(layoutRemindOf, "translationX", 0f, defaultDisplay.getWidth());
                        ObjectAnimator animatorRegularlyHide = ObjectAnimator.ofFloat(layoutRegularly, "translationX", 0f, defaultDisplay.getWidth());

                        animatorRegularlyHide.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                ObjectAnimator animatorPriorityUp = ObjectAnimator.ofFloat(layoutPriority, "translationY", 0f, -330f);

                                animatorPriorityUp.setDuration(duration);

                                animatorPriorityUp.start();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });

                        animatorRegularlyHide.setDuration(duration);
                        animatorFireDateHide.setDuration(duration);
                        animatorRemindOfHide.setDuration(duration);

                        animatorRegularlyHide.start();
                        animatorFireDateHide.start();
                        animatorRemindOfHide.start();
                    }
                }
                currentTypeNote = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.duration = 0;
        if (this.note.getTypeNote() == TypeNote.Idea) {
            ObjectAnimator animatorFireDateHide = ObjectAnimator.ofFloat(layoutFireDate, "translationX", 0f, defaultDisplay.getWidth());
            ObjectAnimator animatorRemindOfHide = ObjectAnimator.ofFloat(layoutRemindOf, "translationX", 0f, defaultDisplay.getWidth());
            ObjectAnimator animatorRegularlyHide = ObjectAnimator.ofFloat(layoutRegularly, "translationX", 0f, defaultDisplay.getWidth());
            ObjectAnimator animatorPriorityUp = ObjectAnimator.ofFloat(layoutPriority, "translationY", 0f, -330f);
            ObjectAnimator animatorFireDateUp = ObjectAnimator.ofFloat(layoutFireDate, "translationY", 0, -110f);
            ObjectAnimator animatorRemindOfUp = ObjectAnimator.ofFloat(layoutRemindOf, "translationY", 0, -110f);

            animatorPriorityUp.setDuration(duration);
            animatorRegularlyHide.setDuration(duration);
            animatorFireDateHide.setDuration(duration);
            animatorRemindOfHide.setDuration(duration);
            animatorFireDateUp.setDuration(duration);
            animatorRemindOfUp.setDuration(duration);

            animatorPriorityUp.start();
            animatorRegularlyHide.start();
            animatorFireDateHide.start();
            animatorRemindOfHide.start();
            animatorFireDateUp.start();
            animatorRemindOfUp.start();
        } else if (this.note.getTypeNote() == TypeNote.Birthday) {
            ObjectAnimator animatorRegularlyHide = ObjectAnimator.ofFloat(layoutRegularly, "translationX", 0f, defaultDisplay.getWidth());
            ObjectAnimator animatorPriorityUp = ObjectAnimator.ofFloat(layoutPriority, "translationY", 0f, -110f);
            ObjectAnimator animatorRemindOfUp = ObjectAnimator.ofFloat(layoutRemindOf, "translationY", 0f, -110f);
            ObjectAnimator animatorFireDateUp = ObjectAnimator.ofFloat(layoutFireDate, "translationY", 0f, -110f);

            animatorPriorityUp.setDuration(duration);
            animatorRemindOfUp.setDuration(duration);
            animatorFireDateUp.setDuration(duration);
            animatorRegularlyHide.setDuration(duration);

            animatorPriorityUp.start();
            animatorRemindOfUp.start();
            animatorFireDateUp.start();
            animatorRegularlyHide.start();
        }
        this.duration = 500;
        this.spinnerTypeNote.setSelection(this.note.getTypeNote().getValue(), true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap;
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                Uri uriImage = data.getData();
                mCurrentPhotoPath = getRealPathFromURI(uriImage);
                bitmap = setPic();
                note.setBitmap(bitmap);
                initChangeImageIntent(bitmap);
            } else if (requestCode == CAMERA_REQUEST) {
                bitmap = setPic();
                note.setBitmap(bitmap);
                initChangeImageIntent(bitmap);
            } else if (requestCode == CHANGE_IMAGE_REQUEST) {
                byte[] image = data.getByteArrayExtra("data");
                bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File image = new File(Environment.getExternalStorageDirectory(),
                imageFileName + ".jpg");

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private void initChangeImageIntent(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        Intent changeImageIntent = new Intent(getApplicationContext(), CropperActivity.class);
        byte[] byteArray = stream.toByteArray();

        changeImageIntent.putExtra("image", byteArray);
        startActivityForResult(changeImageIntent, CHANGE_IMAGE_REQUEST);
    }

    private Bitmap setPic() {
        // Get the dimensions of the View
        Display defaultDisplay = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int targetW = defaultDisplay.getWidth() / 5;
        int targetH = defaultDisplay.getHeight() / 5;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
    }

    private void initAlertDialogImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(getString(R.string.title_alertdialog_image));

        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(photoFile));
                            startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                        }
                    }
                } else if (which == 1) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, GALLERY_REQUEST);
                } else if (which == 2) {
                    initChangeImageIntent(note.getBitmap());
                }
            }
        };

        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        if (!bitmapDefault.equals(bitmap)) {
            builder.setItems(R.array.load_image_and_change, clickListener);
        } else {
            builder.setItems(R.array.load_image, clickListener);
        }

        builder.show();

    }

    private void initImage() {
        this.imageView = (CircleImageView) findViewById(R.id.imageNote);
        if (this.note.getImage().length != 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(this.note.getImage(), 0, this.note.getImage().length);
            this.note.setBitmap(bitmap);
            bitmap = BitmapFactory.decodeByteArray(this.note.getImageCut(), 0, this.note.getImageCut().length);
            this.imageView.setImageBitmap(bitmap);
        }
        this.bitmapDefault = ((BitmapDrawable) getResources().getDrawable(R.drawable.account_circle)).getBitmap();
        this.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAlertDialogImage();
            }
        });
    }

    private void initToolBarButtonSave() {
        Button save = (Button) findViewById(R.id.toolBarSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean save = true;
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                int typeNote = spinnerTypeNote.getSelectedItemPosition();
                String header = editTextHeader.getText().toString().trim();
                String description = editTextDescription.getText().toString().trim();
                String body = editTextBody.getText().toString().trim();
                boolean regularly = switchRegularly.isChecked();
                String date = editTextDate.getText().toString().trim();
                String time = editTextTime.getText().toString().trim();
                int idRemindOf = spinnerRemindOf.getSelectedItemPosition();
                int priority = (int) ratingBarPriority.getRating();

                if (header.length() == 0) {
                    save = false;
                    editTextHeader.setError(getString(R.string.text_error_header));
                }
                if (description.length() == 0) {
                    save = false;
                    editTextDescription.setError(getString(R.string.text_error_description));
                }
                if (body.length() == 0) {
                    save = false;
                    editTextBody.setError(getString(R.string.text_error_body));
                }
                if (typeNote != TypeNote.Idea.getValue()) {
                    if (typeNote == TypeNote.Todo.getValue()) {
                        if (!regularly) {
                            if (date.length() == 0) {
                                save = false;
                                editTextDate.setError(getString(R.string.text_error_date));
                            }
                        }
                    } else {
                        if (date.length() == 0) {
                            save = false;
                            editTextDate.setError(getString(R.string.text_error_date));
                        }
                    }
                    if (time.length() == 0) {
                        save = false;
                        editTextTime.setError(getString(R.string.text_error_time));
                    }
                }
                if (save) {
                    if (typeNote == TypeNote.Birthday.getValue()) {
                        if (note.getTimeStamp().getTime() > note.getFireDate().getTime()) {
                            note.setPerformed(true);
                        } else {
                            note.setPerformed(false);
                        }
                    } else if (typeNote == TypeNote.Todo.getValue()) {
                        if (regularly) {
                            note.setPerformed(false);
                        } else {
                            if (note.getTimeStamp().getTime() > note.getFireDate().getTime()) {
                                note.setPerformed(true);
                            } else {
                                note.setPerformed(false);
                            }
                        }
                    }
                    if (bitmapDefault.equals(bitmap)) {
                        note.setImage(new byte[0]);
                        note.setImageCut(new byte[0]);
                    } else {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        note.setImageCut(stream.toByteArray());
                        stream = new ByteArrayOutputStream();
                        note.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        note.setImage(stream.toByteArray());
                    }
                    note.setTypeNote(TypeNote.getValue(typeNote));
                    note.setHeader(header);
                    note.setDescription(description);
                    note.setBody(body);
                    note.setRegularly(regularly);
                    note.setFireDate(calendar.getTime());
                    note.setTimeStamp(new GregorianCalendar().getTime());
                    note.setRemindOf(remindOf[idRemindOf]);
                    note.setPriority(Priority.getValue(priority));
                    if (revise) {
                        noteDao.update(note);
                    } else {
                        noteDao.insert(note);
                    }

                    initAlarmNotification();
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    private void initAlarmNotification() {
        if (note.getTypeNote() == TypeNote.Birthday) {
            if (!note.getPerformed()) {
                Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
                alarmIntent.putExtra("NoteUUID", note.getUuid());

                final int delay = 60000;
                long triggerAtMillis = note.getFireDate().getTime() - note.getRemindOf() - delay;
                if (triggerAtMillis < note.getTimeStamp().getTime()) {
                    triggerAtMillis = note.getFireDate().getTime();
                    alarmIntent.putExtra("remind", false);
                } else {
                    triggerAtMillis = note.getFireDate().getTime() - note.getRemindOf();
                    alarmIntent.putExtra("remind", true);
                }
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), note.getUuid().hashCode(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            }
        } else if (note.getTypeNote() == TypeNote.Todo) {
            if (!note.getPerformed()) {
                Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
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
                            if (daysOfWeekCheck[i] && note.getTimeStamp().getTime() < calendar.getTimeInMillis()) {
                                break;
                            } else {
                                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
                            }
                        }
                        if (i == 6) {
                            for (int j = 0; i < currentDayOfWeek; j++) {
                                if (daysOfWeekCheck[j]) {
                                    break;
                                } else {
                                    calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
                                }
                            }
                        }
                    }

                    triggerAtMillis = calendar.getTimeInMillis() - note.getRemindOf() - delay;
                    if (triggerAtMillis < note.getTimeStamp().getTime()) {
                        triggerAtMillis = calendar.getTimeInMillis();
                        alarmIntent.putExtra("remind", false);
                    } else {
                        triggerAtMillis = calendar.getTimeInMillis() - note.getRemindOf();
                        alarmIntent.putExtra("remind", true);
                    }

                } else {
                    triggerAtMillis = note.getFireDate().getTime() - note.getRemindOf() - delay;
                    if (triggerAtMillis < note.getTimeStamp().getTime()) {
                        triggerAtMillis = note.getFireDate().getTime();
                        alarmIntent.putExtra("remind", false);
                    } else {
                        triggerAtMillis = note.getFireDate().getTime() - note.getRemindOf();
                        alarmIntent.putExtra("remind", true);
                    }
                }
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), note.getUuid().hashCode(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            }
        }
    }

    private void initToolBarButtonCancel() {
        Button cancel = (Button) findViewById(R.id.toolBarCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initToolBar() {
        TextView title = (TextView) findViewById(R.id.toolBarTitle);
        if (revise) {
            title.setText(R.string.revise_note);
        } else {
            title.setText(R.string.new_note);
        }
    }

    @Override
    public void onBackPressed() {
        boolean exit = true;
        int typeNote = spinnerTypeNote.getSelectedItemPosition();
        String header = editTextHeader.getText().toString();
        String description = editTextDescription.getText().toString();
        String body = editTextBody.getText().toString();

        int idRemindOf = spinnerRemindOf.getSelectedItemPosition();
        float priority = ratingBarPriority.getRating();

        boolean regularly = switchRegularly.isChecked();

        if (typeNote != note.getTypeNote().getValue()) {
            exit = false;
        } else if (!header.equals(note.getHeader())) {
            exit = false;
        } else if (!description.equals(note.getDescription())) {
            exit = false;
        } else if (!body.equals(note.getBody())) {
            exit = false;
        } else if (priority != note.getPriority().getValue()) {
            exit = false;
        }
        if (typeNote != TypeNote.Idea.getValue()) {
            if (remindOf[idRemindOf] != note.getRemindOf()) {
                exit = false;
            }
            if (typeNote == TypeNote.Todo.getValue()) {
                if (regularly) {
                    if (this.note.getDaysOfWeek() != this.dayOfWeek) {
                        exit = false;
                    }
                } else {
                    if (calendar.getTimeInMillis() != note.getFireDate().getTime()) {
                        exit = false;
                    }
                }
            } else {
                if (calendar.getTimeInMillis() != note.getFireDate().getTime()) {
                    exit = false;
                }
            }

            if (!exit) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.question_save));
                builder.setNegativeButton(getString(R.string.text_no), null);
                builder.setPositiveButton(getString(R.string.text_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                });
                builder.create().show();
            } else {
                finish();
            }
        }
    }
}