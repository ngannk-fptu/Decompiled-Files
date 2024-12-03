/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.concurrent.Immutable
 */
package com.google.template.soy.data.restricted;

import com.google.common.base.Preconditions;
import com.google.template.soy.data.restricted.PrimitiveData;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class StringData
extends PrimitiveData {
    public static final StringData EMPTY_STRING = new StringData("");
    private final String value;

    @Deprecated
    public StringData(String value) {
        Preconditions.checkNotNull((Object)value);
        this.value = value;
    }

    public static StringData forValue(String value) {
        return value.length() == 0 ? EMPTY_STRING : new StringData(value);
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String stringValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @Override
    @Deprecated
    public boolean toBoolean() {
        return this.value.length() > 0;
    }

    @Override
    public boolean equals(Object other) {
        return other != null && this.value.equals(other.toString());
    }

    public int hashCode() {
        return this.value.hashCode();
    }
}

