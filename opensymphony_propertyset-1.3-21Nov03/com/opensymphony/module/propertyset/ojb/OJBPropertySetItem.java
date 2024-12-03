/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.propertyset.ojb;

import java.io.Serializable;
import java.util.Date;

public class OJBPropertySetItem
implements Serializable {
    Date dateValue;
    String globalKey;
    String itemKey;
    String stringValue;
    byte[] byteValue;
    double doubleValue;
    int itemType;
    long longValue;

    public void setByteValue(byte[] bs) {
        this.byteValue = bs;
    }

    public byte[] getByteValue() {
        return this.byteValue;
    }

    public void setDateValue(Date date) {
        this.dateValue = date;
    }

    public Date getDateValue() {
        return this.dateValue;
    }

    public void setDoubleValue(double d) {
        this.doubleValue = d;
    }

    public double getDoubleValue() {
        return this.doubleValue;
    }

    public void setGlobalKey(String string) {
        this.globalKey = string;
    }

    public String getGlobalKey() {
        return this.globalKey;
    }

    public void setItemKey(String string) {
        this.itemKey = string;
    }

    public String getItemKey() {
        return this.itemKey;
    }

    public void setItemType(int i) {
        this.itemType = i;
    }

    public int getItemType() {
        return this.itemType;
    }

    public void setLongValue(long l) {
        this.longValue = l;
    }

    public long getLongValue() {
        return this.longValue;
    }

    public void setStringValue(String string) {
        this.stringValue = string;
    }

    public String getStringValue() {
        return this.stringValue;
    }
}

