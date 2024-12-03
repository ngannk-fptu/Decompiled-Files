/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.socket;

import java.nio.charset.StandardCharsets;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.AbstractWebSocketMessage;

public final class TextMessage
extends AbstractWebSocketMessage<String> {
    @Nullable
    private final byte[] bytes;

    public TextMessage(CharSequence payload) {
        super(payload.toString(), true);
        this.bytes = null;
    }

    public TextMessage(byte[] payload) {
        super(new String(payload, StandardCharsets.UTF_8));
        this.bytes = payload;
    }

    public TextMessage(CharSequence payload, boolean isLast) {
        super(payload.toString(), isLast);
        this.bytes = null;
    }

    @Override
    public int getPayloadLength() {
        return this.asBytes().length;
    }

    public byte[] asBytes() {
        return this.bytes != null ? this.bytes : ((String)this.getPayload()).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected String toStringPayload() {
        String payload = (String)this.getPayload();
        return payload.length() > 10 ? payload.substring(0, 10) + ".." : payload;
    }
}

