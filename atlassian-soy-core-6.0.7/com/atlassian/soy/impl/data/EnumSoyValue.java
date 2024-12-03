/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.template.soy.data.SoyAbstractValue
 *  com.google.template.soy.data.SoyValue
 *  com.google.template.soy.data.restricted.StringData
 *  javax.annotation.Nonnull
 */
package com.atlassian.soy.impl.data;

import com.google.common.base.Preconditions;
import com.google.template.soy.data.SoyAbstractValue;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.restricted.StringData;
import javax.annotation.Nonnull;

public class EnumSoyValue
extends SoyAbstractValue {
    private final Enum<?> value;

    public EnumSoyValue(Enum<?> value) {
        this.value = (Enum)Preconditions.checkNotNull(value, (Object)"value");
    }

    public boolean equals(@Nonnull SoyValue other) {
        return other instanceof EnumSoyValue && this.value.equals(((EnumSoyValue)other).value) || other instanceof StringData && this.value.name().equals(other.stringValue());
    }

    public boolean coerceToBoolean() {
        return true;
    }

    public String coerceToString() {
        return this.value.name();
    }

    public Enum<?> getValue() {
        return this.value;
    }
}

