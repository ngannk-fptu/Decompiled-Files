/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package net.fortuna.ical4j.model;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Deprecated
public class UtcOffset
implements Serializable {
    private static final long serialVersionUID = 5883111996721531728L;
    private static final int HOUR_START_INDEX = 1;
    private static final int HOUR_END_INDEX = 3;
    private static final int MINUTE_START_INDEX = 3;
    private static final int MINUTE_END_INDEX = 5;
    private static final int SECOND_START_INDEX = 5;
    private static final int SECOND_END_INDEX = 7;
    private static final NumberFormat HOUR_FORMAT = new DecimalFormat("00");
    private static final NumberFormat MINUTE_FORMAT = new DecimalFormat("00");
    private static final NumberFormat SECOND_FORMAT = new DecimalFormat("00");
    private long offset;

    public UtcOffset(String value) {
        boolean negative;
        if (value.length() < 5) {
            throw new IllegalArgumentException("Invalid UTC offset [" + value + "] - must be of the form: (+/-)HHMM[SS]");
        }
        boolean bl = negative = value.charAt(0) == '-';
        if (!negative && value.charAt(0) != '+') {
            throw new IllegalArgumentException("UTC offset value must be signed");
        }
        this.offset = 0L;
        this.offset += (long)Integer.parseInt(value.substring(1, 3)) * 3600000L;
        this.offset = value.contains(":") ? (this.offset += (long)Integer.parseInt(value.substring(4, 6)) * 60000L) : (this.offset += (long)Integer.parseInt(value.substring(3, 5)) * 60000L);
        if (value.length() == 7) {
            this.offset += (long)Integer.parseInt(value.substring(5, 7)) * 1000L;
        }
        if (negative) {
            this.offset = -this.offset;
        }
    }

    public UtcOffset(long offset) {
        this.offset = (long)Math.floor((double)offset / 1000.0) * 1000L;
    }

    public final String toString() {
        StringBuilder b = new StringBuilder();
        long remainder = Math.abs(this.offset);
        if (this.offset < 0L) {
            b.append('-');
        } else {
            b.append('+');
        }
        b.append(HOUR_FORMAT.format(remainder / 3600000L));
        b.append(MINUTE_FORMAT.format((remainder %= 3600000L) / 60000L));
        if ((remainder %= 60000L) > 0L) {
            b.append(SECOND_FORMAT.format(remainder / 1000L));
        }
        return b.toString();
    }

    public final long getOffset() {
        return this.offset;
    }

    public final boolean equals(Object arg0) {
        if (arg0 instanceof UtcOffset) {
            return this.getOffset() == ((UtcOffset)arg0).getOffset();
        }
        return super.equals(arg0);
    }

    public final int hashCode() {
        return new HashCodeBuilder().append(this.getOffset()).toHashCode();
    }
}

