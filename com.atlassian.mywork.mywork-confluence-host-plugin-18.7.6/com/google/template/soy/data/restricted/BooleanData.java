/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.Immutable
 */
package com.google.template.soy.data.restricted;

import com.google.template.soy.data.restricted.PrimitiveData;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class BooleanData
extends PrimitiveData {
    public static final BooleanData TRUE = new BooleanData(true);
    public static final BooleanData FALSE = new BooleanData(false);
    private final boolean value;

    @Deprecated
    public BooleanData(boolean value) {
        this.value = value;
    }

    public static BooleanData forValue(boolean value) {
        return value ? TRUE : FALSE;
    }

    public boolean getValue() {
        return this.value;
    }

    @Override
    public boolean booleanValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return Boolean.toString(this.value);
    }

    @Override
    @Deprecated
    public boolean toBoolean() {
        return this.value;
    }

    @Override
    public boolean equals(Object other) {
        return other != null && other.getClass() == BooleanData.class && ((BooleanData)other).getValue() == this.value;
    }

    public int hashCode() {
        return Boolean.valueOf(this.value).hashCode();
    }
}

