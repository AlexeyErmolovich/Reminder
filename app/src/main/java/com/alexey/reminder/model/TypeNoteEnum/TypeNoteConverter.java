package com.alexey.reminder.model.TypeNoteEnum;

import de.greenrobot.dao.converter.PropertyConverter;

/**
 * Created by Alexey on 21.03.2016.
 */
public class TypeNoteConverter implements PropertyConverter<TypeNote, Integer> {
    @Override
    public TypeNote convertToEntityProperty(Integer databaseValue) {
        if (databaseValue != null) {
            for (TypeNote typeNote : TypeNote.values()) {
                if (typeNote.getValue() == databaseValue) {
                    return typeNote;
                }
            }
        }
        return null;
    }

    @Override
    public Integer convertToDatabaseValue(TypeNote entityProperty) {
        if (entityProperty != null) {
            return entityProperty.getValue();
        }
        return null;
    }
}
