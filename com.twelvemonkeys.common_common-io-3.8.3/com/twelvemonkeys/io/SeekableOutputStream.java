/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.io;

import com.twelvemonkeys.io.Seekable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Stack;

public abstract class SeekableOutputStream
extends OutputStream
implements Seekable {
    long position;
    long flushedPosition;
    boolean closed;
    protected Stack<Long> markedPositions = new Stack();

    @Override
    public final void write(byte[] byArray) throws IOException {
        this.write(byArray, 0, byArray != null ? byArray.length : 1);
    }

    @Override
    public final void seek(long l) throws IOException {
        this.checkOpen();
        if (l < this.flushedPosition) {
            throw new IndexOutOfBoundsException("position < flushedPosition!");
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
                throw new IOException("Previous marked position has been discarded!");
            }
            this.seek(l);
        }
    }

    @Override
    public final void flushBefore(long l) throws IOException {
        if (l < this.flushedPosition) {
            throw new IndexOutOfBoundsException("position < flushedPosition!");
        }
        if (l > this.getStreamPosition()) {
            throw new IndexOutOfBoundsException("position > getStreamPosition()!");
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
}

