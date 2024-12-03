/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.impl;

import org.eclipse.jdt.internal.compiler.impl.Constant;

public class DoubleConstant
extends Constant {
    private double value;

    public static Constant fromValue(double value) {
        return new DoubleConstant(value);
    }

    private DoubleConstant(double value) {
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
        return (float)this.value;
    }

    @Override
    public int intValue() {
        return (int)this.value;
    }

    @Override
    public long longValue() {
        return (long)this.value;
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
        if (this == NotAConstant) {
            return "(Constant) NotAConstant";
        }
        return "(double)" + this.value;
    }

    @Override
    public int typeID() {
        return 8;
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.value);
        return (int)(temp ^ temp >>> 32);
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
        DoubleConstant other = (DoubleConstant)obj;
        return Double.doubleToLongBits(this.value) == Double.doubleToLongBits(other.value);
    }
}

