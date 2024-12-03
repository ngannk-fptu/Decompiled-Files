/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.fileupload.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.fileupload.util.Closeable;

public abstract class LimitedInputStream
extends FilterInputStream
implements Closeable {
    private final long sizeMax;
    private long count;
    private boolean closed;

    public LimitedInputStream(InputStream inputStream, long pSizeMax) {
        super(inputStream);
        this.sizeMax = pSizeMax;
    }

    protected abstract void raiseError(long var1, long var3) throws IOException;

    private void checkLimit() throws IOException {
        if (this.count > this.sizeMax) {
            this.raiseError(this.sizeMax, this.count);
        }
    }

    @Override
    public int read() throws IOException {
        int res = super.read();
        if (res != -1) {
            ++this.count;
            this.checkLimit();
        }
        return res;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int res = super.read(b, off, len);
        if (res > 0) {
            this.count += (long)res;
            this.checkLimit();
        }
        return res;
    }

    @Override
    public boolean isClosed() throws IOException {
        return this.closed;
    }

    @Override
    public void close() throws IOException {
        this.closed = true;
        super.close();
    }
}

