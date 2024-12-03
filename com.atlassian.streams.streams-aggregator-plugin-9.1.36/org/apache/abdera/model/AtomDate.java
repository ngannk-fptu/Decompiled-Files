/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AtomDate
implements Cloneable,
Serializable {
    private static final long serialVersionUID = -7062139688635877771L;
    private Date value;
    private static final Pattern PATTERN = Pattern.compile("(\\d{4})(?:-(\\d{2}))?(?:-(\\d{2}))?(?:([Tt])?(?:(\\d{2}))?(?::(\\d{2}))?(?::(\\d{2}))?(?:\\.(\\d{3}))?)?([Zz])?(?:([+-])(\\d{2}):(\\d{2}))?");

    public AtomDate() {
        this(new Date());
    }

    public AtomDate(String value) {
        this(AtomDate.parse(value));
    }

    public AtomDate(Date value) {
        this.value = (Date)value.clone();
    }

    public AtomDate(Calendar value) {
        this(value.getTime());
    }

    public AtomDate(long value) {
        this(new Date(value));
    }

    public String getValue() {
        return AtomDate.format(this.value);
    }

    public AtomDate setValue(String value) {
        this.value = AtomDate.parse(value);
        return this;
    }

    public AtomDate setValue(Date date) {
        this.value = (Date)date.clone();
        return this;
    }

    public AtomDate setValue(Calendar calendar) {
        this.value = calendar.getTime();
        return this;
    }

    public AtomDate setValue(long timestamp) {
        this.value = new Date(timestamp);
        return this;
    }

    public Date getDate() {
        return (Date)this.value.clone();
    }

    public Calendar getCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.value);
        return cal;
    }

    public long getTime() {
        return this.value.getTime();
    }

    public String toString() {
        return this.getValue();
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.value.hashCode();
        return result;
    }

    public boolean equals(Object obj) {
        boolean answer = false;
        if (obj instanceof Date) {
            Date d = (Date)obj;
            answer = this.value.equals(d);
        } else if (obj instanceof String) {
            Date d = AtomDate.parse((String)obj);
            answer = this.value.equals(d);
        } else if (obj instanceof Calendar) {
            Calendar c = (Calendar)obj;
            answer = this.value.equals(c.getTime());
        } else if (obj instanceof AtomDate) {
            Date d = ((AtomDate)obj).value;
            answer = this.value.equals(d);
        }
        return answer;
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError((Object)e);
        }
    }

    public static Date parse(String date) {
        Matcher m = PATTERN.matcher(date);
        if (m.find()) {
            if (m.group(4) == null) {
                throw new IllegalArgumentException("Invalid Date Format");
            }
            Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            int hoff = 0;
            int moff = 0;
            int doff = -1;
            if (m.group(10) != null) {
                doff = m.group(10).equals("-") ? 1 : -1;
                hoff = doff * (m.group(11) != null ? Integer.parseInt(m.group(11)) : 0);
                moff = doff * (m.group(12) != null ? Integer.parseInt(m.group(12)) : 0);
            }
            c.set(1, Integer.parseInt(m.group(1)));
            c.set(2, m.group(2) != null ? Integer.parseInt(m.group(2)) - 1 : 0);
            c.set(5, m.group(3) != null ? Integer.parseInt(m.group(3)) : 1);
            c.set(11, m.group(5) != null ? Integer.parseInt(m.group(5)) + hoff : 0);
            c.set(12, m.group(6) != null ? Integer.parseInt(m.group(6)) + moff : 0);
            c.set(13, m.group(7) != null ? Integer.parseInt(m.group(7)) : 0);
            c.set(14, m.group(8) != null ? Integer.parseInt(m.group(8)) : 0);
            return c.getTime();
        }
        throw new IllegalArgumentException("Invalid Date Format");
    }

    public static String format(Date date) {
        StringBuilder sb = new StringBuilder();
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        c.setTime(date);
        sb.append(c.get(1));
        sb.append('-');
        int f = c.get(2);
        if (f < 9) {
            sb.append('0');
        }
        sb.append(f + 1);
        sb.append('-');
        f = c.get(5);
        if (f < 10) {
            sb.append('0');
        }
        sb.append(f);
        sb.append('T');
        f = c.get(11);
        if (f < 10) {
            sb.append('0');
        }
        sb.append(f);
        sb.append(':');
        f = c.get(12);
        if (f < 10) {
            sb.append('0');
        }
        sb.append(f);
        sb.append(':');
        f = c.get(13);
        if (f < 10) {
            sb.append('0');
        }
        sb.append(f);
        sb.append('.');
        f = c.get(14);
        if (f < 100) {
            sb.append('0');
        }
        if (f < 10) {
            sb.append('0');
        }
        sb.append(f);
        sb.append('Z');
        return sb.toString();
    }

    public static AtomDate valueOf(String value) {
        return new AtomDate(value);
    }

    public static AtomDate valueOf(Date value) {
        return new AtomDate(value);
    }

    public static AtomDate valueOf(Calendar value) {
        return new AtomDate(value);
    }

    public static AtomDate valueOf(long value) {
        return new AtomDate(value);
    }
}

