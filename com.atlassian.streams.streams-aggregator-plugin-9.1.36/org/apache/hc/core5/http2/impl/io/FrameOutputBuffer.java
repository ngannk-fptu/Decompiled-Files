/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.impl.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import org.apache.hc.core5.http2.H2ConnectionException;
import org.apache.hc.core5.http2.H2Error;
import org.apache.hc.core5.http2.H2TransportMetrics;
import org.apache.hc.core5.http2.frame.FrameFlag;
import org.apache.hc.core5.http2.frame.RawFrame;
import org.apache.hc.core5.http2.impl.BasicH2TransportMetrics;
import org.apache.hc.core5.util.Args;

public final class FrameOutputBuffer {
    private final BasicH2TransportMetrics metrics;
    private final int maxFramePayloadSize;
    private final byte[] buffer;

    public FrameOutputBuffer(BasicH2TransportMetrics metrics, int maxFramePayloadSize) {
        Args.notNull(metrics, "HTTP2 transport metrics");
        Args.positive(maxFramePayloadSize, "Maximum payload size");
        this.metrics = metrics;
        this.maxFramePayloadSize = maxFramePayloadSize;
        this.buffer = new byte[9 + maxFramePayloadSize + 255 + 1];
    }

    public FrameOutputBuffer(int maxFramePayloadSize) {
        this(new BasicH2TransportMetrics(), maxFramePayloadSize);
    }

    public void write(RawFrame frame, OutputStream outStream) throws IOException {
        int payloadLen;
        if (frame == null) {
            return;
        }
        int type = frame.getType();
        long streamId = frame.getStreamId();
        int flags = frame.getFlags();
        ByteBuffer payload = frame.getPayload();
        int n = payloadLen = payload != null ? payload.remaining() : 0;
        if (payload != null && payload.remaining() > this.maxFramePayloadSize) {
            throw new H2ConnectionException(H2Error.FRAME_SIZE_ERROR, "Frame size exceeds maximum");
        }
        this.buffer[0] = (byte)(payloadLen >> 16 & 0xFF);
        this.buffer[1] = (byte)(payloadLen >> 8 & 0xFF);
        this.buffer[2] = (byte)(payloadLen & 0xFF);
        this.buffer[3] = (byte)(type & 0xFF);
        this.buffer[4] = (byte)(flags & 0xFF);
        this.buffer[5] = (byte)(streamId >> 24 & 0xFFL);
        this.buffer[6] = (byte)(streamId >> 16 & 0xFFL);
        this.buffer[7] = (byte)(streamId >> 8 & 0xFFL);
        this.buffer[8] = (byte)(streamId & 0xFFL);
        int frameLen = 9;
        int padding = 0;
        if ((flags & FrameFlag.PADDED.getValue()) > 0) {
            padding = 16;
            this.buffer[9] = (byte)(padding & 0xFF);
            ++frameLen;
        }
        if (payload != null) {
            payload.get(this.buffer, frameLen, payload.remaining());
            frameLen += payloadLen;
        }
        for (int i = 0; i < padding; ++i) {
            this.buffer[frameLen++] = 0;
        }
        outStream.write(this.buffer, 0, frameLen);
        this.metrics.incrementFramesTransferred();
        this.metrics.incrementBytesTransferred(frameLen);
    }

    public H2TransportMetrics getMetrics() {
        return this.metrics;
    }
}

