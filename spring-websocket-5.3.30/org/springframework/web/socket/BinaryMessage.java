/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.socket;

import java.nio.ByteBuffer;
import org.springframework.web.socket.AbstractWebSocketMessage;

public final class BinaryMessage
extends AbstractWebSocketMessage<ByteBuffer> {
    public BinaryMessage(ByteBuffer payload) {
        super(payload, true);
    }

    public BinaryMessage(ByteBuffer payload, boolean isLast) {
        super(payload, isLast);
    }

    public BinaryMessage(byte[] payload) {
        this(payload, true);
    }

    public BinaryMessage(byte[] payload, boolean isLast) {
        this(payload, 0, payload.length, isLast);
    }

    public BinaryMessage(byte[] payload, int offset, int length, boolean isLast) {
        super(ByteBuffer.wrap(payload, offset, length), isLast);
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

