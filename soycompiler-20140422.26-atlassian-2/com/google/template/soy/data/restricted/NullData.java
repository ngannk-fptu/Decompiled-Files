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
public final class NullData
extends PrimitiveData {
    public static final NullData INSTANCE = new NullData();

    private NullData() {
    }

    @Override
    public String toString() {
        return "null";
    }

    @Override
    @Deprecated
    public boolean toBoolean() {
        return false;
    }

    @Override
    public boolean equals(Object other) {
        return other == INSTANCE;
    }

    public int hashCode() {
        return this.getClass().hashCode();
    }
}

