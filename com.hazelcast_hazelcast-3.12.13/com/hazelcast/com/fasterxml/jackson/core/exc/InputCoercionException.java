/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.com.fasterxml.jackson.core.exc;

import com.hazelcast.com.fasterxml.jackson.core.JsonParser;
import com.hazelcast.com.fasterxml.jackson.core.JsonToken;
import com.hazelcast.com.fasterxml.jackson.core.exc.StreamReadException;
import com.hazelcast.com.fasterxml.jackson.core.util.RequestPayload;

public class InputCoercionException
extends StreamReadException {
    private static final long serialVersionUID = 1L;
    protected final JsonToken _inputType;
    protected final Class<?> _targetType;

    public InputCoercionException(JsonParser p, String msg, JsonToken inputType, Class<?> targetType) {
        super(p, msg);
        this._inputType = inputType;
        this._targetType = targetType;
    }

    @Override
    public InputCoercionException withParser(JsonParser p) {
        this._processor = p;
        return this;
    }

    @Override
    public InputCoercionException withRequestPayload(RequestPayload p) {
        this._requestPayload = p;
        return this;
    }

    public JsonToken getInputType() {
        return this._inputType;
    }

    public Class<?> getTargetType() {
        return this._targetType;
    }
}

