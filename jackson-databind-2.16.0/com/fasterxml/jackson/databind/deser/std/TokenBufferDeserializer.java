/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonParser
 */
package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;

@JacksonStdImpl
public class TokenBufferDeserializer
extends StdScalarDeserializer<TokenBuffer> {
    private static final long serialVersionUID = 1L;

    public TokenBufferDeserializer() {
        super(TokenBuffer.class);
    }

    @Override
    public LogicalType logicalType() {
        return LogicalType.Untyped;
    }

    @Override
    public TokenBuffer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return ctxt.bufferForInputBuffering(p).deserialize(p, ctxt);
    }
}

