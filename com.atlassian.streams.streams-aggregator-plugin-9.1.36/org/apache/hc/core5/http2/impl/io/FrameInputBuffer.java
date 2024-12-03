/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.impl.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.apache.hc.core5.http.ConnectionClosedException;
import org.apache.hc.core5.http2.H2ConnectionException;
import org.apache.hc.core5.http2.H2CorruptFrameException;
import org.apache.hc.core5.http2.H2Error;
import org.apache.hc.core5.http2.H2TransportMetrics;
import org.apache.hc.core5.http2.frame.FrameFlag;
import org.apache.hc.core5.http2.frame.RawFrame;
import org.apache.hc.core5.http2.impl.BasicH2TransportMetrics;
import org.apache.hc.core5.util.Args;

public final class FrameInputBuffer {
    private final BasicH2TransportMetrics metrics;
    private final int maxFramePayloadSize;
    private final byte[] buffer;
    private int off;
    private int dataLen;

    FrameInputBuffer(BasicH2TransportMetrics metrics, int bufferLen, int maxFramePayloadSize) {
        Args.notNull(metrics, "HTTP2 transport metrics");
        Args.positive(maxFramePayloadSize, "Maximum payload size");
        this.metrics = metrics;
        this.maxFramePayloadSize = maxFramePayloadSize;
        this.buffer = new byte[bufferLen];
        this.dataLen = 0;
    }

    public FrameInputBuffer(BasicH2TransportMetrics metrics, int maxFramePayloadSize) {
        this(metrics, 9 + maxFramePayloadSize, maxFramePayloadSize);
    }

    public FrameInputBuffer(int maxFramePayloadSize) {
        this(new BasicH2TransportMetrics(), maxFramePayloadSize);
    }

    boolean hasData() {
        return this.dataLen > 0;
    }

    void fillBuffer(InputStream inStream, int requiredLen) throws IOException {
        while (this.dataLen < requiredLen) {
            int bytesRead;
            if (this.off > 0) {
                System.arraycopy(this.buffer, this.off, this.buffer, 0, this.dataLen);
                this.off = 0;
            }
            if ((bytesRead = inStream.read(this.buffer, this.off + this.dataLen, this.buffer.length - this.dataLen)) == -1) {
                if (this.dataLen > 0) {
                    throw new H2CorruptFrameException("Corrupt or incomplete HTTP2 frame");
                }
                throw new ConnectionClosedException();
            }
            this.dataLen += bytesRead;
            this.metrics.incrementBytesTransferred(bytesRead);
        }
    }

    public RawFrame read(InputStream inStream) throws IOException {
        this.fillBuffer(inStream, 9);
        int payloadOff = 9;
        int payloadLen = (this.buffer[this.off] & 0xFF) << 16 | (this.buffer[this.off + 1] & 0xFF) << 8 | this.buffer[this.off + 2] & 0xFF;
        int type = this.buffer[this.off + 3] & 0xFF;
        int flags = this.buffer[this.off + 4] & 0xFF;
        int streamId = Math.abs(this.buffer[this.off + 5] & 0xFF) << 24 | this.buffer[this.off + 6] & 0xFF0000 | (this.buffer[this.off + 7] & 0xFF) << 8 | this.buffer[this.off + 8] & 0xFF;
        if (payloadLen > this.maxFramePayloadSize) {
            throw new H2ConnectionException(H2Error.FRAME_SIZE_ERROR, "Frame size exceeds maximum");
        }
        int frameLen = 9 + payloadLen;
        this.fillBuffer(inStream, frameLen);
        if ((flags & FrameFlag.PADDED.getValue()) > 0) {
            if (payloadLen == 0) {
                throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, "Inconsistent padding");
            }
            int padding = this.buffer[this.off + 9] & 0xFF;
            if (payloadLen < padding + 1) {
                throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, "Inconsistent padding");
            }
        }
        ByteBuffer payload = payloadLen > 0 ? ByteBuffer.wrap(this.buffer, this.off + 9, payloadLen) : null;
        RawFrame frame = new RawFrame(type, flags, streamId, payload);
        this.off += frameLen;
        this.dataLen -= frameLen;
        this.metrics.incrementFramesTransferred();
        return frame;
    }

    public H2TransportMetrics getMetrics() {
        return this.metrics;
    }
}

