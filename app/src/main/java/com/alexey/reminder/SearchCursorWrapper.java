package com.alexey.reminder;

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 * Created by Alexey on 11.04.2016.
 */
public class SearchCursorWrapper extends CursorWrapper {

    private String filter;
    private int[] index;
    private int count = 0;
    private int pos = 0;

    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public SearchCursorWrapper(Cursor cursor, String search) {
        super(cursor);
        this.filter = search.toLowerCase();
        if (this.filter != "") {
            this.count = super.getCount();
            this.index = new int[this.count];
            for (int i = 0; i < this.count; i++) {
                super.moveToPosition(i);
                if (this.getString(1).toLowerCase().contains(this.filter))
                    this.index[this.pos++] = i;
                else if (this.getString(2).toLowerCase().contains(this.filter))
                    this.index[this.pos++] = i;
                else if (this.getString(3).toLowerCase().contains(this.filter))
                    this.index[this.pos++] = i;
            }
            this.count = this.pos;
            this.pos = 0;
            super.moveToFirst();
        } else {
            this.count = super.getCount();
            this.index = new int[this.count];
            for (int i = 0; i < this.count; i++) {
                this.index[i] = i;
            }
        }

    }

    @Override
    public boolean move(int offset) {
        return this.moveToPosition(this.pos + offset);
    }

    @Override
    public boolean moveToNext() {
        this.pos++;
        if (this.pos >= this.count) {
            pos--;
            return false;
        }
        return this.moveToPosition(this.pos);
    }

    @Override
    public boolean moveToPrevious() {
        this.pos--;
        if (this.pos < 0) {
            pos++;
            return false;
        }
        return this.moveToPosition(this.pos);
    }

    @Override
    public boolean moveToFirst() {
        return this.moveToPosition(0);
    }

    @Override
    public boolean moveToLast() {
        return this.moveToPosition(this.count - 1);
    }

    @Override
    public boolean moveToPosition(int position) {
        if (position >= this.count || position < 0)
            return false;
        return super.moveToPosition(this.index[position]);
    }

    @Override
    public int getCount() {
        return this.count;
    }

    @Override
    public int getPosition() {
        return this.pos;
    }
}
