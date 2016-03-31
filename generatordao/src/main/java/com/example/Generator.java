package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class Generator {

    public static void main(String... args) throws Exception {
        Schema schema = new Schema(1, "com.alexey.reminder.model");

        Entity note = schema.addEntity("Note");

        note.addStringProperty("uuid").primaryKey();

        note.addStringProperty("header").notNull();

        note.addStringProperty("description").notNull();

        note.addStringProperty("body").notNull();

        note.addDateProperty("timeStamp").notNull();

        note.addDateProperty("fireDate");

        note.addBooleanProperty("performed").notNull();

        note.addLongProperty("remindOf");

        note.addByteArrayProperty("imageCut");

        note.addByteArrayProperty("image");

        note.addIntProperty("priority")
                .customType("com.alexey.reminder.model.PriorityEnum.Priority", "com.alexey.reminder.model.PriorityEnum.PriorityConverter")
                .notNull();

        note.addIntProperty("typeNote")
                .customType("com.alexey.reminder.model.TypeNoteEnum.TypeNote", "com.alexey.reminder.model.TypeNoteEnum.TypeNoteConverter")
                .notNull();

        new DaoGenerator().generateAll(schema, "../app/src/main/java");
    }

}
