package com.alexey.reminder.model.PriorityEnum;

import de.greenrobot.dao.converter.PropertyConverter;

/**
 * Created by Alexey on 21.03.2016.
 */
public class PriorityConverter implements PropertyConverter<Priority,Integer>{
    @Override
    public Priority convertToEntityProperty(Integer databaseValue) {
        if(databaseValue!=null){
            for(Priority priority:Priority.values()){
                if(priority.getValue()==databaseValue){
                    return priority;
                }
            }
        }
        return null;
    }

    @Override
    public Integer convertToDatabaseValue(Priority entityProperty) {
        if(entityProperty!=null){
            return entityProperty.getValue();
        }
        return null;
    }
}
