/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.json.marshal.wrapped;

import com.atlassian.json.marshal.wrapped.WrappingJsonable;

public class JsonableNumber
extends WrappingJsonable<Number> {
    public JsonableNumber(Number value) {
        super(value);
    }

    @Override
    protected String convertValueToString(Number value) {
        return String.valueOf(value);
    }
}

