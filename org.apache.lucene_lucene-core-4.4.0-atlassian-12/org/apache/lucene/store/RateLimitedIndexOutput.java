/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.IOException;
import org.apache.lucene.store.BufferedIndexOutput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.RateLimiter;

final class RateLimitedIndexOutput
extends BufferedIndexOutput {
    private final IndexOutput delegate;
    private final BufferedIndexOutput bufferedDelegate;
    private final RateLimiter rateLimiter;

    RateLimitedIndexOutput(RateLimiter rateLimiter, IndexOutput delegate) {
        if (delegate instanceof BufferedIndexOutput) {
            this.bufferedDelegate = (BufferedIndexOutput)delegate;
            this.delegate = delegate;
        } else {
            this.delegate = delegate;
            this.bufferedDelegate = null;
        }
        this.rateLimiter = rateLimiter;
    }

    @Override
    protected void flushBuffer(byte[] b, int offset, int len) throws IOException {
        this.rateLimiter.pause(len);
        if (this.bufferedDelegate != null) {
            this.bufferedDelegate.flushBuffer(b, offset, len);
        } else {
            this.delegate.writeBytes(b, offset, len);
        }
    }

    @Override
    public long length() throws IOException {
        return this.delegate.length();
    }

    @Override
    public void seek(long pos) throws IOException {
        this.flush();
        this.delegate.seek(pos);
    }

    @Override
    public void flush() throws IOException {
        try {
            super.flush();
        }
        finally {
            this.delegate.flush();
        }
    }

    @Override
    public void close() throws IOException {
        try {
            super.close();
        }
        finally {
            this.delegate.close();
        }
    }
}

