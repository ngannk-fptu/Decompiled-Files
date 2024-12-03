/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.utils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public class LookaheadInputStream
extends FilterInputStream {
    private Integer next;
    private Integer nextAtMark;

    public LookaheadInputStream(InputStream in) {
        super(in);
    }

    public int peek() throws IOException {
        if (this.next == null) {
            this.next = this.read();
        }
        return this.next;
    }

    @Override
    public int read() throws IOException {
        if (this.next == null) {
            return super.read();
        }
        Integer next = this.next;
        this.next = null;
        return next;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.next == null) {
            return super.read(b, off, len);
        }
        if (len == 0) {
            return 0;
        }
        if (this.next == -1) {
            return -1;
        }
        b[off] = (byte)this.next.intValue();
        this.next = null;
        if (len == 1) {
            return 1;
        }
        return super.read(b, off + 1, b.length - 1) + 1;
    }

    @Override
    public long skip(long n) throws IOException {
        if (this.next == null) {
            return super.skip(n);
        }
        if (n == 0L) {
            return 0L;
        }
        if (this.next == -1) {
            return 0L;
        }
        this.next = null;
        if (n == 1L) {
            return 1L;
        }
        return super.skip(n - 1L);
    }

    @Override
    public int available() throws IOException {
        if (this.next == null) {
            return super.available();
        }
        return super.available() + 1;
    }

    @Override
    public synchronized void mark(int readlimit) {
        if (this.next == null) {
            super.mark(readlimit);
        } else {
            this.nextAtMark = this.next;
            super.mark(readlimit - 1);
        }
    }

    @Override
    public synchronized void reset() throws IOException {
        this.next = this.nextAtMark;
        super.reset();
    }
}

