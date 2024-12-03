/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.codecs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import org.apache.http.ConnectionClosedException;
import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.impl.nio.codecs.AbstractContentDecoder;
import org.apache.http.nio.FileContentDecoder;
import org.apache.http.nio.reactor.SessionInputBuffer;
import org.apache.http.util.Args;

public class LengthDelimitedDecoder
extends AbstractContentDecoder
implements FileContentDecoder {
    private final long contentLength;
    private long len;

    public LengthDelimitedDecoder(ReadableByteChannel channel, SessionInputBuffer buffer, HttpTransportMetricsImpl metrics, long contentLength) {
        super(channel, buffer, metrics);
        Args.notNegative(contentLength, "Content length");
        this.contentLength = contentLength;
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        int bytesRead;
        Args.notNull(dst, "Byte buffer");
        if (this.isCompleted()) {
            return -1;
        }
        int chunk = (int)Math.min(this.contentLength - this.len, Integer.MAX_VALUE);
        if (this.buffer.hasData()) {
            int maxLen = Math.min(chunk, this.buffer.length());
            bytesRead = this.buffer.read(dst, maxLen);
        } else {
            bytesRead = this.readFromChannel(dst, chunk);
        }
        if (bytesRead == -1) {
            this.setCompleted();
            if (this.len < this.contentLength) {
                throw new ConnectionClosedException("Premature end of Content-Length delimited message body (expected: %,d; received: %,d)", this.contentLength, this.len);
            }
        }
        this.len += (long)bytesRead;
        if (this.len >= this.contentLength) {
            this.setCompleted();
        }
        return this.isCompleted() && bytesRead == 0 ? -1 : bytesRead;
    }

    @Override
    public long transfer(FileChannel dst, long position, long count) throws IOException {
        long bytesRead;
        if (dst == null) {
            return 0L;
        }
        if (this.isCompleted()) {
            return -1L;
        }
        int chunk = (int)Math.min(this.contentLength - this.len, Integer.MAX_VALUE);
        if (this.buffer.hasData()) {
            int maxLen = Math.min(chunk, this.buffer.length());
            dst.position(position);
            bytesRead = this.buffer.read(dst, count < (long)maxLen ? (int)count : maxLen);
        } else {
            if (this.channel.isOpen()) {
                if (position > dst.size()) {
                    throw new IOException(String.format("Position past end of file [%,d > %,d]", position, dst.size()));
                }
                bytesRead = dst.transferFrom(this.channel, position, count < (long)chunk ? count : (long)chunk);
            } else {
                bytesRead = -1L;
            }
            if (bytesRead > 0L) {
                this.metrics.incrementBytesTransferred(bytesRead);
            }
        }
        if (bytesRead == -1L) {
            this.setCompleted();
            if (this.len < this.contentLength) {
                throw new ConnectionClosedException("Premature end of Content-Length delimited message body (expected: %,d; received: %,d)", this.contentLength, this.len);
            }
        }
        this.len += bytesRead;
        if (this.len >= this.contentLength) {
            this.setCompleted();
        }
        return bytesRead;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[content length: ");
        sb.append(this.contentLength);
        sb.append("; pos: ");
        sb.append(this.len);
        sb.append("; completed: ");
        sb.append(this.completed);
        sb.append("]");
        return sb.toString();
    }
}

