/*
 * Decompiled with CFR 0.152.
 */
package org.apache.coyote.http2;

import java.nio.ByteBuffer;
import org.apache.coyote.http2.AbstractStream;
import org.apache.coyote.http2.FrameType;
import org.apache.coyote.http2.Http2Exception;
import org.apache.coyote.http2.StreamStateMachine;

abstract class AbstractNonZeroStream
extends AbstractStream {
    protected static final ByteBuffer ZERO_LENGTH_BYTEBUFFER = ByteBuffer.allocate(0);
    protected final StreamStateMachine state;

    AbstractNonZeroStream(String connectionId, Integer identifier) {
        super(identifier);
        this.state = new StreamStateMachine(connectionId, this.getIdAsString());
    }

    AbstractNonZeroStream(Integer identifier, StreamStateMachine state) {
        super(identifier);
        this.state = state;
    }

    final boolean isClosedFinal() {
        return this.state.isClosedFinal();
    }

    final void checkState(FrameType frameType) throws Http2Exception {
        this.state.checkFrameType(frameType);
    }

    abstract ByteBuffer getInputByteBuffer();

    abstract void receivedData(int var1) throws Http2Exception;
}

