/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.types;

import java.io.Serializable;
import java.text.NumberFormat;
import org.apache.axis.utils.Messages;

public class YearMonth
implements Serializable {
    int year;
    int month;
    String timezone = null;

    public YearMonth(int year, int month) throws NumberFormatException {
        this.setValue(year, month);
    }

    public YearMonth(int year, int month, String timezone) throws NumberFormatException {
        this.setValue(year, month, timezone);
    }

    public YearMonth(String source) throws NumberFormatException {
        int negative = 0;
        if (source.charAt(0) == '-') {
            negative = 1;
        }
        if (source.length() < 7 + negative) {
            throw new NumberFormatException(Messages.getMessage("badYearMonth00"));
        }
        int pos = source.substring(negative).indexOf(45);
        if (pos < 0) {
            throw new NumberFormatException(Messages.getMessage("badYearMonth00"));
        }
        if (negative > 0) {
            ++pos;
        }
        this.setValue(Integer.parseInt(source.substring(0, pos)), Integer.parseInt(source.substring(pos + 1, pos + 3)), source.substring(pos + 3));
    }

    public int getYear() {
        return this.year;
    }

    public void setYear(int year) {
        if (year == 0) {
            throw new NumberFormatException(Messages.getMessage("badYearMonth00"));
        }
        this.year = year;
    }

    public int getMonth() {
        return this.month;
    }

    public void setMonth(int month) {
        if (month < 1 || month > 12) {
            throw new NumberFormatException(Messages.getMessage("badYearMonth00"));
        }
        this.month = month;
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

    public void setValue(int year, int month, String timezone) throws NumberFormatException {
        this.setYear(year);
        this.setMonth(month);
        this.setTimezone(timezone);
    }

    public void setValue(int year, int month) throws NumberFormatException {
        this.setYear(year);
        this.setMonth(month);
    }

    public String toString() {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        nf.setMinimumIntegerDigits(4);
        String s = nf.format(this.year) + "-";
        nf.setMinimumIntegerDigits(2);
        s = s + nf.format(this.month);
        if (this.timezone != null) {
            s = s + this.timezone;
        }
        return s;
    }

    public boolean equals(Object obj) {
        boolean equals;
        if (!(obj instanceof YearMonth)) {
            return false;
        }
        YearMonth other = (YearMonth)obj;
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        boolean bl = equals = this.year == other.year && this.month == other.month;
        if (this.timezone != null) {
            equals = equals && this.timezone.equals(other.timezone);
        }
        return equals;
    }

    public int hashCode() {
        return null == this.timezone ? this.month + this.year : this.month + this.year ^ this.timezone.hashCode();
    }
}

