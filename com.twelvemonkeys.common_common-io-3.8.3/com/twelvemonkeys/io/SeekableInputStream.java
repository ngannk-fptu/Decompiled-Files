/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.io;

import com.twelvemonkeys.io.Seekable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

public abstract class SeekableInputStream
extends InputStream
implements Seekable {
    long position;
    long flushedPosition;
    boolean closed;
    protected Stack<Long> markedPositions = new Stack();

    @Override
    public final int read(byte[] byArray) throws IOException {
        return this.read(byArray, 0, byArray != null ? byArray.length : 1);
    }

    @Override
    public final long skip(long l) throws IOException {
        long l2 = this.position;
        long l3 = l2 + l;
        if (l3 < this.flushedPosition) {
            throw new IOException("position < flushedPosition");
        }
        int n = this.available();
        if (n > 0) {
            this.seek(Math.min(l3, l2 + (long)n));
        } else {
            for (int i = (int)Math.max(Math.min(l, 512L), -512L); i > 0 && this.read() >= 0; --i) {
            }
        }
        return this.position - l2;
    }

    @Override
    public final void mark(int n) {
        this.mark();
        try {
            this.flushBefore(Math.max(this.position - (long)n, this.flushedPosition));
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    @Override
    public final boolean markSupported() {
        return true;
    }

    @Override
    public final void seek(long l) throws IOException {
        this.checkOpen();
        if (l < this.flushedPosition) {
            throw new IndexOutOfBoundsException("position < flushedPosition");
        }
        this.seekImpl(l);
        this.position = l;
    }

    protected abstract void seekImpl(long var1) throws IOException;

    @Override
    public final void mark() {
        this.markedPositions.push(this.position);
    }

    @Override
    public final void reset() throws IOException {
        this.checkOpen();
        if (!this.markedPositions.isEmpty()) {
            long l = this.markedPositions.pop();
            if (l < this.flushedPosition) {
                throw new IOException("Previous marked position has been discarded");
            }
            this.seek(l);
        } else {
            this.seek(0L);
        }
    }

    @Override
    public final void flushBefore(long l) throws IOException {
        if (l < this.flushedPosition) {
            throw new IndexOutOfBoundsException("position < flushedPosition");
        }
        if (l > this.getStreamPosition()) {
            throw new IndexOutOfBoundsException("position > stream position");
        }
        this.checkOpen();
        this.flushBeforeImpl(l);
        this.flushedPosition = l;
    }

    protected abstract void flushBeforeImpl(long var1) throws IOException;

    @Override
    public final void flush() throws IOException {
        this.flushBefore(this.flushedPosition);
    }

    @Override
    public final long getFlushedPosition() throws IOException {
        this.checkOpen();
        return this.flushedPosition;
    }

    @Override
    public final long getStreamPosition() throws IOException {
        this.checkOpen();
        return this.position;
    }

    protected final void checkOpen() throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
    }

    @Override
    public final void close() throws IOException {
        this.checkOpen();
        this.closed = true;
        this.closeImpl();
    }

    protected abstract void closeImpl() throws IOException;

    protected void finalize() throws Throwable {
        if (!this.closed) {
            try {
                this.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        super.finalize();
    }
}

