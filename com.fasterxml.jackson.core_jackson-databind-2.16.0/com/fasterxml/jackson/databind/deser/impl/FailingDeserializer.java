/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonParser
 */
package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;

public class FailingDeserializer
extends StdDeserializer<Object> {
    private static final long serialVersionUID = 1L;
    protected final String _message;

    public FailingDeserializer(String m) {
        this(Object.class, m);
    }

    public FailingDeserializer(Class<?> rawType, String m) {
        super(rawType);
        this._message = m;
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ctxt.reportInputMismatch(this, this._message, new Object[0]);
        return null;
    }
}

