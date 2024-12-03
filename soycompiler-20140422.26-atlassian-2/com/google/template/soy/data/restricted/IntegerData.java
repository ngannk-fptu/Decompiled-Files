/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.concurrent.Immutable
 */
package com.google.template.soy.data.restricted;

import com.google.common.base.Preconditions;
import com.google.template.soy.data.restricted.NumberData;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class IntegerData
extends NumberData {
    public static final IntegerData ZERO = new IntegerData(0L);
    public static final IntegerData ONE = new IntegerData(1L);
    public static final IntegerData MINUS_ONE = new IntegerData(-1L);
    private static final IntegerData TWO = new IntegerData(2L);
    private static final IntegerData THREE = new IntegerData(3L);
    private static final IntegerData FOUR = new IntegerData(4L);
    private static final IntegerData FIVE = new IntegerData(5L);
    private static final IntegerData SIX = new IntegerData(6L);
    private static final IntegerData SEVEN = new IntegerData(7L);
    private static final IntegerData EIGHT = new IntegerData(8L);
    private static final IntegerData NINE = new IntegerData(9L);
    private static final IntegerData TEN = new IntegerData(10L);
    private final long value;

    IntegerData(long value) {
        this.value = value;
    }

    public static IntegerData forValue(long value) {
        if (value > 10L || value < -1L) {
            return new IntegerData(value);
        }
        switch ((int)value) {
            case -1: {
                return MINUS_ONE;
            }
            case 0: {
                return ZERO;
            }
            case 1: {
                return ONE;
            }
            case 2: {
                return TWO;
            }
            case 3: {
                return THREE;
            }
            case 4: {
                return FOUR;
            }
            case 5: {
                return FIVE;
            }
            case 6: {
                return SIX;
            }
            case 7: {
                return SEVEN;
            }
            case 8: {
                return EIGHT;
            }
            case 9: {
                return NINE;
            }
            case 10: {
                return TEN;
            }
        }
        throw new AssertionError((Object)"Impossible case");
    }

    public long getValue() {
        return this.value;
    }

    @Override
    public int integerValue() {
        Preconditions.checkState((this.value >= Integer.MIN_VALUE && this.value <= Integer.MAX_VALUE ? 1 : 0) != 0, (Object)("Casting long to integer results in overflow: " + this.value));
        return (int)this.value;
    }

    @Override
    public long longValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    @Override
    @Deprecated
    public boolean toBoolean() {
        return this.value != 0L;
    }

    @Override
    public double toFloat() {
        return this.value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof NumberData)) {
            return false;
        }
        if (other instanceof IntegerData) {
            return this.value == ((IntegerData)other).value;
        }
        return super.equals(other);
    }
}

