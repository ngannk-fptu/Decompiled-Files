/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang.mutable;

import org.apache.commons.lang.mutable.Mutable;

public class MutableShort
extends Number
implements Comparable,
Mutable {
    private static final long serialVersionUID = -2135791679L;
    private short value;

    public MutableShort() {
    }

    public MutableShort(short value) {
        this.value = value;
    }

    public MutableShort(Number value) {
        this.value = value.shortValue();
    }

    public MutableShort(String value) throws NumberFormatException {
        this.value = Short.parseShort(value);
    }

    public Object getValue() {
        return new Short(this.value);
    }

    public void setValue(short value) {
        this.value = value;
    }

    public void setValue(Object value) {
        this.setValue(((Number)value).shortValue());
    }

    public void increment() {
        this.value = (short)(this.value + 1);
    }

    public void decrement() {
        this.value = (short)(this.value - 1);
    }

    public void add(short operand) {
        this.value = (short)(this.value + operand);
    }

    public void add(Number operand) {
        this.value = (short)(this.value + operand.shortValue());
    }

    public void subtract(short operand) {
        this.value = (short)(this.value - operand);
    }

    public void subtract(Number operand) {
        this.value = (short)(this.value - operand.shortValue());
    }

    public short shortValue() {
        return this.value;
    }

    public int intValue() {
        return this.value;
    }

    public long longValue() {
        return this.value;
    }

    public float floatValue() {
        return this.value;
    }

    public double doubleValue() {
        return this.value;
    }

    public Short toShort() {
        return new Short(this.shortValue());
    }

    public boolean equals(Object obj) {
        if (obj instanceof MutableShort) {
            return this.value == ((MutableShort)obj).shortValue();
        }
        return false;
    }

    public int hashCode() {
        return this.value;
    }

    public int compareTo(Object obj) {
        MutableShort other = (MutableShort)obj;
        short anotherVal = other.value;
        return this.value < anotherVal ? -1 : (this.value == anotherVal ? 0 : 1);
    }

    public String toString() {
        return String.valueOf(this.value);
    }
}

