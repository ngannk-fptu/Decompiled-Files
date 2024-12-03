/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.model;

import java.util.Calendar;
import java.util.Date;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Element;

public interface DateTime
extends Element {
    public AtomDate getValue();

    public Date getDate();

    public Calendar getCalendar();

    public long getTime();

    public String getString();

    public DateTime setValue(AtomDate var1);

    public DateTime setDate(Date var1);

    public DateTime setCalendar(Calendar var1);

    public DateTime setTime(long var1);

    public DateTime setString(String var1);
}

