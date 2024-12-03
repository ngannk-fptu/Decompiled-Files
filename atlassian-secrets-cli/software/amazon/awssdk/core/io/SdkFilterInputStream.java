/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.exception.AbortedException;
import software.amazon.awssdk.core.internal.io.Releasable;
import software.amazon.awssdk.utils.IoUtils;

@SdkProtectedApi
public class SdkFilterInputStream
extends FilterInputStream
implements Releasable {
    protected SdkFilterInputStream(InputStream in) {
        super(in);
    }

    protected final void abortIfNeeded() {
        if (Thread.currentThread().isInterrupted()) {
            this.abort();
            throw AbortedException.builder().build();
        }
    }

    protected void abort() {
    }

    @Override
    public int read() throws IOException {
        this.abortIfNeeded();
        return this.in.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        this.abortIfNeeded();
        return this.in.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        this.abortIfNeeded();
        return this.in.skip(n);
    }

    @Override
    public int available() throws IOException {
        this.abortIfNeeded();
        return this.in.available();
    }

    @Override
    public void close() throws IOException {
        this.in.close();
        this.abortIfNeeded();
    }

    @Override
    public synchronized void mark(int readlimit) {
        this.abortIfNeeded();
        this.in.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        this.abortIfNeeded();
        this.in.reset();
    }

    @Override
    public boolean markSupported() {
        this.abortIfNeeded();
        return this.in.markSupported();
    }

    @Override
    public void release() {
        IoUtils.closeQuietly(this, null);
        if (this.in instanceof Releasable) {
            Releasable r = (Releasable)((Object)this.in);
            r.release();
        }
    }
}

