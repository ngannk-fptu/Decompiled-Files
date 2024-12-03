/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.util;

import java.io.IOException;
import java.io.InputStream;

public class MultiInputStream
extends InputStream {
    private final InputStream[] inputStreams;
    private final int inputStreamCount;
    private int inputStreamIndex;

    public MultiInputStream(InputStream ... inputStreams) {
        this.inputStreams = inputStreams;
        this.inputStreamCount = inputStreams.length;
    }

    @Override
    public int read() throws IOException {
        for (int i = this.inputStreamIndex; i < this.inputStreamCount; ++i) {
            int b = this.inputStreams[i].read();
            if (b >= 0) {
                return b;
            }
            ++this.inputStreamIndex;
        }
        return -1;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        for (int i = this.inputStreamIndex; i < this.inputStreamCount; ++i) {
            int read = this.inputStreams[i].read(b, off, len);
            if (read >= 0) {
                return read;
            }
            ++this.inputStreamIndex;
        }
        return -1;
    }

    @Override
    public long skip(long n) throws IOException {
        throw new IOException("skip() not supported");
    }

    @Override
    public int available() throws IOException {
        if (this.inputStreamIndex < this.inputStreamCount) {
            return this.inputStreams[this.inputStreamIndex].available();
        }
        return 0;
    }

    @Override
    public void close() throws IOException {
        for (int i = 0; i < this.inputStreamCount; ++i) {
            try {
                this.inputStreams[i].close();
                continue;
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }
}

