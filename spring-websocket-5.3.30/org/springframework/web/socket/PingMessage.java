/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.socket;

import java.nio.ByteBuffer;
import org.springframework.web.socket.AbstractWebSocketMessage;

public final class PingMessage
extends AbstractWebSocketMessage<ByteBuffer> {
    public PingMessage() {
        super(ByteBuffer.allocate(0));
    }

    public PingMessage(ByteBuffer payload) {
        super(payload);
    }

    @Override
    public int getPayloadLength() {
        return ((ByteBuffer)this.getPayload()).remaining();
    }

    @Override
    protected String toStringPayload() {
        return ((ByteBuffer)this.getPayload()).toString();
    }
}

