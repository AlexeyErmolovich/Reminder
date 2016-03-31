package com.alexey.reminder.model.PriorityEnum;

/**
 * Created by Alexey on 21.03.2016.
 */
public enum Priority {

    None(0),
    VeryLow(1),
    Low(2),
    Middle(3),
    High(4),
    VeryHigh(5);

    private int value;

    Priority(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static Priority getValue(int value){
        if(value==None.getValue()){
            return None;
        }else if(value==VeryLow.getValue()){
            return VeryLow;
        }else if(value==Low.getValue()){
            return Low;
        }else if(value==Middle.getValue()){
            return Middle;
        }else if(value==High.getValue()){
            return High;
        }else if(value==VeryHigh.getValue()){
            return VeryHigh;
        }
        return null;
    }
}
