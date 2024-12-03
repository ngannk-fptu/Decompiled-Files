/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.data.restricted;

import com.google.template.soy.data.restricted.PrimitiveData;

public abstract class NumberData
extends PrimitiveData {
    public abstract double toFloat();

    @Override
    public double numberValue() {
        return this.toFloat();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof NumberData && ((NumberData)other).toFloat() == this.toFloat();
    }

    public int hashCode() {
        return Double.valueOf(this.toFloat()).hashCode();
    }
}

