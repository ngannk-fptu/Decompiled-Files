/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonFormat
 *  com.fasterxml.jackson.annotation.JsonFormat$Shape
 */
package org.bedework.util.timezones.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.bedework.util.misc.ToString;

@JsonFormat(shape=JsonFormat.Shape.ARRAY)
public class LocalNameType {
    protected String value;
    protected String lang;
    protected Boolean pref;

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLang() {
        return this.lang;
    }

    public void setLang(String value) {
        this.lang = value;
    }

    public boolean isPref() {
        if (this.pref == null) {
            return false;
        }
        return this.pref;
    }

    public void setPref(Boolean value) {
        this.pref = value;
    }

    public String toString() {
        ToString ts = new ToString(this);
        ts.append("value", this.getValue());
        ts.append("lang", this.getLang());
        ts.append("pref", this.isPref());
        return ts.toString();
    }
}

