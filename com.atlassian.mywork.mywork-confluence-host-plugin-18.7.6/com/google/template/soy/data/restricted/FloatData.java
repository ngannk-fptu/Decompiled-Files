/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.Immutable
 */
package com.google.template.soy.data.restricted;

import com.google.template.soy.data.restricted.NumberData;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class FloatData
extends NumberData {
    private final double value;

    @Deprecated
    public FloatData(double value) {
        this.value = value;
    }

    public static FloatData forValue(double value) {
        return new FloatData(value);
    }

    public double getValue() {
        return this.value;
    }

    @Override
    public double floatValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return FloatData.toString(this.value);
    }

    public static String toString(double value) {
        if (value % 1.0 == 0.0 && Math.abs(value) < 9.223372036854776E18) {
            return String.valueOf((long)value);
        }
        return Double.toString(value).replace('E', 'e');
    }

    @Override
    @Deprecated
    public boolean toBoolean() {
        return this.value != 0.0 && !Double.isNaN(this.value);
    }

    @Override
    public double toFloat() {
        return this.value;
    }
}

