/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.impl;

import org.eclipse.jdt.internal.compiler.impl.Constant;

public class LongConstant
extends Constant {
    private static final LongConstant ZERO = new LongConstant(0L);
    private static final LongConstant MIN_VALUE = new LongConstant(Long.MIN_VALUE);
    private long value;

    public static Constant fromValue(long value) {
        if (value == 0L) {
            return ZERO;
        }
        if (value == Long.MIN_VALUE) {
            return MIN_VALUE;
        }
        return new LongConstant(value);
    }

    private LongConstant(long value) {
        this.value = value;
    }

    @Override
    public byte byteValue() {
        return (byte)this.value;
    }

    @Override
    public char charValue() {
        return (char)this.value;
    }

    @Override
    public double doubleValue() {
        return this.value;
    }

    @Override
    public float floatValue() {
        return this.value;
    }

    @Override
    public int intValue() {
        return (int)this.value;
    }

    @Override
    public long longValue() {
        return this.value;
    }

    @Override
    public short shortValue() {
        return (short)this.value;
    }

    @Override
    public String stringValue() {
        return String.valueOf(this.value);
    }

    @Override
    public String toString() {
        return "(long)" + this.value;
    }

    @Override
    public int typeID() {
        return 7;
    }

    public int hashCode() {
        return (int)(this.value ^ this.value >>> 32);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        LongConstant other = (LongConstant)obj;
        return this.value == other.value;
    }
}

