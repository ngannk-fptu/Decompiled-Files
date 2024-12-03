/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.json.marshal.wrapped;

import com.atlassian.json.marshal.wrapped.WrappingJsonable;

public class JsonableString
extends WrappingJsonable<String> {
    public JsonableString(String value) {
        super(value);
    }

    @Override
    protected String convertValueToString(String value) {
        return "\"" + value + "\"";
    }
}

