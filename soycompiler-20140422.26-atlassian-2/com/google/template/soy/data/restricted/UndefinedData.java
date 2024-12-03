/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.Immutable
 */
package com.google.template.soy.data.restricted;

import com.google.template.soy.data.SoyDataException;
import com.google.template.soy.data.restricted.PrimitiveData;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class UndefinedData
extends PrimitiveData {
    public static final UndefinedData INSTANCE = new UndefinedData();

    @Deprecated
    public UndefinedData() {
    }

    @Override
    public String toString() {
        throw new SoyDataException("Attempted to coerce undefined value into a string.");
    }

    @Override
    @Deprecated
    public boolean toBoolean() {
        return false;
    }

    @Override
    public boolean equals(Object other) {
        return this == other;
    }

    public int hashCode() {
        return super.hashCode();
    }
}

