/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.types;

import java.io.Serializable;
import java.text.NumberFormat;
import org.apache.axis.utils.Messages;

public class Year
implements Serializable {
    int year;
    String timezone = null;

    public Year(int year) throws NumberFormatException {
        this.setValue(year);
    }

    public Year(int year, String timezone) throws NumberFormatException {
        this.setValue(year, timezone);
    }

    public Year(String source) throws NumberFormatException {
        int pos;
        int negative = 0;
        if (source.charAt(0) == '-') {
            negative = 1;
        }
        if (source.length() < 4 + negative) {
            throw new NumberFormatException(Messages.getMessage("badYear00"));
        }
        for (pos = 4 + negative; pos < source.length() && Character.isDigit(source.charAt(pos)); ++pos) {
        }
        this.setValue(Integer.parseInt(source.substring(0, pos)), source.substring(pos));
    }

    public int getYear() {
        return this.year;
    }

    public void setYear(int year) {
        if (year == 0) {
            throw new NumberFormatException(Messages.getMessage("badYear00"));
        }
        this.year = year;
    }

    public String getTimezone() {
        return this.timezone;
    }

    public void setTimezone(String timezone) {
        if (timezone != null && timezone.length() > 0) {
            if (timezone.charAt(0) == '+' || timezone.charAt(0) == '-' ? timezone.length() != 6 || !Character.isDigit(timezone.charAt(1)) || !Character.isDigit(timezone.charAt(2)) || timezone.charAt(3) != ':' || !Character.isDigit(timezone.charAt(4)) || !Character.isDigit(timezone.charAt(5)) : !timezone.equals("Z")) {
                throw new NumberFormatException(Messages.getMessage("badTimezone00"));
            }
            this.timezone = timezone;
        }
    }

    public void setValue(int year, String timezone) throws NumberFormatException {
        this.setYear(year);
        this.setTimezone(timezone);
    }

    public void setValue(int year) throws NumberFormatException {
        this.setYear(year);
    }

    public String toString() {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        nf.setMinimumIntegerDigits(4);
        String s = nf.format(this.year);
        if (this.timezone != null) {
            s = s + this.timezone;
        }
        return s;
    }

    public boolean equals(Object obj) {
        boolean equals;
        if (!(obj instanceof Year)) {
            return false;
        }
        Year other = (Year)obj;
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        boolean bl = equals = this.year == other.year;
        if (this.timezone != null) {
            equals = equals && this.timezone.equals(other.timezone);
        }
        return equals;
    }

    public int hashCode() {
        return null == this.timezone ? this.year : this.year ^ this.timezone.hashCode();
    }
}

