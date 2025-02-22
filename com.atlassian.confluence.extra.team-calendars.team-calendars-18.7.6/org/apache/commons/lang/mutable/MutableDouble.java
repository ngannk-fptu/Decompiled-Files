/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang.mutable;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.mutable.Mutable;

public class MutableDouble
extends Number
implements Comparable,
Mutable {
    private static final long serialVersionUID = 1587163916L;
    private double value;

    public MutableDouble() {
    }

    public MutableDouble(double value) {
        this.value = value;
    }

    public MutableDouble(Number value) {
        this.value = value.doubleValue();
    }

    public MutableDouble(String value) throws NumberFormatException {
        this.value = Double.parseDouble(value);
    }

    public Object getValue() {
        return new Double(this.value);
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setValue(Object value) {
        this.setValue(((Number)value).doubleValue());
    }

    public boolean isNaN() {
        return Double.isNaN(this.value);
    }

    public boolean isInfinite() {
        return Double.isInfinite(this.value);
    }

    public void increment() {
        this.value += 1.0;
    }

    public void decrement() {
        this.value -= 1.0;
    }

    public void add(double operand) {
        this.value += operand;
    }

    public void add(Number operand) {
        this.value += operand.doubleValue();
    }

    public void subtract(double operand) {
        this.value -= operand;
    }

    public void subtract(Number operand) {
        this.value -= operand.doubleValue();
    }

    public int intValue() {
        return (int)this.value;
    }

    public long longValue() {
        return (long)this.value;
    }

    public float floatValue() {
        return (float)this.value;
    }

    public double doubleValue() {
        return this.value;
    }

    public Double toDouble() {
        return new Double(this.doubleValue());
    }

    public boolean equals(Object obj) {
        return obj instanceof MutableDouble && Double.doubleToLongBits(((MutableDouble)obj).value) == Double.doubleToLongBits(this.value);
    }

    public int hashCode() {
        long bits = Double.doubleToLongBits(this.value);
        return (int)(bits ^ bits >>> 32);
    }

    public int compareTo(Object obj) {
        MutableDouble other = (MutableDouble)obj;
        double anotherVal = other.value;
        return NumberUtils.compare(this.value, anotherVal);
    }

    public String toString() {
        return String.valueOf(this.value);
    }
}

