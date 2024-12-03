/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.types;

import java.io.Serializable;
import java.text.NumberFormat;
import org.apache.axis.utils.Messages;

public class Day
implements Serializable {
    int day;
    String timezone = null;

    public Day(int day) throws NumberFormatException {
        this.setValue(day);
    }

    public Day(int day, String timezone) throws NumberFormatException {
        this.setValue(day, timezone);
    }

    public Day(String source) throws NumberFormatException {
        if (source.length() < 5) {
            throw new NumberFormatException(Messages.getMessage("badDay00"));
        }
        if (source.charAt(0) != '-' || source.charAt(1) != '-' || source.charAt(2) != '-') {
            throw new NumberFormatException(Messages.getMessage("badDay00"));
        }
        this.setValue(Integer.parseInt(source.substring(3, 5)), source.substring(5));
    }

    public int getDay() {
        return this.day;
    }

    public void setDay(int day) {
        if (day < 1 || day > 31) {
            throw new NumberFormatException(Messages.getMessage("badDay00"));
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

    public void setValue(int day, String timezone) throws NumberFormatException {
        this.setDay(day);
        this.setTimezone(timezone);
    }

    public void setValue(int day) throws NumberFormatException {
        this.setDay(day);
    }

    public String toString() {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        nf.setMinimumIntegerDigits(2);
        String s = "---" + nf.format(this.day);
        if (this.timezone != null) {
            s = s + this.timezone;
        }
        return s;
    }

    public boolean equals(Object obj) {
        boolean equals;
        if (!(obj instanceof Day)) {
            return false;
        }
        Day other = (Day)obj;
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        boolean bl = equals = this.day == other.day;
        if (this.timezone != null) {
            equals = equals && this.timezone.equals(other.timezone);
        }
        return equals;
    }

    public int hashCode() {
        return null == this.timezone ? this.day : this.day ^ this.timezone.hashCode();
    }
}

