/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.core5.http2.impl.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.WritableByteChannel;
import org.apache.hc.core5.http2.H2TransportMetrics;
import org.apache.hc.core5.http2.frame.RawFrame;
import org.apache.hc.core5.http2.impl.BasicH2TransportMetrics;
import org.apache.hc.core5.util.Args;

public final class FrameOutputBuffer {
    private final BasicH2TransportMetrics metrics;
    private volatile int maxFramePayloadSize;
    private volatile ByteBuffer buffer;

    public FrameOutputBuffer(BasicH2TransportMetrics metrics, int maxFramePayloadSize) {
        Args.notNull((Object)metrics, (String)"HTTP2 transport metrics");
        Args.positive((int)maxFramePayloadSize, (String)"Maximum payload size");
        this.metrics = metrics;
        this.maxFramePayloadSize = maxFramePayloadSize;
        this.buffer = ByteBuffer.allocate(9 + maxFramePayloadSize);
    }

    public FrameOutputBuffer(int maxFramePayloadSize) {
        this(new BasicH2TransportMetrics(), maxFramePayloadSize);
    }

    public void expand(int maxFramePayloadSize) {
        this.maxFramePayloadSize = maxFramePayloadSize;
        ByteBuffer newBuffer = ByteBuffer.allocate(9 + maxFramePayloadSize);
        if (this.buffer.position() > 0) {
            this.buffer.flip();
            newBuffer.put(this.buffer);
        }
        this.buffer = newBuffer;
    }

    public void write(RawFrame frame, WritableByteChannel channel) throws IOException {
        Args.notNull((Object)frame, (String)"Frame");
        ByteBuffer payload = frame.getPayload();
        Args.check((payload == null || payload.remaining() <= this.maxFramePayloadSize ? 1 : 0) != 0, (String)"Frame size exceeds maximum");
        this.buffer.putInt((payload != null ? payload.remaining() << 8 : 0) | frame.getType() & 0xFF);
        this.buffer.put((byte)(frame.getFlags() & 0xFF));
        this.buffer.putInt(frame.getStreamId());
        if (payload != null) {
            if (channel instanceof GatheringByteChannel) {
                this.buffer.flip();
                ((GatheringByteChannel)channel).write(new ByteBuffer[]{this.buffer, payload});
                this.buffer.compact();
                if (payload.hasRemaining()) {
                    this.buffer.put(payload);
                }
            } else {
                this.buffer.put(payload);
            }
        }
        this.flush(channel);
        this.metrics.incrementFramesTransferred();
    }

    public void flush(WritableByteChannel channel) throws IOException {
        if (this.buffer.position() > 0) {
            this.buffer.flip();
            try {
                int bytesWritten = channel.write(this.buffer);
                if (bytesWritten > 0) {
                    this.metrics.incrementBytesTransferred(bytesWritten);
                }
            }
            finally {
                this.buffer.compact();
            }
        }
    }

    public boolean isEmpty() {
        return this.buffer.position() == 0;
    }

    public H2TransportMetrics getMetrics() {
        return this.metrics;
    }
}

