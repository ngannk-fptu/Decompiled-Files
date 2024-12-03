/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.google.template.soy.data;

import com.google.template.soy.data.SoyAbstractValue;
import com.google.template.soy.data.SoyMap;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueProvider;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class SoyAbstractMap
extends SoyAbstractValue
implements SoyMap {
    @Override
    public SoyValue getItem(SoyValue key) {
        SoyValueProvider valueProvider = this.getItemProvider(key);
        return valueProvider != null ? valueProvider.resolve() : null;
    }

    @Override
    public final String coerceToString() {
        StringBuilder mapStr = new StringBuilder();
        mapStr.append('{');
        boolean isFirst = true;
        for (SoyValue soyValue : this.getItemKeys()) {
            SoyValue value = this.getItem(soyValue);
            if (isFirst) {
                isFirst = false;
            } else {
                mapStr.append(", ");
            }
            mapStr.append(soyValue.coerceToString()).append(": ").append(value.coerceToString());
        }
        mapStr.append('}');
        return mapStr.toString();
    }

    @Override
    public final boolean equals(SoyValue other) {
        return this == other;
    }

    @Override
    public final boolean coerceToBoolean() {
        return true;
    }

    public String toString() {
        return this.coerceToString();
    }
}

