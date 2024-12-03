/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang.mutable;

import org.apache.commons.lang.mutable.Mutable;

public class MutableByte
extends Number
implements Comparable,
Mutable {
    private static final long serialVersionUID = -1585823265L;
    private byte value;

    public MutableByte() {
    }

    public MutableByte(byte value) {
        this.value = value;
    }

    public MutableByte(Number value) {
        this.value = value.byteValue();
    }

    public MutableByte(String value) throws NumberFormatException {
        this.value = Byte.parseByte(value);
    }

    public Object getValue() {
        return new Byte(this.value);
    }

    public void setValue(byte value) {
        this.value = value;
    }

    public void setValue(Object value) {
        this.setValue(((Number)value).byteValue());
    }

    public void increment() {
        this.value = (byte)(this.value + 1);
    }

    public void decrement() {
        this.value = (byte)(this.value - 1);
    }

    public void add(byte operand) {
        this.value = (byte)(this.value + operand);
    }

    public void add(Number operand) {
        this.value = (byte)(this.value + operand.byteValue());
    }

    public void subtract(byte operand) {
        this.value = (byte)(this.value - operand);
    }

    public void subtract(Number operand) {
        this.value = (byte)(this.value - operand.byteValue());
    }

    public byte byteValue() {
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

    public Byte toByte() {
        return new Byte(this.byteValue());
    }

    public boolean equals(Object obj) {
        if (obj instanceof MutableByte) {
            return this.value == ((MutableByte)obj).byteValue();
        }
        return false;
    }

    public int hashCode() {
        return this.value;
    }

    public int compareTo(Object obj) {
        MutableByte other = (MutableByte)obj;
        byte anotherVal = other.value;
        return this.value < anotherVal ? -1 : (this.value == anotherVal ? 0 : 1);
    }

    public String toString() {
        return String.valueOf(this.value);
    }
}

