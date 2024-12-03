/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.frame;

import java.nio.ByteBuffer;
import org.apache.hc.core5.http2.frame.Frame;
import org.apache.hc.core5.http2.frame.FrameFlag;

public final class RawFrame
extends Frame<ByteBuffer> {
    private final ByteBuffer payload;
    private final int len;

    public RawFrame(int type, int flags, int streamId, ByteBuffer payload) {
        super(type, flags, streamId);
        this.payload = payload;
        this.len = payload != null ? payload.remaining() : 0;
    }

    public boolean isPadded() {
        return this.isFlagSet(FrameFlag.PADDED);
    }

    public int getLength() {
        return this.len;
    }

    public ByteBuffer getPayloadContent() {
        if (this.payload != null) {
            if (this.isPadded()) {
                ByteBuffer dup = this.payload.duplicate();
                if (dup.remaining() == 0) {
                    return null;
                }
                int padding = dup.get() & 0xFF;
                if (padding > dup.remaining()) {
                    return null;
                }
                dup.limit(dup.limit() - padding);
                return dup;
            }
            return this.payload.duplicate();
        }
        return null;
    }

    @Override
    public ByteBuffer getPayload() {
        return this.payload != null ? this.payload.duplicate() : null;
    }
}

