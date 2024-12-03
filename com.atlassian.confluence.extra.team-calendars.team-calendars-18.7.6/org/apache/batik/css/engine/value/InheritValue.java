/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value;

import org.apache.batik.css.engine.value.AbstractValue;

public class InheritValue
extends AbstractValue {
    public static final InheritValue INSTANCE = new InheritValue();

    protected InheritValue() {
    }

    @Override
    public String getCssText() {
        return "inherit";
    }

    @Override
    public short getCssValueType() {
        return 0;
    }

    public String toString() {
        return this.getCssText();
    }
}

