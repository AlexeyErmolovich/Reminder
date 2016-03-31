package com.alexey.reminder;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
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
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

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

    private final int CAMERA_REQUEST = 0;
    private final int GALLERY_REQUEST = 1;
    private final int CHANGE_IMAGE_REQUEST = 2;

    private boolean draw;
    private Note note;
    private NoteDao noteDao;
    private GregorianCalendar calendar;
    private AppCompatActivity activity;
    private Bitmap bitmapDefault;
    private int currentTypeNote;
    private String mCurrentPhotoPath;

    private CircleImageView imageView;
    private Spinner spinnerTypeNote;
    private EditText editTextHeader;
    private EditText editTextDescription;
    private EditText editTextBody;
    private EditText editTextDate;
    private EditText editTextTime;
    private Spinner spinnerRemindOf;
    private RatingBar ratingBarPriority;

    private LinearLayout layoutFireDate;
    private RelativeLayout layoutRemindOf;
    private RelativeLayout layoutPriority;

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
            draw = false;
            this.note = new Note(UUID.randomUUID().toString(), "", "", "", null, calendar.getTime(), false, 0l,
                    new byte[0], new byte[0], Priority.None, TypeNote.Birthday);
        } else {
            draw = true;
            this.note = this.noteDao.load(uuid);
            this.calendar.setTime(this.note.getFireDate());
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
        initSpinnerRemindOf();
        inirRatingeBarPriority();
        initLayouts();
        initSpinnerTypeNote();
    }

    private void initLayouts() {
        this.layoutFireDate = (LinearLayout) findViewById(R.id.layoutFireDate);
        this.layoutRemindOf = (RelativeLayout) findViewById(R.id.layoutRemindOf);
        this.layoutPriority = (RelativeLayout) findViewById(R.id.layoutPriority);
    }

    private void inirRatingeBarPriority() {
        this.ratingBarPriority = (RatingBar) findViewById(R.id.ratingBarPriority);
        if (draw) {
            this.ratingBarPriority.setRating(this.note.getPriority().getValue());
        }
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
        if (draw) {
            this.spinnerRemindOf.setSelection(remindOfPosition(this.note.getRemindOf()));
        }
    }

    private void initEditTextTime() {
        this.editTextTime = (EditText) findViewById(R.id.editTextFireTime);
        final DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
        if (draw) {
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
                    TimePickerDialog pickerDialog = new TimePickerDialog(activity, timeSetListener,
                            12, 0, true);
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
        if (draw) {
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
        if (draw) {
            this.editTextBody.setText(this.note.getBody());
        }
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
        if (draw) {
            this.editTextDescription.setText(this.note.getDescription());
        }
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
        if (draw) {
            this.editTextHeader.setText(this.note.getHeader());
        }
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
        this.spinnerTypeNote.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == TypeNote.Idea.getValue()) {
                    Display defaultDisplay = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

                    ObjectAnimator animatorFireDate = ObjectAnimator.ofFloat(layoutFireDate, "translationX", 0f,
                            defaultDisplay.getWidth());
                    animatorFireDate.setDuration(500);

                    ObjectAnimator animatorRemindOf = ObjectAnimator.ofFloat(layoutRemindOf, "translationX", 0f,
                            defaultDisplay.getWidth());
                    animatorRemindOf.setDuration(500);

                    animatorFireDate.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            ObjectAnimator animatorPriority = ObjectAnimator.ofFloat(layoutPriority, "translationY", 0f, -220f);
                            animatorPriority.setDuration(500);
                            animatorPriority.start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    animatorFireDate.start();
                    animatorRemindOf.start();
                } else if (currentTypeNote == TypeNote.Idea.getValue()) {
                    ObjectAnimator animatorPriority = ObjectAnimator.ofFloat(layoutPriority, "translationY", -220f, 0f);
                    animatorPriority.setDuration(500);
                    animatorPriority.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            Display defaultDisplay = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

                            ObjectAnimator animatorFireDate = ObjectAnimator.ofFloat(layoutFireDate, "translationX",
                                    defaultDisplay.getWidth(), 0);
                            animatorFireDate.setDuration(500);

                            ObjectAnimator animatorRemindOf = ObjectAnimator.ofFloat(layoutRemindOf, "translationX",
                                    defaultDisplay.getWidth(), 0);
                            animatorRemindOf.setDuration(500);

                            animatorFireDate.start();
                            animatorRemindOf.start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    animatorPriority.start();
                }
                currentTypeNote = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if(this.note.getTypeNote() == TypeNote.Idea){

        }
        if (draw) {
            this.spinnerTypeNote.setSelection(this.note.getTypeNote().getValue(), true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
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
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
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
//                     Ensure that there's a camera activity to handle the intent
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                         Create the File where the photo should go
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
                String desxription = editTextDescription.getText().toString().trim();
                String body = editTextBody.getText().toString().trim();
                String date = editTextDate.getText().toString().trim();
                String time = editTextTime.getText().toString().trim();
                int idRemindOf = spinnerRemindOf.getSelectedItemPosition();
                int priority = (int) ratingBarPriority.getRating();

                if (header.length() == 0) {
                    save = false;
                    editTextHeader.setError(getString(R.string.text_error_header));
                }
                if (desxription.length() == 0) {
                    save = false;
                    editTextDescription.setError(getString(R.string.text_error_description));
                }
                if (body.length() == 0) {
                    save = false;
                    editTextBody.setError(getString(R.string.text_error_body));
                }
                if (typeNote != TypeNote.Idea.getValue()) {
                    if (date.length() == 0) {
                        save = false;
                        editTextDate.setError(getString(R.string.text_error_date));
                    }
                    if (time.length() == 0) {
                        save = false;
                        editTextTime.setError(getString(R.string.text_error_time));
                    }
                }
                if (save) {
                    if (calendar.getTimeInMillis() < note.getFireDate().getTime()) {
                        note.setPerformed(true);
                    } else {
                        note.setPerformed(false);
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
                    note.setDescription(desxription);
                    note.setBody(body);
                    note.setFireDate(calendar.getTime());
                    note.setTimeStamp(new GregorianCalendar().getTime());
                    note.setRemindOf(remindOf[idRemindOf]);
                    note.setPriority(Priority.getValue(priority));
                    if (draw) {
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
        if (note.getTypeNote() != TypeNote.Idea) {
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
        if (draw) {
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
            if (calendar.getTimeInMillis() != note.getFireDate().getTime()) {
                exit = false;
            } else if (remindOf[idRemindOf] != note.getRemindOf()) {
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