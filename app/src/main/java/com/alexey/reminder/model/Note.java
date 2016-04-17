package com.alexey.reminder.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import com.alexey.reminder.SearchCursorWrapper;
import com.alexey.reminder.model.PriorityEnum.Priority;
import com.alexey.reminder.model.TypeNoteEnum.TypeNote;

import java.util.ArrayList;
import java.util.List;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

/**
 * Entity mapped to table "NOTE".
 */
public class Note {

    private String uuid;
    /**
     * Not-null value.
     */
    private String header;
    /**
     * Not-null value.
     */
    private String description;
    /**
     * Not-null value.
     */
    private String body;
    /**
     * Not-null value.
     */
    private java.util.Date timeStamp;
    private java.util.Date fireDate;
    private Boolean Regularly;
    private Byte DaysOfWeek;
    private boolean performed;
    private Long remindOf;
    private byte[] imageCut;
    private byte[] image;
    private Priority priority;
    private TypeNote typeNote;

    private Bitmap bitmap;
    private int showInfo;

    public Note() {
    }

    public Note(String uuid) {
        this.uuid = uuid;
    }

    public Note(String uuid, String header, String description, String body, java.util.Date timeStamp, java.util.Date fireDate, Boolean Regularly, Byte DaysOfWeek, boolean performed, Long remindOf, byte[] imageCut, byte[] image, Priority priority, TypeNote typeNote) {
        this.uuid = uuid;
        this.header = header;
        this.description = description;
        this.body = body;
        this.timeStamp = timeStamp;
        this.fireDate = fireDate;
        this.Regularly = Regularly;
        this.DaysOfWeek = DaysOfWeek;
        this.performed = performed;
        this.remindOf = remindOf;
        this.imageCut = imageCut;
        this.image = image;
        this.priority = priority;
        this.typeNote = typeNote;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Not-null value.
     */
    public String getHeader() {
        return header;
    }

    /**
     * Not-null value; ensure this value is available before it is saved to the database.
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * Not-null value.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Not-null value; ensure this value is available before it is saved to the database.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Not-null value.
     */
    public String getBody() {
        return body;
    }

    /**
     * Not-null value; ensure this value is available before it is saved to the database.
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Not-null value.
     */
    public java.util.Date getTimeStamp() {
        return timeStamp;
    }

    /**
     * Not-null value; ensure this value is available before it is saved to the database.
     */
    public void setTimeStamp(java.util.Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public java.util.Date getFireDate() {
        return fireDate;
    }

    public void setFireDate(java.util.Date fireDate) {
        this.fireDate = fireDate;
    }

    public Boolean getRegularly() {
        return Regularly;
    }

    public void setRegularly(Boolean Regularly) {
        this.Regularly = Regularly;
    }

    public Byte getDaysOfWeek() {
        return DaysOfWeek;
    }

    public void setDaysOfWeek(Byte DaysOfWeek) {
        this.DaysOfWeek = DaysOfWeek;
    }

    public boolean getPerformed() {
        return performed;
    }

    public void setPerformed(boolean performed) {
        this.performed = performed;
    }

    public Long getRemindOf() {
        return remindOf;
    }

    public void setRemindOf(Long remindOf) {
        this.remindOf = remindOf;
    }

    public byte[] getImageCut() {
        return imageCut;
    }

    public void setImageCut(byte[] imageCut) {
        this.imageCut = imageCut;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public TypeNote getTypeNote() {
        return typeNote;
    }

    public void setTypeNote(TypeNote typeNote) {
        this.typeNote = typeNote;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getShowInfo() {
        return showInfo;
    }

    public void setShowInfo(int showInfo) {
        this.showInfo = showInfo;
    }


    public static List<Note> getNotesInSearch(NoteDao noteDao, String search) {
        List<Note> res = new ArrayList<>();
        String query = "SELECT * FROM NOTE";
        SQLiteDatabase db = noteDao.getDatabase();
        Cursor cursor = db.rawQuery(query, null);
        SearchCursorWrapper cursorWrapper = new SearchCursorWrapper(cursor, search);
        if (cursorWrapper.moveToFirst()) {
            do {
                Note note = noteDao.readEntity(cursorWrapper, 0);
                res.add(note);
            } while (cursorWrapper.moveToNext());
        }
        return res;
    }
}
