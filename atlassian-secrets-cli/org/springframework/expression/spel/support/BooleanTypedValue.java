/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.expression.spel.support;

import org.springframework.expression.TypedValue;

public class BooleanTypedValue
extends TypedValue {
    public static final BooleanTypedValue TRUE = new BooleanTypedValue(true);
    public static final BooleanTypedValue FALSE = new BooleanTypedValue(false);

    private BooleanTypedValue(boolean b) {
        super(b);
    }

    public static BooleanTypedValue forValue(boolean b) {
        return b ? TRUE : FALSE;
    }
}

