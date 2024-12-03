/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.AsciiString
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.util.AsciiString;

public enum WebSocketVersion {
    UNKNOWN(AsciiString.cached((String)"")),
    V00(AsciiString.cached((String)"0")),
    V07(AsciiString.cached((String)"7")),
    V08(AsciiString.cached((String)"8")),
    V13(AsciiString.cached((String)"13"));

    private final AsciiString headerValue;

    private WebSocketVersion(AsciiString headerValue) {
        this.headerValue = headerValue;
    }

    public String toHttpHeaderValue() {
        return this.toAsciiString().toString();
    }

    AsciiString toAsciiString() {
        if (this == UNKNOWN) {
            throw new IllegalStateException("Unknown web socket version: " + (Object)((Object)this));
        }
        return this.headerValue;
    }
}

