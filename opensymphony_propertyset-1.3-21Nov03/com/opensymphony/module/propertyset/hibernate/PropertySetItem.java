/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.propertyset.hibernate;

import java.io.Serializable;
import java.util.Date;

public class PropertySetItem
implements Serializable {
    Date dateVal;
    String entityName;
    String key;
    String stringVal;
    boolean booleanVal;
    double doubleVal;
    int intVal;
    int type;
    long entityId;
    long longVal;

    public PropertySetItem() {
    }

    public PropertySetItem(String entityName, long entityId, String key) {
        this.entityName = entityName;
        this.entityId = entityId;
        this.key = key;
    }

    public void setBooleanVal(boolean booleanVal) {
        this.booleanVal = booleanVal;
    }

    public boolean getBooleanVal() {
        return this.booleanVal;
    }

    public void setDateVal(Date dateVal) {
        this.dateVal = dateVal;
    }

    public Date getDateVal() {
        return this.dateVal;
    }

    public void setDoubleVal(double doubleVal) {
        this.doubleVal = doubleVal;
    }

    public double getDoubleVal() {
        return this.doubleVal;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public long getEntityId() {
        return this.entityId;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public void setIntVal(int intVal) {
        this.intVal = intVal;
    }

    public int getIntVal() {
        return this.intVal;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public void setLongVal(long longVal) {
        this.longVal = longVal;
    }

    public long getLongVal() {
        return this.longVal;
    }

    public void setStringVal(String stringVal) {
        this.stringVal = stringVal;
    }

    public String getStringVal() {
        return this.stringVal;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof PropertySetItem)) {
            return false;
        }
        PropertySetItem item = (PropertySetItem)obj;
        return item.getEntityId() == this.entityId && item.getEntityName().equals(this.entityName) && item.getKey().equals(this.key);
    }

    public int hashCode() {
        return (int)(this.entityId + (long)this.entityName.hashCode() + (long)this.key.hashCode());
    }
}

