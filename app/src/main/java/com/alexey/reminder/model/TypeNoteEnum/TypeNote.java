package com.alexey.reminder.model.TypeNoteEnum;

/**
 * Created by Alexey on 21.03.2016.
 */
public enum TypeNote {

    Birthday(0),
    Todo(1),
    Idea(2);

    private int value;

    TypeNote(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static TypeNote getValue(int value) {
        if (value == Birthday.value) {
            return Birthday;
        } else if (value == Todo.getValue()) {
            return Todo;
        } else if (value == Idea.getValue()) {
            return Idea;
        }
        return null;
    }
}
