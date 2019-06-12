package com.example.demo.model;

import java.util.Map;

/**
 * Created by Chen on 2019/3/4.
 */
public class Field {


    private String fieldName;

    private String fieldCode;

    private boolean selectFlag;

    private boolean timePickerFlag;

    private boolean datePickerFlag;

    private boolean dateTimePickerFlag;

    private boolean queryFlag;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    public boolean isSelectFlag() {
        return selectFlag;
    }

    public void setSelectFlag(boolean selectFlag) {
        this.selectFlag = selectFlag;
    }

    public boolean isTimePickerFlag() {
        return timePickerFlag;
    }

    public void setTimePickerFlag(boolean timePickerFlag) {
        this.timePickerFlag = timePickerFlag;
    }

    public boolean isDatePickerFlag() {
        return datePickerFlag;
    }

    public void setDatePickerFlag(boolean datePickerFlag) {
        this.datePickerFlag = datePickerFlag;
    }

    public boolean isDateTimePickerFlag() {
        return dateTimePickerFlag;
    }

    public void setDateTimePickerFlag(boolean dateTimePickerFlag) {
        this.dateTimePickerFlag = dateTimePickerFlag;
    }

    public boolean isQueryFlag() {
        return queryFlag;
    }

    public void setQueryFlag(boolean queryFlag) {
        this.queryFlag = queryFlag;
    }



    @Override
    public String toString() {
        return "Field{" +
                "fieldName='" + fieldName + '\'' +
                ", fieldCode='" + fieldCode + '\'' +
                ", selectFlag=" + selectFlag +
                ", timePickerFlag=" + timePickerFlag +
                ", datePickerFlag=" + datePickerFlag +
                ", dateTimePickerFlag=" + dateTimePickerFlag +
                ", queryFlag=" + queryFlag +
                '}';
    }
}
