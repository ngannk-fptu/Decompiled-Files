/*
 * Decompiled with CFR 0.152.
 */
package org.apache.coyote.http2;

import java.nio.ByteBuffer;
import org.apache.coyote.http2.AbstractNonZeroStream;
import org.apache.coyote.http2.ConnectionException;
import org.apache.coyote.http2.Http2Exception;
import org.apache.coyote.http2.StreamStateMachine;

class RecycledStream
extends AbstractNonZeroStream {
    private final String connectionId;
    private int remainingFlowControlWindow;

    RecycledStream(String connectionId, Integer identifier, StreamStateMachine state, int remainingFlowControlWindow) {
        super(identifier, state);
        this.connectionId = connectionId;
        this.remainingFlowControlWindow = remainingFlowControlWindow;
    }

    @Override
    String getConnectionId() {
        return this.connectionId;
    }

    @Override
    void incrementWindowSize(int increment) throws Http2Exception {
    }

    @Override
    void receivedData(int payloadSize) throws ConnectionException {
        this.remainingFlowControlWindow -= payloadSize;
    }

    @Override
    ByteBuffer getInputByteBuffer() {
        if (this.remainingFlowControlWindow < 0) {
            return ZERO_LENGTH_BYTEBUFFER;
        }
        return null;
    }
}

