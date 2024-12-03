/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.SdkClientException;
import com.amazonaws.internal.SdkFilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class InputSubstream
extends SdkFilterInputStream {
    private static final int MAX_SKIPS = 100;
    private long currentPosition = 0L;
    private final long requestedOffset;
    private final long requestedLength;
    private final boolean closeSourceStream;
    private long markedPosition = 0L;

    public InputSubstream(InputStream in, long offset, long length, boolean closeSourceStream) {
        super(in);
        this.requestedLength = length;
        this.requestedOffset = offset;
        this.closeSourceStream = closeSourceStream;
    }

    @Override
    public int read() throws IOException {
        byte[] b = new byte[1];
        int bytesRead = this.read(b, 0, 1);
        if (bytesRead == -1) {
            return bytesRead;
        }
        return b[0];
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int count = 0;
        while (this.currentPosition < this.requestedOffset) {
            long skippedBytes = super.skip(this.requestedOffset - this.currentPosition);
            if (skippedBytes == 0L && ++count > 100) {
                throw new SdkClientException("Unable to position the currentPosition from " + this.currentPosition + " to " + this.requestedOffset);
            }
            this.currentPosition += skippedBytes;
        }
        long bytesRemaining = this.requestedLength + this.requestedOffset - this.currentPosition;
        if (bytesRemaining <= 0L) {
            return -1;
        }
        len = (int)Math.min((long)len, bytesRemaining);
        int bytesRead = super.read(b, off, len);
        this.currentPosition += (long)bytesRead;
        return bytesRead;
    }

    @Override
    public synchronized void mark(int readlimit) {
        this.markedPosition = this.currentPosition;
        super.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        this.currentPosition = this.markedPosition;
        super.reset();
    }

    @Override
    public void close() throws IOException {
        if (this.closeSourceStream) {
            super.close();
        }
    }

    @Override
    public int available() throws IOException {
        long bytesRemaining = this.currentPosition < this.requestedOffset ? this.requestedLength : this.requestedLength + this.requestedOffset - this.currentPosition;
        return (int)Math.min(bytesRemaining, (long)super.available());
    }

    InputStream getWrappedInputStream() {
        return this.in;
    }
}

