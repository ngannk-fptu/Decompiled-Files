/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.json.marshal.wrapped;

import com.atlassian.json.marshal.wrapped.WrappingJsonable;

public class JsonableBoolean
extends WrappingJsonable<Boolean> {
    public JsonableBoolean(Boolean value) {
        super(value);
    }

    @Override
    protected String convertValueToString(Boolean value) {
        return String.valueOf(value);
    }
}

