/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.types;

import java.io.Serializable;
import java.text.NumberFormat;
import org.apache.axis.utils.Messages;

public class MonthDay
implements Serializable {
    int month;
    int day;
    String timezone = null;

    public MonthDay(int month, int day) throws NumberFormatException {
        this.setValue(month, day);
    }

    public MonthDay(int month, int day, String timezone) throws NumberFormatException {
        this.setValue(month, day, timezone);
    }

    public MonthDay(String source) throws NumberFormatException {
        if (source.length() < 6) {
            throw new NumberFormatException(Messages.getMessage("badMonthDay00"));
        }
        if (source.charAt(0) != '-' || source.charAt(1) != '-' || source.charAt(4) != '-') {
            throw new NumberFormatException(Messages.getMessage("badMonthDay00"));
        }
        this.setValue(Integer.parseInt(source.substring(2, 4)), Integer.parseInt(source.substring(5, 7)), source.substring(7));
    }

    public int getMonth() {
        return this.month;
    }

    public void setMonth(int month) {
        if (month < 1 || month > 12) {
            throw new NumberFormatException(Messages.getMessage("badMonthDay00"));
        }
        this.month = month;
    }

    public int getDay() {
        return this.day;
    }

    public void setDay(int day) {
        if (day < 1 || day > 31) {
            throw new NumberFormatException(Messages.getMessage("badMonthDay00"));
        }
        if (this.month == 2 && day > 29 || (this.month == 9 || this.month == 4 || this.month == 6 || this.month == 11) && day > 30) {
            throw new NumberFormatException(Messages.getMessage("badMonthDay00"));
        }
        this.day = day;
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

    public void setValue(int month, int day, String timezone) throws NumberFormatException {
        this.setMonth(month);
        this.setDay(day);
        this.setTimezone(timezone);
    }

    public void setValue(int month, int day) throws NumberFormatException {
        this.setMonth(month);
        this.setDay(day);
    }

    public String toString() {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        nf.setMinimumIntegerDigits(2);
        String s = "--" + nf.format(this.month) + "-" + nf.format(this.day);
        if (this.timezone != null) {
            s = s + this.timezone;
        }
        return s;
    }

    public boolean equals(Object obj) {
        boolean equals;
        if (!(obj instanceof MonthDay)) {
            return false;
        }
        MonthDay other = (MonthDay)obj;
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        boolean bl = equals = this.month == other.month && this.day == other.day;
        if (this.timezone != null) {
            equals = equals && this.timezone.equals(other.timezone);
        }
        return equals;
    }

    public int hashCode() {
        return null == this.timezone ? this.month + this.day : this.month + this.day ^ this.timezone.hashCode();
    }
}

