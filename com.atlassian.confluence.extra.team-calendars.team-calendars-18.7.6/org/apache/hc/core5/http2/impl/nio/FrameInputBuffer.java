/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.impl.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
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
    private final byte[] bytes;
    private final ByteBuffer buffer;
    private State state;
    private int payloadLen;
    private int type;
    private int flags;
    private int streamId;

    FrameInputBuffer(BasicH2TransportMetrics metrics, int bufferLen, int maxFramePayloadSize) {
        Args.notNull(metrics, "HTTP2 transport metrics");
        Args.positive(maxFramePayloadSize, "Maximum payload size");
        this.metrics = metrics;
        this.maxFramePayloadSize = Math.max(maxFramePayloadSize, 16384);
        this.bytes = new byte[bufferLen];
        this.buffer = ByteBuffer.wrap(this.bytes);
        this.buffer.flip();
        this.state = State.HEAD_EXPECTED;
    }

    public FrameInputBuffer(BasicH2TransportMetrics metrics, int maxFramePayloadSize) {
        this(metrics, 9 + maxFramePayloadSize, maxFramePayloadSize);
    }

    public FrameInputBuffer(int maxFramePayloadSize) {
        this(new BasicH2TransportMetrics(), maxFramePayloadSize);
    }

    @Deprecated
    public void put(ByteBuffer src) {
        if (this.buffer.hasRemaining()) {
            this.buffer.compact();
        } else {
            this.buffer.clear();
        }
        this.buffer.put(src);
        this.buffer.flip();
    }

    public RawFrame read(ByteBuffer src, ReadableByteChannel channel) throws IOException {
        block18: {
            int bytesRead;
            do {
                if (src != null) {
                    if (this.buffer.hasRemaining()) {
                        this.buffer.compact();
                    } else {
                        this.buffer.clear();
                    }
                    int remaining = this.buffer.remaining();
                    int n = src.remaining();
                    if (remaining >= n) {
                        this.buffer.put(src);
                        this.metrics.incrementBytesTransferred(n);
                    } else {
                        int limit = src.limit();
                        src.limit(remaining);
                        this.buffer.put(src);
                        src.limit(limit);
                        this.metrics.incrementBytesTransferred(remaining);
                    }
                    this.buffer.flip();
                }
                switch (this.state) {
                    case HEAD_EXPECTED: {
                        if (this.buffer.remaining() < 9) break;
                        int lengthAndType = this.buffer.getInt();
                        this.payloadLen = lengthAndType >> 8;
                        if (this.payloadLen > this.maxFramePayloadSize) {
                            throw new H2ConnectionException(H2Error.FRAME_SIZE_ERROR, "Frame size exceeds maximum");
                        }
                        this.type = lengthAndType & 0xFF;
                        this.flags = this.buffer.get();
                        this.streamId = Math.abs(this.buffer.getInt());
                        this.state = State.PAYLOAD_EXPECTED;
                    }
                    case PAYLOAD_EXPECTED: {
                        if (this.buffer.remaining() < this.payloadLen) break;
                        if ((this.flags & FrameFlag.PADDED.getValue()) > 0) {
                            if (this.payloadLen == 0) {
                                throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, "Inconsistent padding");
                            }
                            this.buffer.mark();
                            byte padding = this.buffer.get();
                            if (this.payloadLen < padding + 1) {
                                throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, "Inconsistent padding");
                            }
                            this.buffer.reset();
                        }
                        ByteBuffer payload = this.payloadLen > 0 ? ByteBuffer.wrap(this.bytes, this.buffer.position(), this.payloadLen) : null;
                        this.buffer.position(this.buffer.position() + this.payloadLen);
                        this.state = State.HEAD_EXPECTED;
                        this.metrics.incrementFramesTransferred();
                        return new RawFrame(this.type, this.flags, this.streamId, payload);
                    }
                }
                if (this.buffer.hasRemaining()) {
                    this.buffer.compact();
                } else {
                    this.buffer.clear();
                }
                bytesRead = channel.read(this.buffer);
                this.buffer.flip();
                if (bytesRead > 0) {
                    this.metrics.incrementBytesTransferred(bytesRead);
                }
                if (bytesRead == 0) break block18;
            } while (bytesRead >= 0);
            if (this.state != State.HEAD_EXPECTED || this.buffer.hasRemaining()) {
                throw new H2CorruptFrameException("Corrupt or incomplete HTTP2 frame");
            }
            throw new ConnectionClosedException();
        }
        return null;
    }

    public RawFrame read(ReadableByteChannel channel) throws IOException {
        return this.read(null, channel);
    }

    public void reset() {
        this.buffer.compact();
        this.state = State.HEAD_EXPECTED;
    }

    public H2TransportMetrics getMetrics() {
        return this.metrics;
    }

    static enum State {
        HEAD_EXPECTED,
        PAYLOAD_EXPECTED;

    }
}

