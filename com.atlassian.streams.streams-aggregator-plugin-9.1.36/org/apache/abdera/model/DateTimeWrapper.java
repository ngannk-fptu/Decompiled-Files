/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.model;

import java.util.Calendar;
import java.util.Date;
import javax.xml.namespace.QName;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.DateTime;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;

public abstract class DateTimeWrapper
extends ElementWrapper
implements DateTime {
    protected DateTimeWrapper(Element internal) {
        super(internal);
    }

    protected DateTimeWrapper(Factory factory, QName qname) {
        super(factory, qname);
    }

    public AtomDate getValue() {
        AtomDate value = null;
        String v = this.getText();
        if (v != null) {
            value = AtomDate.valueOf(v);
        }
        return value;
    }

    public DateTime setValue(AtomDate dateTime) {
        if (dateTime != null) {
            this.setText(dateTime.getValue());
        } else {
            this.setText("");
        }
        return this;
    }

    public DateTime setDate(Date date) {
        if (date != null) {
            this.setText(AtomDate.valueOf(date).getValue());
        } else {
            this.setText("");
        }
        return this;
    }

    public DateTime setCalendar(Calendar date) {
        if (date != null) {
            this.setText(AtomDate.valueOf(date).getValue());
        } else {
            this.setText("");
        }
        return this;
    }

    public DateTime setTime(long date) {
        this.setText(AtomDate.valueOf(date).getValue());
        return this;
    }

    public DateTime setString(String date) {
        if (date != null) {
            this.setText(AtomDate.valueOf(date).getValue());
        } else {
            this.setText("");
        }
        return this;
    }

    public Date getDate() {
        AtomDate ad = this.getValue();
        return ad != null ? ad.getDate() : null;
    }

    public Calendar getCalendar() {
        AtomDate ad = this.getValue();
        return ad != null ? ad.getCalendar() : null;
    }

    public long getTime() {
        AtomDate ad = this.getValue();
        return ad != null ? Long.valueOf(ad.getTime()) : null;
    }

    public String getString() {
        AtomDate ad = this.getValue();
        return ad != null ? ad.getValue() : null;
    }
}

