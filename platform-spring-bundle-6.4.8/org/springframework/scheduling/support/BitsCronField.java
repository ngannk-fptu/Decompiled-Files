/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scheduling.support;

import java.time.DateTimeException;
import java.time.temporal.Temporal;
import java.time.temporal.ValueRange;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.support.CronField;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

final class BitsCronField
extends CronField {
    private static final long MASK = -1L;
    @Nullable
    private static BitsCronField zeroNanos = null;
    private long bits;

    private BitsCronField(CronField.Type type) {
        super(type);
    }

    public static BitsCronField zeroNanos() {
        if (zeroNanos == null) {
            BitsCronField field = new BitsCronField(CronField.Type.NANO);
            field.setBit(0);
            zeroNanos = field;
        }
        return zeroNanos;
    }

    public static BitsCronField parseSeconds(String value) {
        return BitsCronField.parseField(value, CronField.Type.SECOND);
    }

    public static BitsCronField parseMinutes(String value) {
        return BitsCronField.parseField(value, CronField.Type.MINUTE);
    }

    public static BitsCronField parseHours(String value) {
        return BitsCronField.parseField(value, CronField.Type.HOUR);
    }

    public static BitsCronField parseDaysOfMonth(String value) {
        return BitsCronField.parseDate(value, CronField.Type.DAY_OF_MONTH);
    }

    public static BitsCronField parseMonth(String value) {
        return BitsCronField.parseField(value, CronField.Type.MONTH);
    }

    public static BitsCronField parseDaysOfWeek(String value) {
        BitsCronField result = BitsCronField.parseDate(value, CronField.Type.DAY_OF_WEEK);
        if (result.getBit(0)) {
            result.setBit(7);
            result.clearBit(0);
        }
        return result;
    }

    private static BitsCronField parseDate(String value, CronField.Type type) {
        if (value.equals("?")) {
            value = "*";
        }
        return BitsCronField.parseField(value, type);
    }

    private static BitsCronField parseField(String value, CronField.Type type) {
        Assert.hasLength(value, "Value must not be empty");
        Assert.notNull((Object)type, "Type must not be null");
        try {
            String[] fields;
            BitsCronField result = new BitsCronField(type);
            for (String field : fields = StringUtils.delimitedListToStringArray(value, ",")) {
                int delta;
                int slashPos = field.indexOf(47);
                if (slashPos == -1) {
                    ValueRange range = BitsCronField.parseRange(field, type);
                    result.setBits(range);
                    continue;
                }
                String rangeStr = field.substring(0, slashPos);
                String deltaStr = field.substring(slashPos + 1);
                ValueRange range = BitsCronField.parseRange(rangeStr, type);
                if (rangeStr.indexOf(45) == -1) {
                    range = ValueRange.of(range.getMinimum(), type.range().getMaximum());
                }
                if ((delta = Integer.parseInt(deltaStr)) <= 0) {
                    throw new IllegalArgumentException("Incrementer delta must be 1 or higher");
                }
                result.setBits(range, delta);
            }
            return result;
        }
        catch (IllegalArgumentException | DateTimeException ex) {
            String msg = ex.getMessage() + " '" + value + "'";
            throw new IllegalArgumentException(msg, ex);
        }
    }

    private static ValueRange parseRange(String value, CronField.Type type) {
        if (value.equals("*")) {
            return type.range();
        }
        int hyphenPos = value.indexOf(45);
        if (hyphenPos == -1) {
            int result = type.checkValidValue(Integer.parseInt(value));
            return ValueRange.of(result, result);
        }
        int min = Integer.parseInt(value.substring(0, hyphenPos));
        int max = Integer.parseInt(value.substring(hyphenPos + 1));
        min = type.checkValidValue(min);
        max = type.checkValidValue(max);
        if (type == CronField.Type.DAY_OF_WEEK && min == 7) {
            min = 0;
        }
        return ValueRange.of(min, max);
    }

    @Override
    @Nullable
    public <T extends Temporal & Comparable<? super T>> T nextOrSame(T temporal) {
        int current = this.type().get(temporal);
        int next = this.nextSetBit(current);
        if (next == -1) {
            temporal = this.type().rollForward(temporal);
            next = this.nextSetBit(0);
        }
        if (next == current) {
            return temporal;
        }
        int count = 0;
        current = this.type().get(temporal);
        while (current != next && count++ < 366) {
            temporal = this.type().elapseUntil(temporal, next);
            current = this.type().get(temporal);
            next = this.nextSetBit(current);
            if (next != -1) continue;
            temporal = this.type().rollForward(temporal);
            next = this.nextSetBit(0);
        }
        if (count >= 366) {
            return null;
        }
        return this.type().reset(temporal);
    }

    boolean getBit(int index) {
        return (this.bits & 1L << index) != 0L;
    }

    private int nextSetBit(int fromIndex) {
        long result = this.bits & -1L << fromIndex;
        if (result != 0L) {
            return Long.numberOfTrailingZeros(result);
        }
        return -1;
    }

    private void setBits(ValueRange range) {
        if (range.getMinimum() == range.getMaximum()) {
            this.setBit((int)range.getMinimum());
        } else {
            long minMask = -1L << (int)range.getMinimum();
            long maxMask = -1L >>> (int)(-(range.getMaximum() + 1L));
            this.bits |= minMask & maxMask;
        }
    }

    private void setBits(ValueRange range, int delta) {
        if (delta == 1) {
            this.setBits(range);
        } else {
            int i2 = (int)range.getMinimum();
            while ((long)i2 <= range.getMaximum()) {
                this.setBit(i2);
                i2 += delta;
            }
        }
    }

    private void setBit(int index) {
        this.bits |= 1L << index;
    }

    private void clearBit(int index) {
        this.bits &= 1L << index ^ 0xFFFFFFFFFFFFFFFFL;
    }

    public int hashCode() {
        return Long.hashCode(this.bits);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BitsCronField)) {
            return false;
        }
        BitsCronField other = (BitsCronField)o;
        return this.type() == other.type() && this.bits == other.bits;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(this.type().toString());
        builder.append(" {");
        int i2 = this.nextSetBit(0);
        if (i2 != -1) {
            builder.append(i2);
            i2 = this.nextSetBit(i2 + 1);
            while (i2 != -1) {
                builder.append(", ");
                builder.append(i2);
                i2 = this.nextSetBit(i2 + 1);
            }
        }
        builder.append('}');
        return builder.toString();
    }
}

