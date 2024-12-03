/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.types;

import java.io.Serializable;
import java.text.NumberFormat;
import org.apache.axis.utils.Messages;

public class Month
implements Serializable {
    int month;
    String timezone = null;

    public Month(int month) throws NumberFormatException {
        this.setValue(month);
    }

    public Month(int month, String timezone) throws NumberFormatException {
        this.setValue(month, timezone);
    }

    public Month(String source) throws NumberFormatException {
        if (source.length() < 6) {
            throw new NumberFormatException(Messages.getMessage("badMonth00"));
        }
        if (source.charAt(0) != '-' || source.charAt(1) != '-' || source.charAt(4) != '-' || source.charAt(5) != '-') {
            throw new NumberFormatException(Messages.getMessage("badMonth00"));
        }
        this.setValue(Integer.parseInt(source.substring(2, 4)), source.substring(6));
    }

    public int getMonth() {
        return this.month;
    }

    public void setMonth(int month) {
        if (month < 1 || month > 12) {
            throw new NumberFormatException(Messages.getMessage("badMonth00"));
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

    public void setValue(int month, String timezone) throws NumberFormatException {
        this.setMonth(month);
        this.setTimezone(timezone);
    }

    public void setValue(int month) throws NumberFormatException {
        this.setMonth(month);
    }

    public String toString() {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        nf.setMinimumIntegerDigits(2);
        String s = "--" + nf.format(this.month) + "--";
        if (this.timezone != null) {
            s = s + this.timezone;
        }
        return s;
    }

    public boolean equals(Object obj) {
        boolean equals;
        if (!(obj instanceof Month)) {
            return false;
        }
        Month other = (Month)obj;
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        boolean bl = equals = this.month == other.month;
        if (this.timezone != null) {
            equals = equals && this.timezone.equals(other.timezone);
        }
        return equals;
    }

    public int hashCode() {
        return null == this.timezone ? this.month : this.month ^ this.timezone.hashCode();
    }
}

