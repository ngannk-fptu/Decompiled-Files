/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.attachments;

import java.io.IOException;
import java.io.InputStream;
import org.apache.axiom.attachments.PartImpl;

class ReadOnceInputStreamWrapper
extends InputStream {
    private final PartImpl part;
    private InputStream in;

    ReadOnceInputStreamWrapper(PartImpl part, InputStream in) {
        this.part = part;
        this.in = in;
    }

    public int available() throws IOException {
        return this.in == null ? 0 : this.in.available();
    }

    public int read() throws IOException {
        if (this.in == null) {
            return -1;
        }
        int result = this.in.read();
        if (result == -1) {
            this.close();
        }
        return result;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (this.in == null) {
            return -1;
        }
        int result = this.in.read(b, off, len);
        if (result == -1) {
            this.close();
        }
        return result;
    }

    public int read(byte[] b) throws IOException {
        if (this.in == null) {
            return -1;
        }
        int result = this.in.read(b);
        if (result == -1) {
            this.close();
        }
        return result;
    }

    public long skip(long n) throws IOException {
        return this.in == null ? 0L : this.in.skip(n);
    }

    public void close() throws IOException {
        if (this.in != null) {
            this.in.close();
            this.part.releaseContent();
            this.in = null;
        }
    }
}

