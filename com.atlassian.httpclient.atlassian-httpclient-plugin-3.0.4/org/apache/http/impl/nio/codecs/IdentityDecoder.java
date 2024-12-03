/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.codecs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.impl.nio.codecs.AbstractContentDecoder;
import org.apache.http.nio.FileContentDecoder;
import org.apache.http.nio.reactor.SessionInputBuffer;
import org.apache.http.util.Args;

public class IdentityDecoder
extends AbstractContentDecoder
implements FileContentDecoder {
    public IdentityDecoder(ReadableByteChannel channel, SessionInputBuffer buffer, HttpTransportMetricsImpl metrics) {
        super(channel, buffer, metrics);
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        Args.notNull(dst, "Byte buffer");
        if (this.isCompleted()) {
            return -1;
        }
        int bytesRead = this.buffer.hasData() ? this.buffer.read(dst) : this.readFromChannel(dst);
        if (bytesRead == -1) {
            this.setCompleted();
        }
        return bytesRead;
    }

    @Override
    public long transfer(FileChannel dst, long position, long count) throws IOException {
        long bytesRead;
        if (dst == null) {
            return 0L;
        }
        if (this.isCompleted()) {
            return 0L;
        }
        if (this.buffer.hasData()) {
            int maxLen = this.buffer.length();
            dst.position(position);
            bytesRead = this.buffer.read(dst, count < (long)maxLen ? (int)count : maxLen);
        } else {
            if (this.channel.isOpen()) {
                if (position > dst.size()) {
                    throw new IOException(String.format("Position past end of file [%,d > %,d]", position, dst.size()));
                }
                bytesRead = dst.transferFrom(this.channel, position, count);
                if (count > 0L && bytesRead == 0L) {
                    bytesRead = this.buffer.fill(this.channel);
                }
            } else {
                bytesRead = -1L;
            }
            if (bytesRead > 0L) {
                this.metrics.incrementBytesTransferred(bytesRead);
            }
        }
        if (bytesRead == -1L) {
            this.setCompleted();
        }
        return bytesRead;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[identity; completed: ");
        sb.append(this.completed);
        sb.append("]");
        return sb.toString();
    }
}

