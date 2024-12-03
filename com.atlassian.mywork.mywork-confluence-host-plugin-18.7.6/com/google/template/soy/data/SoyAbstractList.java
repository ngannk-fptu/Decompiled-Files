/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.google.template.soy.data;

import com.google.common.collect.ImmutableList;
import com.google.template.soy.data.SoyAbstractValue;
import com.google.template.soy.data.SoyDataException;
import com.google.template.soy.data.SoyList;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueProvider;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.data.restricted.StringData;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class SoyAbstractList
extends SoyAbstractValue
implements SoyList {
    @Override
    public final SoyValue get(int index) {
        SoyValueProvider valueProvider = this.getProvider(index);
        return valueProvider != null ? valueProvider.resolve() : null;
    }

    @Override
    public final int getItemCnt() {
        return this.length();
    }

    @Nonnull
    public final Iterable<IntegerData> getItemKeys() {
        ImmutableList.Builder indicesBuilder = ImmutableList.builder();
        int n = this.length();
        for (int i = 0; i < n; ++i) {
            indicesBuilder.add((Object)IntegerData.forValue(i));
        }
        return indicesBuilder.build();
    }

    @Override
    public final boolean hasItem(SoyValue key) {
        int index = this.getIntegerIndex(key);
        return 0 <= index && index < this.length();
    }

    @Override
    public final SoyValue getItem(SoyValue key) {
        return this.get(this.getIntegerIndex(key));
    }

    @Override
    public final SoyValueProvider getItemProvider(SoyValue key) {
        return this.getProvider(this.getIntegerIndex(key));
    }

    protected final int getIntegerIndex(SoyValue key) {
        if (key instanceof StringData) {
            try {
                return Integer.parseInt(key.stringValue());
            }
            catch (IllegalArgumentException e) {
                throw new SoyDataException("\"" + key + "\" is not a valid list index (must be an int)");
            }
        }
        return key.integerValue();
    }

    @Override
    public final String coerceToString() {
        StringBuilder listStr = new StringBuilder();
        listStr.append('[');
        boolean isFirst = true;
        for (SoyValueProvider soyValueProvider : this.asJavaList()) {
            if (isFirst) {
                isFirst = false;
            } else {
                listStr.append(", ");
            }
            listStr.append(soyValueProvider.resolve().coerceToString());
        }
        listStr.append(']');
        return listStr.toString();
    }

    @Override
    public final boolean equals(SoyValue other) {
        return this == other;
    }

    @Override
    public final boolean coerceToBoolean() {
        return true;
    }
}

